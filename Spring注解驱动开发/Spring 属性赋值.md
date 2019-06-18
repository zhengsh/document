### Spring 属性赋值
1. @Value 赋值有如下三种方式
  * 基本数值
  * 可以写SpEL; #{}
  * 可以写${}; 取出配置文件中的值（在运行环境变量中值）
     1. 通过@PropertySource 来加载属性文件保存到环境变量中
     2. 加载完外部的配置文件后使用${}读取配置文件中的值
     3. 如果中文出现乱码可以通过指定 @PropertySource encoding 来解决。

2. 自动装配，Spring利用依赖注入,完成对IOC容器中各个组件的依赖关系赋值
  * @Autowired 自动注入，如果一个组件需要使用另外一个组件，那么只需要在组件上面添加@Autowired, 默认会按照<font color=red>类型</font>去容器中找，找到就赋值，如果这个类型的组件有多个，
  * 如果找到多个类型的组件，需要通过@Primary（<font color=red>在自动装配的过程中首选装配</font>）指定一个优先级高的Bean来消除自动装配解决异常: NoUniqueBeanDefinitionException
如：org.springframework.beans.factory.NoUniqueBeanDefinitionException: No qualifying bean of type 'cn.edu.cqvie.dao.BookDao' available: expected single matching bean but found 2: bookDao,bookDao2
  * 通过@Qualifier 注解可以根据@Autowired 一起使用，指定被注入的Bean的ID，每个Bean都有一个默认的Qualifier, 内容与Bean的ID相同。
  * 如果在自动装配过程中允许Bean找不到或者那么可以通过 @Autowired(required = false) 来处理，就不会在装配过程中报NPE

3. Spring 还支持使用 @Resource（JSR250） @Inject(JSR330) [Java 规范注解]
  * @Resource 默认按照名称来装配
  * 通过name属性指定Bean ID, 如：@Resource(name = "bookDao2")
  * 没有required 属性
  * 不能和@Qualifier一起使用
  * 没有支持@Primary注解
  * @Inject 首先需要导入包 【javax.inject】依赖
  * @Inject 支持 @Primay 和 @Qualifier(支持 通过@Named 来指定 Bean 的ID)
  * 总结: 推荐使用@Autowired

4. 实现类： AutowiredAnnotationBeanPostProcessor 后置处理器

5. 如果组件只有一个有参构造器，有参构造器的@@Autowired可以省略。
  * 【标在方法位置】@Bean + 方法参数；参数从容器中获取；默认不写都是一样的，都可以默认自动装配
  * 【标在构造器上】如果组件只有一个有参构造器，这个有参构造器的@Autowired可以省略，参数位置的组件会自动注入
  *  放在参数位置

6. 自定义组件想要使用Spring容器底层的一些组件（ApplicationContext, BeanFactory, xxx）
   自定义组件实现xxxAware 在创建对象的时候，回调用接口规定的方法注入相关组件: Auare。
   把Spring底层一些组件注入到自定义的Bean中
   xxxAware 功能是使用 xxxProcessor
     ApplicationContextAware ==> ApplicationContextAwareProcessor
   
7. @Profile: 指定在具体的环境场景下注册组件, 
  * 如果没有这个指定，任何环境下都能注册这个组 默认是 default 环境
  * 通过虚拟机参数位置设置 -Dspring.profiles.active=test 环境参数
  * 通过代码的方式去设置
  * 通过注解的方式 在配置类上面标注 @Profile("test"), 只有具有此环境参数的时候该配置类才会被加载
  * 如果没有被@Profile标注的类，该配置类被加载或者被包扫描所扫描到，该Bean将被注册到容器中。
    
```
//1、创建一个applicationContext
AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
//2、设置需要激活的环境变量
applicationContext.getEnvironment().setActiveProfiles("test", "dev");
//3、注册主配置类
applicationContext.register(MainConfigOfProfile.class);
//4、刷新启动容器
applicationContext.refresh();
```
  
		
		