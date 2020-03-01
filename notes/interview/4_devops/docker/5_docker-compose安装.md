# docker-comppse 安装

###  官方安装

```shell
# 下载安装包
sudo curl -L "https://github.com/docker/compose/releases/download/1.25.4/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# 设置权限
sudo chmod +x /usr/local/bin/docker-compose

# 添加软连接
sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose

# 查看安装信息
docker-compose --version

# 卸载数据
sudo rm /usr/local/bin/docker-compose
```



### pip 安装

```shell
# 1. 安装python-pip
yum -y install epel-release
yum -y install python-pip

 

# 2. 安装docker-compose
pip install docker-compose

# 3. 待安装完成后，执行查询版本的命令，即可安装docker-compose
docker-compose version
```

### 常用命令

```shell
# 后台启动
docker-compose up -d

# 停止
docker-compose down

# 查询容器列表
docker-compose ps

     Name                   Command               State                 Ports               
--------------------------------------------------------------------------------------------
mysql_atlas_1    /bin/sh -c /usr/local/mysq ...   Up      0.0.0.0:12346->1234/tcp           
mysql_master_1   docker-entrypoint.sh mysqld      Up      0.0.0.0:12347->3306/tcp, 33060/tcp
mysql_slave1_1   docker-entrypoint.sh mysqld      Up      0.0.0.0:12348->3306/tcp, 33060/tcp
mysql_slave2_1   docker-entrypoint.sh mysqld      Up      0.0.0.0:12349->3306/tcp, 33060/tcp

# 查询日志 (查询所有日志，可以辅助排查个别容器启动失败问题)
docker-compose logs 
```

