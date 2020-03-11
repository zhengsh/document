# Spring Cloud 概述

* 整合的有很多组件；常见的组件有：eureka 注册中心，Gateway 网关，Ribbon 负载均衡，Fegin 服务调用，Hystrix 熔断器。在有需要的时候添加对于启动器依赖即可
* 版本特征：以英文单词命名

# 创建微服务工程

需求：查询数据库中的用户数据并打印到浏览器中

* 父工程 ssm-springcloud：添加 spring-boot 父坐标和管理其他的组件依赖
* 用户服务工程 user-service:  整合 mybaits 查询数据库中用户数据；提供查询用户服务
* 服务消费工程 consumer-service: 利用用户服务获取用户数据并输出到浏览器

