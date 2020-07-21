# Spring-AOP原理

### Spring AOP

AOP 在 Spring Framework 中主要用于：

* 提供声明式事务
* 实现自定切面，通过AOP 去补充 OOP 使用

### BeanPostPorcessor

Bean 后置处理器允许自定义修改 spring bean factory 创建的bean 实例。如果需要在bean 的初始化前后定制一些逻辑可以通过自定义 BeanPostPorcessor 实现。