### Spring Boot 配置
* Spring Boot 提供了两种配置文件的形式
  1. properties 文件形式
    * 例子
    ```properties
    server.port = 9090
    ```
  2. yml文件形式 （YAML Yet Another Markup Language）
    ```yaml
    server:
      prot: 9090
    ```
  
### Spring Boot 部署
* grable bootJar 打包成一个 JAR 包

* 通过 java -jar **.jar 运行


### Jar 文件规范
* 需要设置到顶层包结构目录，可以允许有包。
* Jar文件是不能被嵌套的
  * 一个运用多个三方Jar，除了将依赖的内容拷贝的Jar中进行执行。会导致Jar文件的混乱
  * 通过自定义类加载器来去加载Jar文件和当前系统的业务文件（FatJar 来实现Jar的嵌套）

### Java 远程调试协议（JDWP）
实现远程调试
* Java Debug Wire Protocol Java调试协议
* 查看命令帮助
```
java -jar -agentlib:jdwp=help
```
* 程序启动命令
```
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5050 -jar xxx.jar
```
* IDEA 配置Remote，然后添加Address和Port
  
### @SpringBootApplication 注解
* 该注解包含 @Configuration、@EnableAutoConfiguration、@ComponentScan 三个注解的功能

* @SpringBootConfiguration 被 @Configuration修饰，表示一个类是 Spring Boot的启动配置类，应该只有在启动类使用
且通常在 @SpringBootApplication 中定义

#### @Configuration 注解
* @Configuration 声明一个配置类，里面可以配置一个或者多个被 @Bean 修饰的方法
  * @Configuration 通过创建 AnnotationConfigApplicationContext 实例然后注册配置类
  * 可以通过 getBean 来获取到 Bean 的实例
  ```
  AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
  ctx.register(AppConfig.class);
  ctx.refresh();
  MyBean myBean = ctx.getBean(MyBean.class);
  // use myBean ...
  ```
  * @Configuration 和 @ComponentScan 一起使用
  
  * Spring 会存在多种配置信息
    * @PropertySource
    * @Value
  
  * @Configuration 和其他组件一起组合。由于@Configuration是被Spring管理的，可以通过导入的方式关联的实例也会被加载
  例子：
  ```
     @Configuration
     public class DatabaseConfig {
   
        @Bean
        public DataSource dataSource() {
            // instantiate, configure and return DataSource
        }
    }
   
    @Configuration
    @Import(DatabaseConfig.class)
    public class AppConfig {
   
        private final DatabaseConfig dataConfig;
   
        public AppConfig(DatabaseConfig dataConfig) {
            this.dataConfig = dataConfig;
        }
   
        @Bean
        public MyBean myBean() {
            // reference the dataSource() bean method
            return new MyBean(dataConfig.dataSource());
        }
     }
     
  //可以通过如下方式来初始化 AppConfig、DatabaseConfig
  new AnnotaionConfigAppliactionContext(AppConfig.class);
  ```
  * @Configuration 和 @Profile 一起搭配
   
  * @Configuration 类的嵌套使用
  
  * @Configuration 延迟初始化通过增加 @Lazy 来实现
  
  * 
#### @EnableAutoConfiguration 注解
* 实现 Spring Boot 的自动化配置
* 尝试去猜想你需要配置 Class Bean , 自动配置类通常是通过你配置的 classpath 路径以及你所定义的 Bean。
如果你导入了tomcat-embedded.jar会推断出您可能需要创建一个 TomcatServletWebServerFactory（除非你自己定义类
ServletWebServerFactory Bean） 
* @SpringBootApplication 中包含本注解的功能
* 可以通过exclude、excludeName 来排除Bean
* 建议将@EnableAutoConfiguration 放置到root package上，会自动查找和匹配实现自动的注入。


### Spring Boot 项目启动过程
* SpringApplication 类作为一个启动类，通过 Java main 方法启动启动一个 spring boot 应用。 
  * 创建一个 ApplicationContext 的实例 
  * 注册一个 CommandLinePropertySource（命令行属性源） 来获取命令行配置，作为Spring 的一些配置
  * 刷新 Spring Context 加载所有的**单例**的Bean  
  * 触发每一个 CommandLineRunner 实例
  
* 大多数可以通过启动类的静态run方法直接启动一个应用。
```java
@Configuration
@EnableAutoConfiguration
public class MyApplication  {
   // ... Bean definitions

   public static void main(String[] args) {
     SpringApplication.run(MyApplication.class, args);
   }
}
```
* 定制化配置
```java
@Configuration
@EnableAutoConfiguration
public class MyApplication  {
   // ... Bean definitions

   public static void main(String[] args) {
     SpringApplication application = new SpringApplication(MyApplication.class);
     // ... customize application settings here
     application.run(args);
   }
}
```
* 推荐@SpringBootApplication来标记启动类

### Spring Boot 启动过程
* SpringApplication.run()
