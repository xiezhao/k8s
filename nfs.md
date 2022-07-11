###1. 安装nfs
    ```shell
    1. 安装nfs
	yum install -y nfs-utils
	# 创建nfs目录
	mkdir -pv /nfs/data/
	# 授予权限
	chmod -R 777 /nfs/data
	# 编辑export文件
	vi /etc/exports
	  /nfs/data *(rw,no_root_squash,sync)
	# 使得配置生效
	exportfs -r
	# 查看生效
	exportfs
	# 启动rpcbind、nfs服务
	systemctl restart rpcbind && systemctl enable rpcbind
	systemctl restart nfs-server.service && systemctl enable nfs-server.service
	# 查看rpc服务的注册情况
	rpcinfo -p localhost
	# showmount测试
	showmount -e localhost

    2. 所有node上安装客户端
	yum install -y nfs-utils rpcbind 
    ```

###2 客户端创建，并尝试挂载验证是否成功
    yum install -y nfs-utils rpcbind
    1，查看rpcbind、nfs是否安装
    安装命令：yum install -y nfs-utils rpcbind

    2，挂载
    挂载命令：mount -t nfs 10.211.55.12:/nfs/data /root/data

    3，取消挂载
    查看占用命令：fuser -mv /root/data
    杀死占用命令：fuser -kv /root/data
    取消挂载命令：umount -a


    如果mount卡住不动，要把防火墙关了
    systemctl stop firewalld.service
    
    