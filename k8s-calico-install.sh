
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
