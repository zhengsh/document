# 模块总览

### 概述
spring-boot 能够让你快速的创建一个 Spring 应用，能够让你快速的完成基础模块和项目平台的搭建。
我们可以以独立Java运用的方式启动如使用启动命令：java -jar xx.jar . 也提供了比较传统的WAR包的方式。
我们的目标是：
  * 提供一个快速的 spring 开发启动运用
  * 开箱即用，提供了大量的初始化配置以减少不必要的配置信息
  * 提供一些非功能性特征（如：嵌入式服务器、安全框架、性能监控、配置外置）
  * 不需要生成代码，也不需要XML配置

### Spring Boot 模块

* spring-boot 
  * 用来支持spring boot 的其他的模块。
  * SpringApplication 用来支持其他的方法，用来创建和刷新一个 Spring 应用上下文(SpringApplication)实例。
  * Web 应用模块（默认使用Tomcat）包含了Tomcat、Jetty or Undertow（据说性能最高）。
  * 外置配置的支持。
  * 初始化器的一些日志的支持。

* spring-boot-autoconfigure 
  * 使用方式 增加注解 @EnableAutoConfiguration 来完成自动装配。
  * 自动配置通过推断来判断系统需要哪些配置。
  
* spring-boot-starters
  * 提供一站式的依赖起步
  * 如果你需要JPA的依赖那么你可以使用 spring-boot-starter-data-jpa 来引入jpa的依赖。

* spring-boot-cli

* spring-boot-starters
  * 提供监控的基础设施，提供了一站式的监控端点。

* spring-boot-actuator-autoconfigure
  * 端点的自动化装配，通过JMX，HTTP 公开端点。
  
* spring-boot-test
  * 单元测试让测试更加简单。
  
* spring-boot-test-autoconfigure
  * 基于类路径实现单元测试自动配置。

* spring-boot-loader
  * spring-boot 的FatJar方式打包工具一般不需要你自己去依赖，而是通过Gradle或者Maven插件依赖的方式来打包。
 
* spring-boot-devtools
  * 提供额外开发特新，自动重启，在打包应用部署的时候会自动禁用
  * 热部署
  
### Spring Boot 源码地址

[源码地址](https://github.com/spring-projects/spring-boot)