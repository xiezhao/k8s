    
![](./images/nfs-pv-pvc.png)


*** 
<font color="#660000" size=4>注意创建pv，pvc时一定要带上storageClassName，否则创建后的pvc不会绑定到pv上</font>


###1. 创建pv
```yaml
apiVersion: v1
kind: PersistentVolume
metadata:
  name: nginx-pv-1g
  namespace: test
spec:
  accessModes:
    - ReadWriteMany
  capacity:
    storage: 1Gi
  storageClassName: nfs
  nfs:
    path: /nfs/data/nginx
    server: 10.211.55.12

---

apiVersion: v1
kind: PersistentVolume
metadata:
  name: nginx-pv-2g
  namespace: test
spec:
  accessModes:
    - ReadWriteMany
  capacity:
    storage: 2Gi
  storageClassName: nfs
  nfs:
    path: /nfs/data/nginx-2g
    server: 10.211.55.12
```

###2. 创建pvc
```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: nginx-pvc-2g
  namespace: test
spec:
  accessModes:
    - ReadWriteMany
  resources:
    requests:
      storage: 2Gi
  storageClassName: nfs
```

###3. 创建deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx
  namespace: test
spec:
  replicas: 2
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - image: nginx
        name: nginx-pvc
        ports:
        - containerPort: 80
        volumeMounts:
        - name: nginx-persistent-storage
          mountPath: /usr/share/nginx/html
      volumes:
      - name: nginx-persistent-storage
        persistentVolumeClaim:
          claimName: nginx-pvc-2g
```


```console
[minikube@localhost ~]$ kubectl get pv -n test
NAME          CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS      CLAIM               STORAGECLASS   REASON   AGE
nginx-pv-1g   1Gi        RWX            Retain           Available                       nfs                     35s
nginx-pv-2g   2Gi        RWX            Retain           Bound       test/nginx-pvc-2g   nfs                     35s

[minikube@localhost ~]$ kubectl get pvc -n test
NAME           STATUS   VOLUME        CAPACITY   ACCESS MODES   STORAGECLASS   AGE
nginx-pvc-2g   Bound    nginx-pv-2g   2Gi        RWX            nfs            18s
```
可以看到 nginx-pv-2g 绑定了pvc -> test/nginx-pvc-2g 



```
[minikube@localhost ~]$ kubectl exec -ti nginx-f476d5c74-hdcsv -n test sh
kubectl exec [POD] [COMMAND] is DEPRECATED and will be removed in a future version. Use kubectl kubectl exec [POD] -- [COMMAND] instead.
# ls /usr/share/nginx/html
index.html
# curl localhost
nginx-2g
```
可以看到已经挂载了nfs里面的nginx-2g目录下的文件