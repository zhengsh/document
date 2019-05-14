### ComponentScan 包扫描
1. 可以扫描指定包下面所有Bean
2. 支持exclude和include通过Filter来进行过滤
3. 通过ComponentScans 可以配置多个ComponentScan

### 组件注册-自定义TypeFilter指定过滤器
1. FilterType.ANNOTATION: 按照注解
2. FilterType.ASSIGNABLE_TYPE: 按照给定的类型
3. FilterType.ASPECTJ使用ASPECTJ: 表达式
4. FilterType.REGEX: 使用正则表达式
5. FilterType.CUSTOM: 自定义

### Spring 默认是单例模式可以通过@Scope 调整作用域
1. 可以通过@Scope来设置创建实例的模式（例子：@Scope("prototype"))。
2. prototype: 多实例的(IOC 容器启动并不会去掉红方法创建对象放在容器中，
每次获取的时候才会去调用方法创建对象)。
3. singleton: 单例的（默认值): ioc 容器启动会调用方法创建对象放到ioc容器中。以后每次获取就是从容器(map.get())中拿。
4. request: 同一次请求创建一个实例。
5. session: 同一个session 创建一个实例。

### 懒加载,单实例Bean:默认在容器启动的时候创建对象放入容器中
1. 懒加载，容器启动的时候不创建对象，在第一次使用（获取）Bean创建对象，并初始化；
2. 通过@Lazy来设置懒加载（对于单实例）,在第一次调用的时候创建Bean。
  
### 按照条件注册进行注册（@Conditional）
1. Conditional 可以设置Bean的条件注册，他可以修饰Class(标注Class的时候下面所有的Bean只有条件满足的时候才能被注册)也可以修饰Method。
2. 可以通过自定义Condition接口matches方法可以控制是否被注册
 
### 给容器中注册组件
1. 包扫描+组件标注注解（@Controller/@Service/@Repository/@Component）[自己写的类]
2. 导入第三方的包@Bean
3. @Import 导入组件（导入组件id 默认是全类名）, ImportSelector 导入的选择器(需要提供导入的全类名数组), ImportBeanDefinitionRegistrar 可以通过调用实现类的 registry.registerBeanDefinition 来注册。








