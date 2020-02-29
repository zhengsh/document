# Docker 底层存储结构

### 常见问题

* 基于镜像A创建镜像B的时候是否会拷贝A镜像中的所有文件？

    没有拷贝、删除镜像只是删除了引用

* 基于镜像创建容器时只会拷贝共享中所有文件至容器底层  

* 容器与镜像在结构上有什么却别

    

### 镜像的存储结构

* 查看引用关系

```shell
docker history ssm_nginx:latest
```

* 存储信息

```shell
# 查询所有的镜像
docker images 
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
ssm_nginx           latest              7f48053b7f39        43 minutes ago      127MB

# 查询镜像的详细信息
docker inspect 7f48053b7f39

# 镜像信息
//下级
"LowerDir": "/var/lib/docker/overlay2/5dbfb6581a8882ce79c184986fb02498ee404ea775ef1bb37428455331c1f177/diff:/var/lib/docker/overlay2/817ce2d6a40ec1c63d382c0eb647b325bd1f52d16e18c8a1f2f5643fe96de5c9/diff:/var/lib/docker/overlay2/d1fdd72104f5a8cb4796b020ae22a7f22e7b97f589c50452f9d704612ecdb2b2/diff",
//合并
"MergedDir": "/var/lib/docker/overlay2/03876a90f4b8874a9a7df210e1edb39f9a13e28d0b0c05ba1f7479e3c1c7cd82/merged",
// 上级
"UpperDir": "/var/lib/docker/overlay2/03876a90f4b8874a9a7df210e1edb39f9a13e28d0b0c05ba1f7479e3c1c7cd82/diff",
                "WorkDir": "/var/lib/docker/overlay2/03876a90f4b8874a9a7df210e1edb39f9a13e28d0b0c05ba1f7479e3c1c7cd82/work"

## 容器信息
"LowerDir": "/var/lib/docker/overlay2/dc97629c6fc37be2a0f2a74b4786328e8bb8ebf7c2e50c8435186fe1b5ac1217-init/diff:/var/lib/docker/overlay2/03876a90f4b8874a9a7df210e1edb39f9a13e28d0b0c05ba1f7479e3c1c7cd82/diff:/var/lib/docker/overlay2/5dbfb6581a8882ce79c184986fb02498ee404ea775ef1bb37428455331c1f177/diff:/var/lib/docker/overlay2/817ce2d6a40ec1c63d382c0eb647b325bd1f52d16e18c8a1f2f5643fe96de5c9/diff:/var/lib/docker/overlay2/d1fdd72104f5a8cb4796b020ae22a7f22e7b97f589c50452f9d704612ecdb2b2/diff",
"MergedDir": （合并所有层）
"/var/lib/docker/overlay2/dc97629c6fc37be2a0f2a74b4786328e8bb8ebf7c2e50c8435186fe1b5ac1217/merged",
"UpperDir": （容器读写层, 容器运行过程中就会创建这些文件）
"/var/lib/docker/overlay2/dc97629c6fc37be2a0f2a74b4786328e8bb8ebf7c2e50c8435186fe1b5ac1217/diff",
"WorkDir": "/var/lib/docker/overlay2/dc97629c6fc37be2a0f2a74b4786328e8bb8ebf7c2e50c8435186fe1b5ac1217/work"


```

* 查看上层信息

```shell
# 打开上层目录
cd /var/lib/docker/overlay2/03876a90f4b8874a9a7df210e1edb39f9a13e28d0b0c05ba1f7479e3c1c7cd82/diff

# 查看上层设置信息
cat usr/share/nginx/html/index.html 
<h1>This is Test Nginx</h1>
```

* 下层是操作系统的文件信息

