### Docker安装


```
    yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine

    yum install -y yum-utils

    yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo


    yum install docker-ce docker-ce-cli containerd.io --nogpgcheck

    systemctl start docker
```



### minikube安装
```
下载minikube安装包

curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64

sudo install minikube-linux-amd64 /usr/local/bin/minikube

或者直接下载一个rpm包，安装

启动minikube 
    minikube start


报错：
X Exiting due to DRV_AS_ROOT: The "docker" driver should not be used with root privileges
解决：
    useradd minikube
    usermod -aG docker minikube && newgrp docker

    su minikube 去执行 minikube start



安装kubectl：
    curl -LO https://storage.googleapis.com/kubernetes-release/release/v1.18.0/bin/linux/amd64/kubectl
    chmod +x ./kubectl
    sudo mv ./kubectl /usr/local/bin/kubectl
    kubectl version --client

```
