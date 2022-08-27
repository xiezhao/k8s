 1. 关闭selinux和iptables

 # vi /etc/selinux/config

 SELINUX=disabled
 # SELINUXTYPE=targeted

systemctl stop firewalld.service
systemctl disable firewalld.service



dockerfile：
    VOLUME /data    
        #容器里面创建一个挂载点，如果没有在启动的时候指定挂载 -v(把本地目录挂载进去)，也会在docker目录中
        自动创建一个  /var/lib/docker/volumnes/ 下创建一个目录挂载到容器里面

制作小镜像：
    不要用centos
    alpine  busybox

    多阶段制作：
        正常一个带有go环境的slim包，打包下来200多。
        思路：
            先用go环境的slim包打成二进制文件，在用到 aipine系统中
        
            FROM golang:1.14.4-alpine as go-builder-stage
            WORKDIR /opt
            COPY main.go /opt
            RUN go build /opt/main.go
            CMD "./main"

            FROM alpine:3.8
            COPY --from=go-builder-stage /opt/main /
            CMD "main"



scratch镜像：






k8s会在 template里面的东西修改的时候去创建新的 replicationSet

一般发版使用set命令： kubectl set image deploy nginx nginx=nginx:1.15.4 --record

 回滚：
    查看历史记录：
        kubectl rollout history deploy nginx
    回滚到上一个版本：
        kubectl rollout undo deploy nginx 
    回滚到指定版本：
        查看指定版本的详细信息
        kubectl rollout history deploy nginx --revision=5
        回滚到指定的版本
        kubectl rollout  undo deploy nginx --to-revision=5

扩容：
    kubectl scale --replicas=3 -f foo.yaml
    kubectl scale --replicas=3 deploy nginx
    kubectl scale --replicas=3 sts web

HPA:
    Horizontal pod Autoscaler: Pod的水平自动伸缩器
    观察pod的cpu，内存使用率自动扩展或缩容pod的数量
    不适用于无法缩放的对象，比如 DaemonSet
    只支持cpu，内存


deployment暂停：
    多次修改，一次更新 
    kubectl rollout pause deployment nginx



StatefulSet：
    更新策略：
    updateStrategy
        rollingUpdate:  # type为rollingUpdate时存在，onDelete时直接删除
            partition: 0    #启动5个pod，设置2，为保留小于2的
        type: OnDelete # 删除pod后才会更新   rollingUpdate 滚动更新

    级联删除和非级联删除：
        级联删除: 删除sts时同时删除pod（默认）
        非级联删除：删除sts时不删除pod
            kubectl delete sts web --cascade=false , 此时pod为孤儿pod，删除后不会再重建


DaemonSet:
    缩写ds，在所有节点或者是匹配的节点上部署一个Pod
    更新建议用 OnDelete

打标签：
    kubectl label node k8s-node01 k8s-node02 ds=true 




k8s探针：
    LivenessProbe:
        判断容器状态是否存活或者健康，不健康则kubelet杀掉该容器，并根据容器的重启策略做相应的处理。
    ReadinessProbe:
        判断容器是否启动完成，可以接收请求。
        探测失败，则pod的状态被修改，Endpoint Controller将从Service的Endpoint中删除包含该容器所在的pod的Endpoint
    StartupProbe:
        指示容器中的应用是否已经启动，如果提供了startup probe，则禁用其他所有其他探针，直到它成功为止。
        启动探针失败，kubelet杀掉该容器，并根据容器的重启策略做相应的处理。


如何组合使用？
1. 启动探针 startupProbe。 
startupProbe:
    httpGet:
        path: /doc.html
        port: 40017
    initialDelaySeconds: 10
    failureThreshold: 10
    periodSeconds: 5

    容器启动10秒后，startupProbe首先检测，应用程序最多有50秒(10次 * 5s = 50s)完成启动。
    一旦startupProbe成功一次，livenessProbe将接管，以对后续运行过程中容器死锁提供快速响应。
    如果startupProbe从未成功，则容器将在50s后被杀死

2. 就绪探针 ReadinessProbe
    验证容器内的服务可以正常提供服务
readinessProbe:
    httpGet:
        path: /doc.html
        port: 40017
    initialDelaySeconds: 10
    failureThreshold: 3
    periodSeconds: 5 

    ReadinessProbe会确保服务在你指定的探测命令执行成功的情况下才开始接受请求。

3. 存活探针 LivenessProbe 来对服务的长时间健康检测
livenessProbe:
    httpGet:
        path: /doc.html
        port: 40017
    failureThreshold: 2
    periodSeconds: 5 

    livenessProbe的设计是为了在pod启动成功后进行健康探测



k8s钩子：
    PostStart： 创建容器之后立即执行。不能保证钩子会在容器入口点之前执行。
    PreStop: 容器终止之前是否立即调用此钩子

    动作：
        exec - 执行一个特定的命令，在容器的cgroups和名称空间中

        lifecycle:
            postStart：
                exec:
                    command: ["/bin/sh", "-c", "echo 1"]
            preStop：
                exec:
                    command: ["/bin/sh", "-c", "echo 1"]


    containers:
        - name: lifecycle
          image: nginx
          lifecycle:
            preStop：
                httpGet:
                    path: /preStop
                    port: 8000
                    scheme: HTTP    






service：
    类型：
        ClusterIP: 集群内部使用
        ExternalName： 通过返回定义的CNAME别名
        NodePort： 在所有安装了 kube-proxy的节点上打开一个端口，此端口可以代理至后端Pod。
            然后集群外部可以使用节点的ip地址合NodePort的端口号访问到集群pod的服务



    可以反代k8s集群外部服务。
        让k8s去访问service服务，而不是集群外的ip地址或者域名
        例如，在部分程序迁移至k8s集群时，想访问外面的mq，redis，mysql等中间件时
        1. 手动创建service
        2. 手动创建endpoint
        #service.yaml

            apiVersion: v1
            kind: Service
            metadata:
              labels: 
                app: nginx-svc-external
              name: nginx-svc-external
            sepc:
              ports:
                - name: http
                  port: 80
                  protocol: TCP
                  targetPort: 80
              sessionAffinity: None
              type: ClusterIP

        #endpoint.yaml

            apiVersion: v1
            kind: Endpoint
            metadata:
              labels:
                app: nginx-svc-external
              name: nginx-svc-external
              namespace: default
            subsets:
              - addresses:
                - ip: 10.244.195.32
                ports:
                - name: http
                  port: 80
                  protocol: TCP


    反代k8s集群外的域名。
        1. 手动创建service
        #nginx-externalname.yaml

            apiVersion: v1
            kind: Service
            metadata:
              labels:
                app: nginx-externalname
                name: nginx-externalname
            sepc:
              type: ExternalName
              externalName: www.baidu.com






        



















