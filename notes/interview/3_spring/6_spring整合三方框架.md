# Spring 整合三方框架

### spring-mybaits 整合

* 导入依赖

```xml
<dependency>
<groupId>org.mybatis</groupId>
<artifactId>mybatis-spring</artifactId>
<version>2.0.3</version>
</dependency>
```

### 事务处理

一个使用 MyBatis-Spring 的其中一个主要原因是它允许 MyBatis 参与到 Spring 的事务管理中。而不是给 MyBatis 创建一个新的专用事务管理器，MyBatis-Spring 借助了 Spring 中的 DataSourceTransactionManager 来实现事务管理。

一旦配置好了 Spring 的事务管理器，你就可以在 Spring 中按你平时的方式来配置事务。并且支持 @Transactional 注解和 AOP 风格的配置。在事务处理期间，一个单独的 `SqlSession` 对象将会被创建和使用。当事务完成时，这个 session 会以合适的方式提交或回滚。

事务配置好了以后，MyBatis-Spring 将会透明地管理事务。这样在你的 DAO 类中就不需要额外的代码了。

### MapperScannerConfigurer

Mybatis MapperScannerConfigurer 自动扫描 将Mapper接口生成代理注入到Spring Mybatis在与Spring集成的时候可以配置 MapperFactoryBean来生成Mapper接口的代理。

### 更多
可以到 [Spring-MyBaits文档](https://mybatis.org/spring/zh/getting-started.html) 阅读文档

