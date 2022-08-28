#!/bin/bash

hostnamectl set-hostname k8s-master

cat >> /etc/hosts << EOF 
10.0.4.3 k8s-master
EOF


# close firewalld
systemctl stop firewalld  
systemctl stop dnsmasq
systemctl disable --now firewalld  
systemctl disable --now dnsmasq


#systemctl disable --now NetworkManager (Centos7需要关闭，centos8不需要)

# close selinux
setenforce 0
sed -i 's#SELINUX=enforcing#SELINUX=disabled#g' /etc/sysconfig/selinux
sed -i 's#SELINUX=enforcing#SELINUX=disabled#g' /etc/selinux/config

# close swap
swapoff -a
sysctl -w vm.swappiness=0
sed -ri '/^[^#]*swap/s@^@#@' /etc/fstab

cat <<EOF | sudo tee /etc/modules-load.d/k8s.conf
overlay
br_netfilter
EOF

modprobe overlay
modprobe br_netfilter

# sysctl params required by setup, params persist across reboots
cat <<EOF | sudo tee /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-iptables  = 1
net.bridge.bridge-nf-call-ip6tables = 1
net.ipv4.ip_forward                 = 1
EOF

# Apply sysctl params without reboot
sysctl --system

# insatll ntpdate sync time
rpm -ivh http://mirrors.wlnmp.com/centos/wlnmp-release-centos.noarch.rpm
dnf install wntp -y 
ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
echo 'Asia/Shanghai' > /etc/timezone
ntpdate ntp3.aliyun.com 
crontab -l > conf && echo "*/5 * * * * ntpdate ntp3.aliyun.com" >> conf && crontab conf && rm -f conf

# config limit
ulimit -SHn 65535

cat <<EOF | tee -a /etc/security/limits.conf
* soft nofile 655360
* hard nofile 131072
* soft nproc 655350
* hard nproc 655350
* soft memlock unlimited
* hard memlock unlimited
EOF


# install docker 
echo "================================insatll docker start=========================================================="
rm -rf /etc/yum.repos.d/*

wget -O /etc/yum.repos.d/CentOS-Base.repo https://mirrors.aliyun.com/repo/Centos-vault-8.5.2111.repo

wget -O /etc/yum.repos.d/docker-ce.repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo

yum clean all && yum makecache

yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine

yum -y erase podman buildah

yum install -y wget jq psmisc vim net-tools telnet yum-utils device-mapper-persistent-data lvm2 git

yum install -y socat conntrack iproute-tc

yum install -y docker-ce docker-ce-cli containerd.io --nogpgcheck

mkdir -p /etc/docker

cat <<EOF | tee /etc/docker/daemon.json
{
    "exec-opts": ["native.cgroupdriver=systemd"],
    "log-driver": "json-file",
    "log-opts": {
        "max-size": "100m"
    },
    "storage-driver": "overlay2",
    "registry-mirrors": ["https://mirror.ccs.tencentyun.com"]
}
EOF

systemctl daemon-reload
systemctl restart docker

echo "================================insatll docker end=========================================================="


echo "================================insatll kubeadm start=========================================================="

# install kubeadm
# install cri-dockerd
rpm -ivh cri-dockerd-0.2.5-3.el8.x86_64.rpm
sed -i 's#ExecStart=/usr/bin/cri-dockerd#ExecStart=/usr/bin/cri-dockerd --container-runtime-endpoint fd:// --network-plugin=cni --pod-infra-container-image=registry.aliyuncs.com/google_containers/pause:3.7#g' /usr/lib/systemd/system/cri-docker.service
systemctl start cri-docker.service

# docker load iamges
docker load -i coredns-v1.8.6.tar.gz
docker load -i kube-controller-manager-v1.24.4.tar.gz
docker load -i pause-3.7.tar.gz
docker load -i etcd-3.5.3-0.tar.gz
docker load -i kube-proxy-v1.24.4.tar.gz
docker load -i kube-apiserver-v1.24.4.tar.gz
docker load -i kube-scheduler-v1.24.4.tar.gz


# Install CNI plugins (required for most pod network)
mkdir -p /opt/cni/bin
tar xf cni-plugins-linux-amd64-v0.8.2.tgz -C /opt/cni/bin 

# stall crictl (required for kubeadm / Kubelet Container Runtime Interface (CRI))
mkdir -p /usr/local/bin
tar xf crictl-v1.22.0-linux-amd64.tar.gz -C /usr/local/bin 


chmod +x {kubeadm,kubelet,kubectl}

mv kubeadm kubelet kubectl /usr/local/bin/

cat << EOF | tee /etc/systemd/system/kubelet.service
[Unit]
Description=kubelet: The Kubernetes Node Agent
Documentation=https://kubernetes.io/docs/home/
Wants=network-online.target
After=network-online.target

[Service]  
ExecStart=/usr/local/bin/kubelet --container-runtime=remote --container-runtime-endpoint=unix:///var/run/cri-dockerd.sock
Restart=always
StartLimitInterval=0
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

mkdir -p /etc/systemd/system/kubelet.service.d

cat << EOF | tee /etc/systemd/system/kubelet.service.d/10-kubeadm.conf
# Note: This dropin only works with kubeadm and kubelet v1.11+
[Service]
Environment="KUBELET_KUBECONFIG_ARGS=--bootstrap-kubeconfig=/etc/kubernetes/bootstrap-kubelet.conf --kubeconfig=/etc/kubernetes/kubelet.conf"
Environment="KUBELET_CONFIG_ARGS=--config=/var/lib/kubelet/config.yaml"
# This is a file that "kubeadm init" and "kubeadm join" generates at runtime, populating the KUBELET_KUBEADM_ARGS variable dynamically
EnvironmentFile=-/var/lib/kubelet/kubeadm-flags.env
# This is a file that the user can use for overrides of the kubelet args as a last resort. Preferably, the user should use
# the .NodeRegistration.KubeletExtraArgs object in the configuration files instead. KUBELET_EXTRA_ARGS should be sourced from this file.
EnvironmentFile=-/etc/default/kubelet
ExecStart=
#$变量在eof中会被执行，所以需要转义
ExecStart=/usr/local/bin/kubelet \$KUBELET_KUBECONFIG_ARGS \$KUBELET_CONFIG_ARGS \$KUBELET_KUBEADM_ARGS \$KUBELET_EXTRA_ARGS
EOF


systemctl enable --now kubelet
systemctl start --now kubelet

echo "================================ insatll kubeadm end =========================================================="


rm -rf /etc/containerd/config.toml
systemctl restart containerd


echo "================================ kubeadm init start =========================================================="
# install 
# calico --pod-network-cidr=192.168.0.0/16
# service-cidr 的选取不能和PodCIDR及本机网络有重叠或者冲突。 
#一般可以选择一个本机网络和PodCIDR都没有用到的私网地址段
#比如PODCIDR使用192.168.0.1/16, 那么service cidr可以选择172.16.0.1/20. 主机网段可以选10.1.0.1/8. 三者之间网络无重叠冲突即可
kubeadm init --cri-socket='unix:///var/run/cri-dockerd.sock' \
--apiserver-advertise-address=10.0.4.3 \
--image-repository=registry.aliyuncs.com/google_containers \
--service-cidr=172.16.0.0/12 \
--pod-network-cidr=192.168.0.0/16

# config kubectl
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
echo "================================ kubeadm init end =========================================================="


#kubeadm join 10.0.4.3:6443 --token lo9z4m.bpmkldela5lmigei \
#        --discovery-token-ca-cert-hash sha256:677123d92d5bf065fcde1e8f660f0242539000703f811e328a6b9ba0d40320e4

# install calico
echo "================================ install calico start =========================================================="

cat <<EOF | tee /etc/NetworkManager/conf.d/calico.conf
[keyfile]
unmanaged-devices=interface-name:cali*;interface-name:tunl*;interface-name:vxlan.calico;interface-name:vxlan-v6.calico;interface-name:wireguard.cali;interface-name:wg-v6.cali
EOF

sysctl -w net.netfilter.nf_conntrack_max=1000000
echo "net.netfilter.nf_conntrack_max=1000000" >> /etc/sysctl.conf


#给master去掉标记，用于单机环境，这样coredns就可以安装，calico也可以安装了
kubectl taint nodes --all node-role.kubernetes.io/control-plane- node-role.kubernetes.io/master-
kubectl create -f tigera-operator.yaml
kubectl create -f custom-resources.yaml

chmod +x calicoctl-linux-amd64
mv calicoctl-linux-amd64 /usr/local/bin/calicoctl

echo "================================ install calico end =========================================================="



