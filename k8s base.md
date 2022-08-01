

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