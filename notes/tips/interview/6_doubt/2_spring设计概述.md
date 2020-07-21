# Spring 设计概述

### spring 提供的功能

* IOC 主要就是管理我们对象创建解决对象依赖的管理
* AOP 做拦截器、日志记录、声明式事务管理
* 对于三方框架或者优秀框架的集成：spring-data(redis, jpa), spring-mybatis, spring-mvc
* spring-boot，spring-cloud 快速启动框架以及微服务的基础平台

### spring 项目中的使用

* 目前主要项目是基于 spring-boot 2.1 主要然后用的 spring 的全家桶那一套 : Eureka、Zuul、OpenFegin
* 也使用国内Alibba 的一些组件 Druid, FastJson

### spring 的设计实现

* 在Spring 主要是使用注解来用的，启动过程中，首先创建 BeanFactory .
* BeanDefinition 是Spring的Bean的基础信息配置会按照这些信息来初始化Bean。
* BeanPostProcessor 是用来对Spring的Bean的功能进行增强如 AOP 就是通过后置处理器来实现的。
* 事件驱动模式，Spring 在容器初始化过程中会创建一个事件派发器 ApplicationEventMultcater 来进行事件派发，其实在
  初始化过程中也会触发一些执行过程中的事件。