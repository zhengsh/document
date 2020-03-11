# Docker 安装 Zookeeper

### 安装命令(单机)
``` shell
docker run --name zookeeper --restart always -p "12341:2181" -d zookeeper
```

## 查询日志

```shell
docker logs -f zookeeper
```

## 进入Docker 内

```shell
docker run -it --rm --link zookeeper:zookeeper zookeeper zkCli.sh -server zookeeper
```

## 查看节点信息

```shell
ls /

[zk: zookeeper(CONNECTED) 0] ls /
[services, zookeeper]
```