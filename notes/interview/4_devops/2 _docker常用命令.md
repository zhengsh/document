# Docker 命令

## Docker 常用命令

* run : 下载（pull）创建（create）运行 start

  --name 给容器定义名称

  --rm 这是一个临时的容器

  --ip 指定容器IP

  -p 指定映射端口

  -d 后台启动

  -v 设置挂在目录（通过：分割连个文件，两边都要写绝对路径）

  -h 设置 hostname

```shell
docker run --name myNginx --rm -p 12346:80 nginx
```

* logs: 查看日志 , 查看容器详细信息 inspect 

```shell
docker logs myNginx
```

* exec -it 进入容器

```shell
# 进入容器
docker exec -it myNginx bash

# 查看 Nginx 的配置
cat /etc/nginx/conf.d/default.conf 
```

* 查看运行的容器

```shell
# 查看当前运行的容器
docker ps

# 查看所有的容器
docker ps -a
```

### Docker 镜像

* pull 下载
* images 查看当前本地镜像仓库
* search 查找镜像

```shell
docker search java
```

* 镜像的构建

```shell
# 1. 编写 Dockerfile
vim Dockerfile

# 2. 文件内容
FROM nginx
RUN echo '<h1>This is Test Nginx</h1>' > /usr/share/nginx/html/index.html

# 3. 构建镜像
docker build -t ssm_nginx ./

# 4. 查询镜像
docker images

REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
ssm_nginx           latest              7f48053b7f39        33 seconds ago      127MB
nginx               latest              a1523e859360        2 days ago          127MB

# 5. 运行镜像
docker run --name myNginx --rm -p 80:80 ssm_nginx
```

