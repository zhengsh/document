# Docker 常见问题
### 1. Cannot connect to the Docker daemon at unix:///var/run/docker.sock. Is the docker daemon running?.

原因是没有启动docker 

```shell
service docker start
```



### 2. Docker 镜像下载过慢的问题

设置阿里云docker 镜像可以 参考aliyun.com 官方文档 

[文档地址](https://help.aliyun.com/document_detail/60743.html?spm=a2c4g.11186623.6.550.6fe3378bMzwMQp)

