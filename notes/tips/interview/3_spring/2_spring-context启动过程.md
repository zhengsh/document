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

初始化上下文可以通过下面的一行代码来初始化容器。下面简述Spring容器在启动过程中的主要流程，具体的执行过程可以在看我的 github 中自己编译的  [spring 源码](https://github.com/zhengsh/spring-framework.git) 中的注释说明。

```java
 ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
```

1. Spring 容器在启动的时候，会先保存所有注册进来的Bean的定义信息。
   1. XML 注册 bean: ``<bean />``。
   2. 注解注册 bean: @Service 、@Component 、@Bean。

2. Spring 容器会在适合的时机创建 bean 。
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

