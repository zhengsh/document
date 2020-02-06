# Spring Boot 整合 Kafka

### 添加依赖 
```groovy
complie "org.springframework.kafka:spring-kafka"
complie "com.google.code.gson:gson"
```

### 添加YML配置
```yaml
spring:
  kafka:
    producer:
      bootstrap-servers: localhost:9092
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
    consumer:
      bootstrap-servers: localhost:9092
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    listener:
      missing-topics-fatal: false
```

### 业务代码
* 创建domain
```java
import java.io.Serializable;
import java.util.Date;

public class KafkaMessage implements Serializable {

    private Long id;
    private String username;
    private String password;
    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

```

* 创建生产者
```java
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendKafkaMessage(KafkaMessage message) {
        this.kafkaTemplate.send("myTopic", new Gson().toJson(message));
    }
}
```
* 创建消费者
```java
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @KafkaListener(topics = "myTopic", groupId = "myGroup")
    public void obtainMessage(ConsumerRecord<String, String> consumerRecord) {
        logger.info("obtainMessage invoked!");

        String topic = consumerRecord.topic();
        String key = consumerRecord.key();
        String value = consumerRecord.value();
        int partition = consumerRecord.partition();
        long timestamp = consumerRecord.timestamp();

        logger.info("topic: {}", topic);
        logger.info("key: {}", key);
        logger.info("value: {}", value);
        logger.info("partition: {}", partition);
        logger.info("timestamp: {}", timestamp);

        logger.info("==========================");

    }
}

```
* 创建控制器
```java
import cn.edu.cqvie.kafka.KafkaMessage;
import cn.edu.cqvie.kafka.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping(value = "kafka")
public class KafkaController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private KafkaProducer kafkaProducer;

    @RequestMapping(value = "message", method = RequestMethod.GET)
    public KafkaMessage sendMessage(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "username") String username,
            @RequestParam(name = "password") String password) {

        logger.info("sendMessage invoked!");
        KafkaMessage message = new KafkaMessage();
        message.setId(id);
        message.setUsername(username);
        message.setPassword(password);
        message.setDate(new Date());

        kafkaProducer.sendKafkaMessage(message);
        return message;
    }

    @RequestMapping(value = "message", method = RequestMethod.POST)
    public KafkaMessage sendMessage2(@RequestBody KafkaMessage message) {
        logger.info("sendMessage2 invoked!");
        kafkaProducer.sendKafkaMessage(message);
        return message;
    }
}
```

* 测试
```shell script
curl http://localhost:9090/kafka/message\?id\=1\&username\=zhangsan\&password\=abc

curl -X POST -H "Content-type:application/json" -d '{"id":5, "username":"zhangsan", "password":"789"}' http://localhost:9090/kafka/message
```