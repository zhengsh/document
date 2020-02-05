# Kafka 操作命令

### 操作命令

* 查询系统的所有主题
```shell script
# 获取所有的主题
./bin/kafka-topics.sh --list --zookeeper localhost:2181
# 结果
#__consumer_offsets
# myTopic

```
  * __consumer_offsets_x 是系统的主题，是判断消费者消费的偏移量，一同会有50 个分区映射到0-49
  * 一个主题会对应多个日志目录，每个文件夹对应着一个分区
  
* 创建一个主题
```shell script
# 创建一个myTopic 3个分区，且一个副本，并且注册中心为 localhost:2181
./bin/kafka-topics.sh --create --zookeeper localhost:2181 --topic yourTopic --replication-factor 1 --partitions 3
```

* 创建一个发布者
```shell script
./bin/kafka-console-producer.sh --broker-list localhost:9092 --topic yourTopic
```

* 创建一个订阅者
```shell script
# --from-beginning 可以消费历史数据
./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic yourTopic --from-beginning
```