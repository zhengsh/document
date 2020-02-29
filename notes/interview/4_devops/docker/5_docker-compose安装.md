# docker-comppse 安装

```shell
# 
sudo curl -L "https://github.com/docker/compose/releases/download/1.25.4/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# 
sudo chmod +x /usr/local/bin/docker-compose

#
sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose

# 查看安装信息
docker-compose --version

# 卸载数据
sudo rm /usr/local/bin/docker-compose
```



```shell
# 1. 安装python-pip
yum -y install epel-release
yum -y install python-pip

 

# 2. 安装docker-compose
pip install docker-compose

# 3. 待安装完成后，执行查询版本的命令，即可安装docker-compose
docker-compose version
```

