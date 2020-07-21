# Docker 安装 Consul

## 拉取镜像并启动容器

```shell
docker run -d --name consul --net=host -e 'CONSUL_LOCAL_CONFIG={"leave_on_terminate": true}' consul agent -bind=39.98.223.198 -config-dir /opt/docker/consul/etc
```



## 官网参照

* https://hub.docker.com/_/consul

