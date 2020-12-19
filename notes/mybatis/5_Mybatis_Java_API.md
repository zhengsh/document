### MyBatis Java API
* MyBatis 很大程度简化了了代码，MyBatis 3 引入了很多重要的改进使得SQL映射更加优秀。


### SqlSession
* 使用MyBatis 的主要接口就是 SqlSession。通过这个接口来执行命令，获取映射器和管理实务。SqlSession 是由 SqlSessionFactory 实例创建的。 SqlSessionFactory 对象包含创建 SqlSession 实例所有方法。 而SqlSessionFactory 本身是
由SqlSessionFactoryBuilder创建的。它可以从 XML、注解或手动配置Java 代码来创建SqlSessionFactory。

* **当MyBatis与一些依赖注入框架（如 Spring 或者 Guice）同时使用的时候， SqlSession 将被依赖注入框架所创建，就不需要适应
SqlSessionFactory 和 SqlSessionFatoryBuilder**

#### SqlSessionFactoryBuilder
* SqlSessionFactoryBuilder 有五个 build() 方法，每一种都允许你从不同的资源中创建一个 SqlSessionFactory 实例。
```java
SqlSessionFactory build(InputStream inputStream)
SqlSessionFactory build(InputStream inputStream, String environment)
SqlSessionFactory build(InputStream inputStream, Properties properties)
SqlSessionFactory build(InputStream inputStream, String env, Properties props)
SqlSessionFactory build(Configuration config)
```
* 第一种方法是最常用的，它使用了一个参照了 XML 文档或上面讨论过的更特定的 mybatis-config.xml 文件的 Reader 实例。可选的参数是 environment 和 properties。environment 决定加载哪种环境，包括数据源和事务管理器。比如：
```xml
<!-- 配置环境 -->
<environments default="development">
    <!-- 配置 development 环境信息 -->
    <environment id="development">
        <!-- 配置事务类型 -->
        <transactionManager type="JDBC"/>
        <!-- 配置数据源（连接池） -->
        <dataSource type="POOLED">
            <!-- 配置数据库的4个基本信息 -->
            <property name="driver" value="${driver}"/>
            <property name="url" value="${url}"/>
            <property name="username" value="${username}"/>
            <property name="password" value="${password}"/>
        </dataSource>
    </environment>
</environments>
```
* 以上参数配置都有注释下文不再赘述。
* MyBatis 加载 properties 的执行顺序：
  * 首先读取在 properties 元素体中指定属性。
  * 其次，从 properties 元素的类路径 resource 或 url 指定的属性，且会覆盖已经指定了的属性
  * 最后，读取作为方法参数传递的属性，，且会覆盖已经从 properties 元素体和 resource 或 url 属性中加载了的重复属性。
* 因此，通过方法参数传递的属性的优先级最高， resource 或 url 指定的属性次之， 在properties 元素体中指定属性优先级最低。

* 以下是一个创建 SqlSessionFacatory 的一个示例：
```java
String resource = "mybatis-config.xml";
InputStream inputStream = Resources.getResourceAsStream(resource);
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
```

#### SqlSessionFactory
* SqlSession 有6个方法创建SqlSession示例。通常来说，当你选择这些方法需要考虑一下的几个问题
  * **事务处理:** 需要session 使用事务或者使用自动提交功能？
  * **连接:** 需要获取数据源的配置？自己使用自己提供的配置？
  * **执行语句:** 需要复用预处理语句或批量更新语句？
* MyBatis 提供了多个重载方法
```java
SqlSession openSession()
SqlSession openSession(boolean autoCommit)
SqlSession openSession(Connection connection)
SqlSession openSession(TransactionIsolationLevel level)
SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level)
SqlSession openSession(ExecutorType execType)
SqlSession openSession(ExecutorType execType, boolean autoCommit)
SqlSession openSession(ExecutorType execType, Connection connection)
Configuration getConfiguration();
```
* 默认的 openSession() 没有参数，它会创建一个有如下特征的 SqlSession。
  * 会开启一个事务，默认不是自动提交
  * 将从当前环境配置的 DataSource 实例中获取 Connection 对象。
  * 事务隔离级别将会使用驱动或从数据源的默认配置
  * 预处理语句不会被服用，也不会批量处理更新

#### SqlSession
* SqlSession 实例是一个非常强大的类。它可以执行语句、提交或者虎丘映射器的实例方法。
* 在 SqlSession 类中有超过20个犯法， 所以它们组合成易于理解的分组。

##### 执行语句方法
* 这些方法用来指定定义在SQL映射的XML文件中的 SELECT、INSERT 、UPDATE 和 DELETE语句。他们都会自行解释。每一句都是使用语句的ID 和参数对象，参数可以是原生类型（自动装箱或包装类）、JavaBean、POJO 或 Map.
```java
<T> T selectOne(String statement, Object parameter)
<E> List<E> selectList(String statement, Object parameter)
<T> Cursor<T> selectCursor(String statement, Object parameter)
<K,V> Map<K,V> selectMap(String statement, Object parameter, String mapKey)
int insert(String statement, Object parameter)
int update(String statement, Object parameter)
int delete(String statement, Object parameter)
```
* selectOne 和 selectList 的不同仅仅是 selectOne 必须返回一个对象或 null 值。如果返回值多于一个，那么就会抛出异常。如果你不知道返回对象的数量，请使用 selectList。如果需要查看返回对象是否存在，可行的方案是返回一个值即可（0 或 1）。selectMap 稍微特殊一点，因为它会将返回的对象的其中一个属性作为 key 值，将对象作为 value 值，从而将多结果集转为 Map 类型值。因为并不是所有语句都需要参数，所以这些方法都重载成不需要参数的形式。

* 最后，还有 select 方法的三个高级版本，它们允许你限制返回行数的范围，或者提供自定义结果控制逻辑，这通常在数据集合庞大的情形下使用。
```java
<E> List<E> selectList (String statement, Object parameter, RowBounds rowBounds)
<T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds)
<K,V> Map<K,V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowbounds)
void select (String statement, Object parameter, ResultHandler<T> handler)
void select (String statement, Object parameter, RowBounds rowBounds, ResultHandler<T> handler)
```
* RowBounds 参数会告诉 MyBatis 略过指定数量的记录，还有限制返回结果的数量。RowBounds 类有一个构造方法来接收 offset 和 limit，另外，它们是不可二次赋值的。
```java
int offset = 100;
int limit = 25;
RowBounds rowBounds = new RowBounds(offset, limit);
```
* 所以在这方面，不同的驱动能够取得不同级别的高效率。为了取得最佳的表现，请使用结果集的 SCROLL_SENSITIVE 或 SCROLL_INSENSITIVE 的类型(换句话说：不用 FORWARD_ONLY)。
* ResultHandler 参数允许你按你喜欢的方式处理每一行。你可以将它添加到 List 中、创建 Map 和 Set，或者丢弃每个返回值都可以，它取代了仅保留执行语句过后的总结果列表的死板结果。你可以使用 ResultHandler 做很多事，并且这是 MyBatis 自身内部会使用的方法，以创建结果集列表。
* 从3.4.6开始，传递给可调用语句的ResultHandler将用于存储过程的每个REFCURSOR输出参数（如果有）。
```java
package org.apache.ibatis.session;
public interface ResultHandler<T> {
  void handleResult(ResultContext<? extends T> context);
}
```
* ResultContext 参数允许你访问结果对象本身、被创建的对象数目、以及返回值为 Boolean 的 stop 方法，你可以使用此 stop 方法来停止 MyBatis 加载更多的结果。

* 使用 ResultHandler 的时候需要注意以下两种限制：
  * 从被 ResultHandler 调用的方法返回的数据不会被缓存。
  * 当使用结果映射集（resultMap）时，MyBatis 大多数情况下需要数行结果来构造外键对象。如果你正在使用 ResultHandler，你可以给出外键（association）或者集合（collection）尚未赋值的对象。


##### 批量执行更新方法
* 有一个方法可以刷新（执行）存储在JDBC 驱动类中的批量更新语句。当你将 ExecutorType.BATCH 作为 ExecutorType 使用时可以采用此方法。
```java
List<BatchResult> flushStatements()
```

##### 事务控制方法
* 控制事务作用域有四个方法。当然，如果你已经设置了自动提交或你正在使用外部事务管理器，这就没有任何效果了。然而，如果你正在使用 JDBC 事务管理器，由Connection 实例来控制，那么这四个方法就会派上用场：
```java
void commit()
void commit(boolean force)
void rollback()
void rollback(boolean force)
```
* 默认情况下 MyBatis 不会自动提交事务，除非它侦察到有插入、更新或删除操作改变了数据库。如果你已经作出了一些改变而没有使用这些
方法，那么你可以传递 true 到commit 和 rollback 方法来保证事务被正常处理（注意，在自动提交模式或使用了外部事务管理器的情况下 force 值对 sesssion 无效）。很多时候你不用调用 rollback() , 因为 MyBatis 会在你没有调用commit 时替你完成回滚操作，然而
如果你要在支持多提交和回滚session 中获得更多细粒度的控制. 你可以使用回滚操作来达到目的。

* MyBatis-Spring 和 MyBatis-Gurice 提供了声明式事务处理，如果你在使用 MyBatis 的同时使用了 Spring 或 Gurice . 请参考他们的官方手册获取更多的信息。

##### 本地缓存
* Mybatis 使用到了两种缓存：本地缓存（local cache）和二级缓存（second level cache）。

* 每当一个新 session 被创建，MyBatis 就会创建一个与之相关联的本地缓存。任何在 session 执行过的查询语句本身都会被保存在本地缓存中，那么，相同的查询语句和相同的参数所产生的更改就不会二度影响数据库了。本地缓存会被增删改、提交事务、关闭事务以及关闭 session 所清空。

* 默认情况下，本地缓存数据可在整个 session 的周期内使用，这一缓存需要被用来解决循环引用错误和加快重复嵌套查询的速度，所以它可以不被禁用掉，但是你可以设置 localCacheScope=STATEMENT 表示缓存仅在语句执行时有效。

* 注意，如果 localCacheScope 被设置为 SESSION， 那么 MyBatis 所返回的引用将传递给保存在本地缓存里相同的对象。返回的对象（例如：list）做出任何更新将会影响本地缓存的内容，而影响存活在 session 生命周期中的缓存缩所返回的值。因此，不要对MyBatis 所返回的对象做任何更改以防后患。

* 清空本地缓存
```java
void clearCache()
```

##### 关闭SqlSession
```java
void close()
```
* 你必须保证的最重要的事情是你要关闭所打开的任何 session。保证做到这点的最佳方式是下面的工作模式:
```java
try (SqlSession session = sqlSessionFactory.openSession()) {
    // following 3 lines pseudocod for "doing some work"
    session.insert(...);
    session.update(...);
    session.delete(...);
    session.commit();
}
```
* 就像 SqlSessionFactory , 你可以通过调用当前使用的 SqlSession 的 getConfiguration 方法来获得 Configuration 实例。
```java
Configuration getConfiguration()
```

##### 使用映射器
```java
<T> T getMapper(Class<T> type)
```
* 上述的各个 insert、update、delete 和 select 方法都很强大，但是也很繁琐，可能会长兴类型安全问题并且对于你的 IDE和单元测试也没有实质性的帮助。在上面的人们章节中我们看到了一个使用映射器的实例。
* 因此，一个更通用的方式来执行映射语句是使用映射器类。 一个映射器类就是一个仅需什么与SqlSession 方法匹配的方法的接口类。下面的示例展示了一些方法签名以及他们是如何映射到 SqlSession 上。
```java
public interface BlogMapper {

    Blog get(String id);

    List<Blog> select();

    void insert(Blog dto);

    void update(Blog dto);
}
```
* 总之，每个映射器方法签名应该匹配相关的 SqlSession 方法，而字符串参数ID则无需匹配，相反，方法名必须配映射语句的ID
* 此外，返回类型必须匹配期望的返回类型， 单返回值时为所指定类的值，多返回值是为数据或集合。所有常用的类型都是支持的，包括：
原生类型、Map、POJO 和 JavaBean.
* **映射器接口不需要去实现任何接口或继承任何类，只要方法可以被唯一标识对应的映射语句就可以了**
* **映射器接口可以继承其他接口，当使用 XML来构建映射器接口时要保证语句被包含在合适的命名空间中。而且唯一的限制就是你不能在两个继承关系的口中拥有相同的方法签名（潜在的危险做法不可取）**
* 你可以传递多个参数给一个映射器方法。如果你这样做了，默认情况下它们将会以 "param" 字符串紧跟着它们在参数列表中的位置来命名，比如：#{param1}、#{param2}等。如果你想改变参数的名称（只在多参数情况下），那么你可以在参数上使用 @Param("paramName") 注解。

##### 映射器注解
* 因为最初设计时，MyBatis 是一个 XML 驱动的框架。配置信息是基于 XML 的，而且映射语句也是定义在 XML 中的。而到了 MyBatis 3，就有新选择了。MyBatis 3 构建在全面且强大的基于 Java 语言的配置 API 之上。这个配置 API 是基于 XML 的 MyBatis 配置的基础，也是新的基于注解配置的基础。注解提供了一种简单的方式来实现简单映射语句，而不会引入大量的开销。

* **注意 不幸的是，Java 注解的的表达力和灵活性十分有限。尽管很多时间都花在调查、设计和试验上，最强大的 MyBatis 映射并不能用注解来构建——并不是在开玩笑，的确是这样。比方说，C#属性就没有这些限制，因此 MyBatis.NET 将会比 XML 有更丰富的选择。也就是说，基于 Java 注解的配置离不开它的特性。**

##### 映射器示例
* 通过 @SelectKey 注解来读取自增列的唯一主键ID
```java
//mapper
@InsertProvider(type = BlogProvider.class, method = "insertSql")
@SelectKey(statement = "select last_insert_id()", keyProperty = "id", keyColumn = "id", 
    before = false, resultType = long.class)
void insert(Blog dto);

//provider
public String insertSql() {
    return new SQL()
            .INSERT_INTO("blog")
            .INTO_COLUMNS("id", "name", "title", "content")
            .INTO_VALUES("#{id}", "#{name}", "#{title}", "#{content}")
            .toString();
}
```

* 或者通过 @Options 注解来读取自增列的唯一主键ID
```java
//mapper
@InsertProvider(type = BlogProvider.class, method = "insertSql")
@Options(useGeneratedKeys = true, keyProperty = "id")
void insert(Blog dto);
```

* 通过 @Result 的id 来获取结果集
```java
@SelectProvider(type = BlogProvider.class, method = "findAllSql")
@Results(id = "resultMap", value = {
        @Result(id = true, column = "id", property = "id"),
        @Result(column = "author_id", property = "authorId"),
        @Result(column = "name", property = "name"),
        @Result(column = "title", property = "title"),
        @Result(column = "content", property = "content"),
})
List<Blog> finAll();

@SelectProvider(type = BlogProvider.class, method = "findBlogLikeSql")
@ResultMap(value = {"resultMap"})
List<Blog> findBlogLike(@Param("name") String name, @Param("title") String title,
                        @Param("content") String content);
```

* 多个参数使用 @SqlProvider 注解
```java
//mapper
@SelectProvider(type = BlogProvider.class, method = "findBlogLikeSql")
@ResultMap(value = {"resultMap"})
List<Blog> findBlogLike(@Param("name") String name, @Param("title") String title,
                        @Param("content") String content);

//provider
public String findBlogLikeSql(@Param("name") String name, @Param("title") String title,
                                  @Param("content") String content) {
    return new SQL() {{
        SELECT("name, title, content");
        FROM("blog");
        if (name != null) {
            WHERE("name like #{name}");
        }
        if (title != null) {
            WHERE("title like #{title}");
        }
        if (content != null) {
            WHERE("content like #{content}");
        }
    }}.toString();
}                        
```


[MyBatis SQL 语句构建器 (next)](https://github.com/zhengsh/document/blob/master/notes/mybatis/6_MyBatis_SQL%E8%AF%AD%E5%8F%A5%E6%9E%84%E5%BB%BA%E5%99%A8.md "MyBatis SQL 语句构建器")