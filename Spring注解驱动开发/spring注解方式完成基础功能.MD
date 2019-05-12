### ComponentScan 包扫描
  1.可以扫描指定包下面所有Bean
  2.支持exclude和include通过Filter来进行过滤
  3.通过ComponentScans 可以配置多个ComponentScan

### 组件注册-自定义TypeFilter指定过滤器
  1.FilterType.ANNOTATION: 按照注解
  2.FilterType.ASSIGNABLE_TYPE: 按照给定的类型
  3.FilterType.ASPECTJ使用ASPECTJ: 表达式
  4.FilterType.REGEX: 使用正则表达式
  5.FilterType.CUSTOM: 自定义

### Spring 默认是单例模式可以通过@Scope 调整作用域
  1.可以通过@Scope来设置创建实例的模式（例子：@Scope("prototype"))。
  2.prototype: 多实例的(IOC 容器启动并不会去掉红方法创建对象放在容器中，
每次获取的时候才会去调用方法创建对象)。
  3.singleton: 单例的（默认值): ioc 容器启动会调用方法创建对象放到ioc容器中。
以后每次获取就是从容器(map.get())中拿。
  4.request: 同一次请求创建一个实例。
  5.session: 同一个session 创建一个实例。

### 懒加载,单实例Bean:默认在容器启动的时候创建对象放入容器中
  1.懒加载，容器启动的时候不创建对象，在第一次使用（获取）Bean创建对象，并初始化；
  2.

例子:
import cn.edu.cqvie.bean.Person;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * 配置类 == 配置文件
 * 告诉Spring 这个是一个配置类
 *
 * @author ZHENG SHAOHONG
 */
@Configuration
/*
@ComponentScan(value = "cn.edu.cqvie",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {
                        Controller.class,
                        Service.class
                })
        },
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {
                        Repository.class
                })
        })*/
@ComponentScans(value = {
        @ComponentScan(value = "cn.edu.cqvie",
                excludeFilters = {
                        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {
                                Controller.class,
                                Service.class
                        })
                },
                includeFilters = {
                        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {
                                Repository.class
                        })
                }, useDefaultFilters = false)
})
public class MainConfig {
    /**
     * 给容器中注册一个Bean; 类型为返回值的类型，ID默认是用方法名作为ID
     *
     * @return
     */
    @Bean("person")
    public Person person01() {
        return new Person("lisi", 20);
    }
}

