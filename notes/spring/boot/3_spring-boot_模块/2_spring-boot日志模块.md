# spring-boot 整合日志模块

### 导入依赖

* spring-boot 的默认日志组件采用的是 **logback** 组件 

* 导入依赖
``` groovy
compile 'org.springframework.boot:spring-boot-starter-web'
```

### 添加配置

* 在 spring-boot 项目的 resources 目录下添加 logger-spring.xml 文件配置如下

* 文件内容如下
```xml
<?xml version="1.0" encoding="utf-8" ?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/base.xml"/>
    <logger name="cn.edu.cqvie" level="DEBUG"/>
    <springProfile name="default">
        <logger name="cn.edu.cqvie" level="INFO"/>
    </springProfile>
</configuration>
```
* 内容概述
  * configuration 标签是 logback 配置的根标签
  * include 标签是导入其他配置文件本次使用的是 "org/springframework/boot/logging/logback/base.xml" XML文件它是 spring-boot 提供的
  一个Base配置文件在 spring-boot.jar 文件中
  * logger 标签可以用来指定系统包的日志级别
  * springProfile 标签是环境配置标签，对应 spring profile 的环境变量，默认环境变量是 default 

### 测试代码
* 测试代码
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.swing.*;

@SpringBootApplication
public class MyApplication {

    private static final Logger logger = LoggerFactory.getLogger(MyApplication.class);

    public static void main(String[] args) {
        System.out.println(MyApplication.class.getClassLoader());
        SpringApplication.run(MyApplication.class, args);
    }

    @PostConstruct
    private static void myLog() {
        logger.trace("Trace Message");
        logger.debug("Debug Message");
        logger.info("Info Message");
        logger.warn("Warn Message");
        logger.error("Error Message");
    }
}

```

* 输入日志
```
2020-02-04 21:45:28.719  INFO 29298 --- [           main] cn.edu.cqvie.MyApplication               : Info Message
2020-02-04 21:45:28.720  WARN 29298 --- [           main] cn.edu.cqvie.MyApplication               : Warn Message
2020-02-04 21:45:28.720 ERROR 29298 --- [           main] cn.edu.cqvie.MyApplication               : Error Message
```

* 在 YML 配置中设置日志输出到文件和根日志级别
```yaml
logging:
  level:
    root: debug
  file:
    path: log/mylog
```