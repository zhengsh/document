### 简介
* MyBatis 是一款优秀的持久层框架，它支持定制化 SQL、存储过程以及高级映射。MyBatis 避免了几乎所有的JDBC代码和手动参数红手指以及获取结果集。
MyBaits 可以使用简单的 XML 或注解来配置和映射原生类型、接口和 Java 的 POJO （Plain Old Java Object, 普通老式 Java 对象）为数据库汇总的记录。 

### 添加依赖
```xml
<dependency>
  <groupId>org.mybatis</groupId>
  <artifactId>mybatis</artifactId>
  <version>x.x.x</version>
</dependency>
```

### 通过 XML 构建 SqlSessionFactory
* 每个基于MyBaits 的应用都是以一个 SqlSessionFactory 的实例为核心，SqlSessionFactory 的实例可以通过 SqlSessionFactoryBuilder 获得。
而 SqlSessionFactory 则可以从 XML 配置文件或一个预先定制的 Configuration 的实例构建出一个 SqlSessionFactory 
* 从 XML 文件中构建 SqlSessionFactory 的实例非常简单，建议使用类路径下的资源文件进行配置。但是也可以使用任意的输入流（InputStream）实例。
包括字符串形式的文件路径或者 file:// 的 URL 形式的文件路径来配置。MyBatis 包含一个名叫 Resources 的工具类，包含一些实用方法，可使从
classpath 或其他位置加载资源文件更加容易。
```java
String resource = "mybatis-config.xml";
InputStream inputStream = Resources.getResourceAsStream(resource);
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
```
* XML 配置文件汇总包含对MyBatis 系统的核心设置，包含获取数据库连接实例的数据源（DataSource）和决定事物作用域和控制方式的事务管理器
（TransactionManager）。 XML 配置文件的详细内容后面再探讨，这里先给一个 XML 配置示例。
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
      PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
      "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
  <properties resource="jdbc.properties"></properties>
  <settings>
      <setting name="cacheEnabled" value="true"/>
      <setting name="logImpl" value="STDOUT_LOGGING"/>
  </settings>
  <environments default="development">
      <environment id="development">
          <transactionManager type="JDBC"/>
          <dataSource type="POOLED">
              <property name="driver" value="${driver}"/>
              <property name="url" value="${url}"/>
              <property name="username" value="${username}"/>
              <property name="password" value="${password}"/>
          </dataSource>
      </environment>
  </environments>
  <mappers>
      <package name="cn.edu.cqvie.mybatis.mapper"/>
  </mappers>
</configuration>
```

* 当然，还有很多配置可以在XML文件中进行配置，上面的示例指出的则是最关键的部分。要注意 XML 头部的声明，它用来验证 XML 文档的正确性。
environment 元素提中包含了事务管理和连接池的配置。mappers 元素则是包含一组映射器（mapper）, 这些映射器的 XML 映射文件包含了 SQL 代码
和映射自定义信息。

### 使用 XML 构建 SqlSessionFactory
* 如果你更加希望从Java代码而不是XML文件中创建配置，或者想要创建你自己的配置构建器，MyBatis 也提供了完整的配置类，提供所有和 XML 文件
相同功能的配置项。
```java
import cn.edu.cqvie.mybatis.dto.BlogDTO;
import cn.edu.cqvie.mybatis.mapper.BlogMapper;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;

public class Test2 {

    public static void main(String[] args) {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://127.0.0.1:3306/mybatis?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&useSSL=false";
        String username = "root";
        String password = "root123";
        DataSource dataSource = new PooledDataSource(driver, url, username, password);
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(BlogMapper.class);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);

        try (SqlSession session = sqlSessionFactory.openSession()) {
            BlogMapper blogMapper = session.getMapper(BlogMapper.class);
            blogMapper.insert(new BlogDTO("102", "zhangsan", "zhangsan", "zhangsan"));
            session.commit();
        }
    }
}
```

* 注意该例子中，configuration 添加了一个映射器类（mapper class）. 映射器类时Java类，它们包含SQL 映射语句的注解从而避免依赖 XML 文件
。不过，由于 Java注解的一些限制以及某些 Mybatis 映射的复杂性，要使用大多数高级映射（比如: 嵌套联合映射），任然需要使用XML配置， 有鉴于此
如果存在一个同名 XML 配置文件，MyBaits 会自动查找并加载它（在例子中，基于类路径和 BlogMapper.class 的类名会加载 BlogMapper.xml）。具体细节稍后讨论。

### 从 SqlSessionFactory 中获取 SqlSession
* 既然有了SessionFactory， 顾名思义，我们可以从中获取到 SqlSession 的实例。SqlSession 玩去哪包含了面向数据库执行SQL命令所需的所有方法。
你可以通过 SqlSession 实例来执行已映射的SQL语句。例如
```
try (SqlSession session = sqlSessionFactory.openSession()) {
    BlogDTO blog = (BlogDTO)session.selectOne("cn.edu.cqvie.mybatis.mapper.BlogMapper.get", 102);
    System.out.println(blog);
}
```
* 诚然，这种方式能能够正常工作， 并且对于使用旧版本的 MyBatis 的用户来说也比较熟悉。不过现在有一种更加简洁的方式 --使用正确面熟每个语句的
参数和返回值的接口（比如：BlogMapper.class）, 你现在不仅可以执行更清晰和安全类型的代码，而且还不用担心易错的字符串字面值以及强制类型转换
```java
try (SqlSession session = sqlSessionFactory.openSession()) {
    BlogMapper blogMapper = session.getMapper(BlogMapper.class);
    BlogDTO blog = blogMapper.get("102");
    System.out.println(blog);
}
```

### 映射SQL语句原理
* 现在我们来探究一下 SqlSession 和 Mapper 到底执行了什么，但 SQL 映射器是一个相当大的话题，可能会占去文档的大部分篇幅，本处做一个大概的
解释，这里会给出几个例子。
* 在上面提到的例子中， 一个语句既可以通过 XML 定义， 也可以通过注解定义。我们先看看XML定义语句的方式，事实上 MyBatis 提供的全部特征都可以利用
基于XML的映射语言来实现，这使得MyBatis 在过去的数年得以流行。如果你以前用过 MyBatis , 你应该对这个概念比较熟悉。不过自那以后，XML 的配置也
改进了许多，我们稍后还会提到。这里给出了一个基于 XML 映射语句的示例。它应该可以满足上述示例中SqlSession 的调用。
```xml
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="cn.edu.cqvie.mybatis.mapper.BlogMapper">

    <resultMap type="cn.edu.cqvie.mybatis.dto.BlogDTO" id="resultMap">
        <id column="id"             property="id"/>
        <result column="name"       property="name"/>
        <result column="title"      property="title"/>
        <result column="content"    property="content"/>
    </resultMap>

    <select id="get" resultMap="resultMap">
        select * from blog where `id` = #{id}
    </select>
</mapper>
```

* 为了这个简单的例子我们，似乎写了不少配置。在一个 XML 映射文件中，可以定义无数个映射语句，这样一来， XML 头部和文档类型申明占去的部分就显得
微不足道了，而文件的剩余部分具备自解释性，很容易理解。在命名空间 "cn.edu.cqvie.mybatis.mapper.BlogMapper"  中定义了一个
名为 "get" 的映射语句，允许你指定完全限定名来调用映射语句，就像前面的例子一样
```java
BlogDTO blog = (BlogDTO)session.selectOne("cn.edu.cqvie.mybatis.mapper.BlogMapper.get", 102);
```

* 你可能注意到这和使用完全限定名调用 Java 对象的个方法类似。 这样，该命名就可以直接映射到命名空间中同名的 Mapper 类， 并将已映射的 select 
语句中的名字、参数、返回类型匹配成方法。因此你就可以像上面那样很容易地调用这个对应的 Mapper 接口的方法，就像下面这样
```java
BlogMapper blogMapper = session.getMapper(BlogMapper.class);
BlogDTO blog = blogMapper.get("102");
```

* 第二种方法有很多优势，首选in它不依赖于字符串字面值，会更加安全一点；其次，如果IDE 有代码补全工鞥呢，那么代码补全完全可以帮你选择已映射的SQL语句。

* 对命名空间的一点说明
  * 在之前版本的 MyBatis 中， 命名空间（Namespaces）的作用并不大，是可选的。但那是现在，随着命名空间的越发重要，你必须制定命名空间
  
  * 命名空间的作用有两个， 一个是利用更长的完全限定名来将不同的语句隔离开来，同时也实现了你上面见到的接口绑定。就算你觉得暂时用不到接口绑定
  你也应该遵循这里的规定，以防止你那一天改变了主意。长远来看，只要将命名空间置于合适的Java包命名空间之中，你的代码会更加整洁，也有利与你
  更好的使用 MyBatis
  
  * 命名解析：为了减少输入量，MyBatis 对所有的命名配置元素（包括语句、结果映射、缓存等）使用了如下的命名解析规则
    * 完全限定名（比如："com.mypackage.myMapper.selectAllThings"） 将直接用于查找和使用
    * 短名称（比如："selectAllThings"）如果全局唯一也可以作为一个单独的引用。如果不唯一，有两个或两个以上的相同名称（比如：
    "com.foo.selectAllThings" 和 "com.bar.selectAllTings"）, 那么使用时就必须使用完全限定名。

* 对于像 BlogMapper 这样的映射器来说，他还有一种方法来处理，它们映射的语句可以不用 XML 来配置，而可以使用 Java 注解来诶之，比如。
上面的XML 可以被替换成如下：
```java
package cn.edu.cqvie.mapper;
public interface BlogMapper {
    @Select("select * from blog where id = #{id}")
    Blog selectBlog(int id);
}
```
* 使用注解来映射简单的查询语句会使代码更加简洁，然而稍微复杂一点的语句，Java 注解就力不从心了，并且会显得更加混乱。因此，如果你需要完成很复杂的查询
那么最好使用 XML 来映射语句。 选择合作方式来配置映射，以及认为映射语句的一致性是否重要，这些完全取决于你和你的团队。换句话说，永远不要拘泥于
一种方式，你可以很轻松的在基于注解和XML语句映射方式之间自由移植和切换。

### 作用域（Scope）和生命周期
* 理解我们目前已经讨论过的不同作用域和生命周期类时至关重要的，因为错误的使用会倒是非常严重的并发问题。
* 对生命周期和依赖注入框架
  * 依赖注入框架可以创建线程安全的、基于事务的SqlSession 和映射器，并将它们直接注入到你的 bean 中，因此可以直接忽略它们的生命周期。
  如果对如何通过依赖注入框架来使用MyBaits 感兴趣可以研究下 MyBaits-Spring 或 MyBaits-Guice 两个子项目
* SqlSessionFactoryBuilder 
  * **这类可以被实例化、使用和丢弃，一旦创建了 SqlSessionFactory, 就不再需要它了**。因此 SqlSessionFactoryBuilder 实例的最佳作用域是方法
  作用域（也就是局部方法变量）。你可以重用 SqlSessionFactoryBuilder 来创建多个 SqlSessionFactory 实例，但是最好还是不要让其以存在，
  以保证所有的 XML 解析资源可以被释放给更重要的事情。 
   
* SqlSessionFactory
  * **SqlSessionFactory 一旦被创建就应该在应用的运行期间一直存在，没有任何理由丢弃它或重新创建一个实例**。使用SqlSessionFactory 的最佳实践
  是在应用运行期间不要重复创建多次，多次重建 SqlSessionFactory 被视为一种代码 bad smell 。因此 SqlSessionFactory 的最佳作用域是应用
  作用域，有很多方法可以做到，最简单的是使用单例模式或者静态单例模式 

* SqlSession
  * **每个线程都应该有它自己的 SqlSession 实例。SqlSession 的实例不是线程安全的。因此不能被共享的**。所以它的最佳的作用域是请求或方法作用域
  绝对不能将 SqlSession 实例引用放在一个类的静域，甚至一个类的实例变量也不行。也绝对不能讲SqlSession 实例的引用放在任何类型的托管作用域中。
  比如 Servlet 框架中的 HttpSession. 如果你现在正在使用一种 Web 框架，考虑 SqlSession 放在一个 HTTP 请求对象相似的作用域中。换句话说。
  每次收到的 HTTP请求，就可以打开一个 SqlSession, 返回一个响应，就关闭它。这个关闭操作是很重要的，你应该把这个关闭操作放到 finally 块
  中确保每次都能执行关。喜爱按的示例就是一个确保SqlSession 关闭的标准模式。
  ```java
  try (SqlSession session = sqlSessionFactory.openSession()) {
      //你的应用逻辑代码
  }
  ```

* 映射器代码示例
  * 映射器是你创建的、绑定你樱色的语句的接口。映射接口的实例是从 SqlSession 中获得的，因此从技术层面上讲，任何映射器实例的最大作用域是和它
  们的SqlSession 相同的。尽管如此，映射器实例的最佳作用域是方法作用域。也就是说，映射器实例应该在调用他们的方法中被请求，用过之后即可丢弃。
  并不需要显示地关闭映射实例，尽管在整个请求作用域保持映射器实例也不会有什么问题，但是你回发现，像SqlSession 一样，在这个作用域上管理
  太多的资源的话会难于控制。为了变这种复杂性，最好把樱色器放在方法作用域内。下面的例子就展示了这个事件：
  ```java
  try (SqlSession session = sqlSessionFactory.openSession()) {
      BlogMapper mapper = session.getMapper(BlogMapper.class);
      //你的应用逻辑代码
  }
  ```
  
