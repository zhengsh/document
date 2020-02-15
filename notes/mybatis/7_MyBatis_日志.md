### 日志

* MyBatis 的内置工厂提供日志功能，内置日志工厂交给一下其中一种工具做代理:
  * SLF4J
  * Apache Commons Logging
  * Log4j2
  * Log4j2
  * JDK Logging
* MyBatis 内置工厂基于运行时自省机制选择合适的日志工具。它会**使用第一个查找得到的工具（按照上文的熟悉怒查找）。如果一个都未找到，日志功能将被禁用**。
* 日志配置如下，在MyBaits 的主配置文件中
```xml
<configuration>
    <settings>
        <setting name="logImpl" value="SLF4J"/>
    </settings>
</configuration>    
```
* logImpl 可选的值有：SLF4J，LOG4J，LOG4J2，JDK_LOGGING，COMMONS_LOGGING，STDOUT_LOGGING ，NO_LOGGING	或者是实现了 org.apache.ibatis.logging.Log的， 并且构造方法是以字符串为参数的类的完全限定名。
* 也可以使用如下方法来使用日志工具:
```java
org.apache.ibatis.logging.LogFactory.useSlf4jLogging();
org.apache.ibatis.logging.LogFactory.useLog4JLogging();
org.apache.ibatis.logging.LogFactory.useJdkLogging();
org.apache.ibatis.logging.LogFactory.useCommonsLogging();
org.apache.ibatis.logging.LogFactory.useStdOutLogging();
```
* 如果使用了以上的任意一种方法，请在**调用其他 MyBatis 方法之前调用**它，另外仅当运行时路径中存在日志工具时，，改用日志工具的对应方法才会生效。

### 日志配置
* 配置 SL4J( SL4J 采用LOG4J2 作为日志实现) 的方式作为 MyBatis 的日志代理工具，Maven 实现。

* 步骤一: 添加依赖
```xml
<!--slf4j依赖-->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
</dependency>

<!--log4j2依赖-->
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-api</artifactId>
</dependency>
<!-- 桥接：告诉Slf4j使用Log4j2 -->
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-slf4j-impl</artifactId>
</dependency>
```
* 步骤二: 配置 LOG4J2 
  * 在应用中创建一个名称为log4j2.xml文件具体内容如下: 
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration status="WARN" monitorInterval="30">
    <appenders>
        <console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="[%d{HH:mm:ss:SSS}] [%p] - %l - %m%n"/>
        </console>
    </appenders>
    <loggers>
        <logger name="cn.edu.cqvie.mapper" level="DEBUG"></logger>
        <root level="info">
            <appender-ref ref="Console"/>
        </root>
    </loggers>
</configuration>
```
  * 以上配置会把org.mybatis包中所有的日志打印出来（**也会打印执行过程中的SQL**）
  * 如果我们想更细粒度的控制，可以设置为如下方式
```xml
<logger name="cn.edu.cqvie.mapper.BlogMapper.findAll" level="DEBUG"></logger>
```
