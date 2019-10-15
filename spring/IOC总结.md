### IOC 总结
  1. ApplicationConfigApplicationContext
  2. 组件注册
    1. @ComponentScan
    2. @Bean
    3. @Configuration
    4. @Component
    5. @Service
    6. @Controller
    7. @Repository
    8. @Conditional 条件注册
    9. @Primary
    10. @Lazy
    11. @Scope
    12. @Import 引入组件
    13. ImportSelector
    14. 工厂模式
  3. 组件赋值
    1. @Value
    2. @Autowired 
       1. @Qualifier
       2. 其他方式
         1. @Resource(JSR250)
         2. @Inject(JSR330, 需要导入 javax.inject)
    3. @PropertySource
    4. @PropertySources
    5. @Profile
       1. Environment
       2. -Dspring.profiles.active=dev
  
  4. 组件注入
    1. 方法参数
    2. 构造参数
    3. ApplicationContextAware
       1. ApplicationContextAwareProcessor
    4. xxxAware
  