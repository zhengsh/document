
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

### Spring Boot 项目启动过程
* 
* 
*   
  
### Java 远程调试协议（JDWP）
* Java Debug Wire Protocol Java调试协议
