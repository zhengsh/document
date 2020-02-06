# Spring MVC的启动过程

* 在 servlet3.0 规范中可以取消了web.xml我们可以通过如下文件来注册启动Servlet
META-info/services/javax.servlet.ServletContainerInitializer文件中

* 该实现类需要实现 ServletContainerInitializer 这个接口的实现

* 还需要为此实现类添加 @HandlesTypes 注解

* 服务启动后回去调用 onStartup 方法。

### SpringServletContainerInitializer
* 在 servlet3.0 规范中SpringServletContainerInitializer是 ServletContainerInitializer 的一个实现，也是Spring的
WebApplicationInitializer一个SPI，它的目的是为了用来替代 web.xml 实现。
* 这个类将被加载和实例化 onStartup 方法会在servlet 容器启动后调用，通过 JAR servers API 的 ServiceLoader.load(Class)。
* 也可以也web.xml 配合使用。
* ServletContainerInitializer 主要负责委托将启动事件给实现类。
* ServletContainerInitializer 初始化的工作交给 WebApplicationInitializer 来完成。

* 本来应该是一个基础设施。这个容器实现是可选的，是用户可选的。