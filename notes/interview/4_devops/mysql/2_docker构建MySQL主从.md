# Docker 构建 MySQL 主从架构

### 配置文件

```properties
# master node
[mysqld]
server-id=99
# bin log 文件前缀
log-bin=mysql-bin
# 对应要同步的数据库
binglog-do-db=ssm

# 不需要同步的数据库
binglog-ignore-db=information_schema
binglog-ignore-db=mysql
binglog-ignore-db=personalsite
binglog-ignore-db=test
                     
                     
# slave node
[mysqld]
server-id=1
# 非必须
log-bin=mysql-bin
# 需要同步的数据
replicate-do-db=ssm

# 不需要同步的数据库
replicate-ignore-db=information_schema
replicate-ignore-db=mysql
replicate-ignore-db=personalsite
replicate-ignore-db=test
                     
```

### 修改密码和其他信息

```shell
# 关闭 slave 服务
stop slave

# 动态的配置，主节点连接信息
change master to master_host='master', master_user='root', master_password='123456'

# 启动 slave
start slave

# 查询状态
show slave status;
```

