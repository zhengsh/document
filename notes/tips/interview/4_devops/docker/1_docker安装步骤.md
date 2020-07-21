# Docker 容器化技术

### 在CentOS上安装Docker 安装

1. Docker 要求CentOS 系统内核版本高于 ``3.10``

   通过 uname -r 查看你当前的版本

```shell
uname -r
```

2. 使用 root 权限登录 Centos 。确保 yum 包更新到最新。

```shell
yum -y update
```

3. 卸载旧版本

```shell
sudo yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine
```

4. 安装所需的软件包。``yum-utils``提供了``yum-config-manager`` 效用，并``device-mapper-persistent-data``和``lvm2``由需要 devicemapper存储驱动程序。

```shell
sudo yum install -y yum-utils \
  device-mapper-persistent-data \
  lvm2
```

5. 设置 ``yum`` 源, 并更新 ``yum`` 包的索引

```shell
sudo yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo
```

6. 查看所有仓库中 ``docker`` 版本，并选择指定版本安装。

```shell
yum list docker-ce --showduplicates | sort -r

docker-ce.x86_64            3:19.03.6-3.el7                     docker-ce-stable
docker-ce.x86_64            3:19.03.5-3.el7                     docker-ce-stable
docker-ce.x86_64            3:19.03.4-3.el7                     docker-ce-stable
```

7. 安装 ``Docker`` , 通过其完全合格的软件包名称安装特定版本，该软件包名称是软件包名称（docker-ce）加上版本字符串（第二列），从第一个冒号（:）一直到第一个连字符，并用连字符（-）分隔。例如，``docker-ce-18.09.1``

```shell
sudo yum install docker-ce-<VERSION_STRING> docker-ce-cli-<VERSION_STRING> containerd.io
# sudo yum -y install docker-ce-18.09.1 docker-ce-18.09.1 containerd.io
```

8. 启动并加入开机启动

```shell
sudo systemctl start docker
sudo systemctl enable docker
```

9. 检查是否安装成功（有client 和 service 两部分的表示 docker 安装启动成功了）

```shell
docker version
```

10. 卸载

   * 卸载 Docker 安装包

   ```shell
   sudo yum remove docker-ce
   ```
   * 主机上的映像，容器，卷或自定义配置文件不会自动删除。要删除所有图像，容器和卷：

   ```shell
   sudo rm -rf /var/lib/docker
   ```
