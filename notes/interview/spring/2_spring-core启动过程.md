# Spring 启动过程

### Spirng 源码构建过程

* spring 源码地址下载

```shell
git clone git@github.com:spring-projects/spring-boot.git
```

* 导入IDEA（建议开启外网VPN）

### JSR-330

从Spring 3.0 开始，Spring 提供对 JSR-330 标准注释（依赖注入）的支持。这些注释的扫描方式与 Spring 注释的扫描方式相同，要使用它们，需要在类路径中有相关的 jar 。

```groovy
compile 'javax.inject:javax.inject:1'
```

### Spring 容器过程

我们可以通过``AnnotationConfigApplicationContext``传入 ``Config``配置类来初始化Spring 的上下文对象，那么咱们就来通过阅读源码来分析在 ``new AnnotationConfigApplicationContext(Config.class)``这个创建 ``ApplicationContext`过程中到底为咱们做了那些事情。

初始化上下文

```java
 ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
```

1. Spring 容器在启动的时候，会先保存所有注册进来的Bean的定义信息。
   1. XML 注册 bean: ``<bean />``。
   2. 注解注册 bean: @Service 、@Component 、@Bean。

2. Spring 容器会在适合的时机创建Bean。
   1. 用到这个 bean 的时候：利用 getBean () 创建 bean ; 创建好以后保存在容器中。
   2. 统一创建剩下的所有 bean 的时候: finishBeanFactoryInitialization()。

3. 后置处理器：

   ​	每一个 bean 创建完成后，都会创建各种后置处理器进行处理，来增强 bean 的功能。

   ​			``AutowiredAnnotationBeanPostProcessor`` : 处理自动注入功能

   ​			``AnnotationAwareAspectJAutoProxyCreator``: 来做 ``AOP`` 功能

   ​			xxx ....

4. 事件驱动模型：

   ``ApplicationListener``： 事件监听；

   ``ApplicationEventMulticaster`` ：事件派发;

### Spring 初始化容器实例代码

```java
public class StartTest {

    public static void main(String[] args) {
        //声明Spring上下文
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        //获取Dog实例
        Dog dog = context.getBean(Dog.class);
        System.out.println(dog);
    }
		
    //Dog类
    static class Dog {
        private String id;
        private String name;
      	//... 省略Get/Set方法
    }

    //配置文件定义Bean
    @Configuration
    static class Config {
        @Bean
        public Dog dog() {
            return new Dog();
        }
    }
}
```

