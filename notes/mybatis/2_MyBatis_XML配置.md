### 配置
* MyBatis 的配置文件包含了会影响 MyBatis 行为的设置和属性信息。配置文档的顶层结构如下
 * configuration (配置)
   * properties (属性)
   * setting (设置)
   * typeAliases (类型别名)
   * typeHandlers (类型处理器)
   * objectFactory (对象工厂)
   * plugins（插件）
   * environments (环境配置)
     * environment (环境变量)
     * transactionManager (事务管理器)
     * dataSource (数据源)
   * databaseIdProvider (数据库厂商标识)
   * <a href="#mappers">mapper (映射器)</a>

###  属性
* 这些属性都是可以外部配置可动态替换的，既可以在典型的 Java属性文件中配合也可以通过properties 元素的子元素来出传递。例如：
```xml
<properties resource="jdbc.properties">
    <property name="username" value="dev_user"/>
    <property name="password" value="F2Fa3!%TFCA"/>
</properties>
```
* 然后其中的属性就可以在整个配置文件中用来被替换需要动态配置的属性值.例如:
```xml
 <!-- 配置数据源（连接池） -->
<dataSource type="POOLED">
    <!-- 配置数据库的4个基本信息 -->
    <property name="driver" value="${driver}"/>
    <property name="url" value="${url}"/>
    <property name="username" value="${username}"/>
    <property name="password" value="${password}"/>
</dataSource>
```
* 这个例子中 username 和 password 将会由 properties 元素中设置的相应值来替换。driver 和 url 属性将会由 jdbc.properties 文件
中对应的值来替换。这样就为配置提供了诸多灵活选择。
```java
SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, props);

//  ... 或者 ...

SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader, environment, props);
```
* 如果属性在不止一个地方进行了配置，那么MyBatis 将按照如下的顺序来加载：
  * 在properties 元素体内指定的属性先被读取。
  * 然后根据 properties 元素中的 resource 属性读取类路径下属性文件或根据 url 属性指定的路径读取属性文件，并覆盖已读取的同名属性
  * 最后读取作为方法传递的的熟悉感，并覆盖已读取的同名属性
* 因此，通过方法参数传递的属性具有最高优先级，resource/url 属性中指定的配置文件次之，最低优先级的是 properties 属性中农指定的属性。
* 从 MyBatis3.4.2 开始，可以为占位符指定一个默认值。例如
```xml
<dataSource type="POOLED">
    <!-- 如果属性 'username' 没有被配置，'username' 属性的值将为 'ut_user' -->
    <property name="username" value="${username:ut_user}"/>
</dataSource>
```
* 这个特征默认是关闭的， 如果你想为占位符指定一个默认值，你应当添加一个指定的属性来开启这个特征。例如：
```xml
<properties>
    <!-- 启用默认值特征 -->
    <property name="org.apache.ibatis.parsing.PropertyParser.enable-default-value" value="true"/>
</properties>
```
* 如果你使用 ":" 作为属性的键, 如(: db:username) , 或者你已经在SQL定义中使用了 OGNL 表达式的三元运算符（如：${tableName != null 
? tableName : 'global_constants'}）, 你应该通过设置特定的属性来修改分隔键名和默认值的字符串。如:
```xml
<properties>
    <!-- 修改默认值得分隔符 -->
    <property name="org.apache.ibatis.parsing.PropertyParse.defualt-value-separator" vaue="?:"/>
</properties>
```
```xml
<dataSource type="POOLED">
    <property name="username" value="${db:username?ut_name}" />
</dataSource>
```

### 设置（settings）
* 这是设置 MyBaits 中极为重要的调整设置， 它们会改变 MyBaits 的运行时行为， 下表描述了设置中各项的意图、默认值等。

* 参数: cacheEnabled
  * 描述: 全局地开启或关闭配置文件中所有已配置的任何缓存
  * 范围: true|false
  * 默认值: true

* 参数: lazyLoadingEnabled 
  * 描述: 延迟加载的全局开关。 当开启时， 所有的关联的对象都会延迟加载。特定关联关系中可以通过设置 fethType 属性来覆盖该项
  的开关状态
  * 范围: true| false
  * 默认值: false

* 参数: aggressiveLazyLoading	
  * 描述: 当开启时，任何方法的调用都会加载该对象的所有属性。否则，每个属性都会按需加载（参考 lazyLoadTriggerMethods）
  * 范围: true| false
  * 默认值: false (在 3.4.1 及以前的版本默认值都为 true)


### 类型别名 (typeAliases)
* 类型别名是为 Java 类型设置一个短的名字。它只和 XML 配置相关，存在的意义在于用来减少完全限定名的冗余。例如：
```xml
<typeAliases>
    <typeAlias alias="Blog" type="cn.edu.cqvie.dto.Blog" />
</typeAliases>
```
* 这样配置时，Blog 可以用在任何 cn.edu.cqvie.dto.Blog 的地方。
* 也可以指定一个包名， MyBatis 会在包名下面搜索需要的 Java Bean，**这种方式也是我们实际开发中最常用到的方式** 比如：
```xml
<typeAliases>
    <package name="cn.edu.cqvie.dto" />
</typeAliases>
```
* 在每一个包 cn.edu.cqvie.dto 中的 Java Bean，在没有注解的情况下，会使用Bean的首字母小写的非限定类名来作为它的别名。
比如 cn.edu.cqvie.dto.Blog 的别名 blog; 若有注解，则别名为其注解值，下面的例子：
```java
@Alias("blog")
public class Blog {
    ...
}
```

### 类型处理器（typeHandlers）
* 无论是 MyBatis 在于处理语句（PreparedStatement）中设置一个参数时，还是从结果集中取出一个值时，都会用类型处理器将获取的值以适合的方式转换为Java类型。
  * **从 3.4.5 开始，MyBatis 默认支持 JSR-310（日期和时间API）**


### 处理枚举类型
* 若想映射枚举类型 Enum，则需要从 EnumTypeHandler 或者 EnumOrdinalTypeHandler 中选一个来使用。
* 比如说我们想存储取近似值时用到的舍入模式。默认情况下，MyBatis 会利用 EnumTypeHandler 来把 Enum 值转换成对应的名字。
* 注意 EnumTypeHandler 在某种意义上来说是比较特别的，其他的处理器只针对某个特定的类，而它不同，它会处理任意继承了 Enum 的类。
不过，我们可能不想存储名字，相反我们的 DBA 会坚持使用整形值代码。那也一样轻而易举： 在配置文件中把 EnumOrdinalTypeHandler 加到 typeHandlers 中即可， 这样每个 RoundingMode 将通过他们的序数值来映射成对应的整形数值。
```xml
<!-- mybatis-config.xml -->
<typeHandlers>
  <typeHandler handler="org.apache.ibatis.type.EnumOrdinalTypeHandler" javaType="java.math.RoundingMode"/>
</typeHandlers>
```
* 但是怎样能将同样的 Enum 既映射成字符串又映射成整形呢？

* 自动映射器（auto-mapper）会自动地选用 EnumOrdinalTypeHandler 来处理， 所以如果我们想用普通的 EnumTypeHandler，就必须要显式地为那些 SQL 语句设置要使用的类型处理器。
```xml
<!DOCTYPE mapper
    PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="org.apache.ibatis.submitted.rounding.Mapper">
	<resultMap type="org.apache.ibatis.submitted.rounding.User" id="usermap">
		<id column="id" property="id"/>
		<result column="name" property="name"/>
		<result column="funkyNumber" property="funkyNumber"/>
		<result column="roundingMode" property="roundingMode"/>
	</resultMap>

	<select id="getUser" resultMap="usermap">
		select * from users
	</select>
	<insert id="insert">
	    insert into users (id, name, funkyNumber, roundingMode) values (
	    	#{id}, #{name}, #{funkyNumber}, #{roundingMode}
	    )
	</insert>

	<resultMap type="org.apache.ibatis.submitted.rounding.User" id="usermap2">
		<id column="id" property="id"/>
		<result column="name" property="name"/>
		<result column="funkyNumber" property="funkyNumber"/>
		<result column="roundingMode" property="roundingMode" typeHandler="org.apache.ibatis.type.EnumTypeHandler"/>
	</resultMap>
	<select id="getUser2" resultMap="usermap2">
		select * from users2
	</select>
	<insert id="insert2">
	    insert into users2 (id, name, funkyNumber, roundingMode) values (
	    	#{id}, #{name}, #{funkyNumber}, #{roundingMode, typeHandler=org.apache.ibatis.type.EnumTypeHandler}
	    )
	</insert>

</mapper>
```
* 注意，这里的 select 语句强制使用 resultMap 来代替 resultType。

### 对象工厂
* MyBatis 每次创建结果对象的新的实例，它都会使用一个对象工厂（ObjectFactory）实例来完成。默认的对象工厂需要做的仅是实例化目标类，要么通过默认构造方法，要么在参数映射存在的时候通过参数构造方法来实例化。如果想覆盖对象工厂的默认行为，则可以通过创建自己的对象工厂来实现，比如:
```java
//ExampleObjectFactory.java
public class ExampleObjectFactory extends DefaultOBjectFactory {
    public Object create(Class type) {
        return super.create(type);
    }

    public Object create(Class type, List<Class> constructorArgTypes, List<Object> constructorArgs) {
        return super.create(type, constructorArgTyps, constructorArgs);
    }

    public void setProperties(Properties properties) {
        super.steProperties(properties);
    }

    public <T> isCollection(Class<T> type) {
        return Collection.class.isAssignableFrom(type);
    } 
}
```

```xml
<!-- mybatis-config.xml -->
<objectFactory type="org.mybatis.example.ExampleObjectFactory">
    <property name="someProperty" value="100"/>
</objectFactory>
```
* Object 接口很简单，它包含两个创建的方法，一个是处理默认构造的方法的，另外一个是处理带参数的构造方法。最后，setPropertes 方法可以用来
配置 ObjectFactory 实例后， objectFactory 元素提体中定义的属性会被传递给 setProperties 方法。

### 插件（Plugins）
* MyBatis 允许在已映射语句执行过程中某一个点进行拦截调用。默认情况下，MyBatis允许使用插件来拦截的方法包括：
  * Executor (update, query, flushStatements, commit, rollback, getTransaction, close, isClosed)
  * ParameterHandler (getParameterObject, setParameters)
  * ResultSetHandler (handleResultSets, handleOutputParameters)
  * StatementHandler (prepare, parameterize, batch, update, query)
* 这些类中方法的谢姐可听过查看每个方法的签名来发现，或者直接查看 MyBatis 发行包中的源代码，如果你想做的不仅仅是方法监控的调用，那么你最好相当了解要重写的方法的行为。因为如果在视图修改或重写已有方法的行为的时候，你可能在破坏 MyBaits 的核心模块。这些都是更底层的类和方法，所以使用插件的时候要特别当心。
* MyBatis 提供的强大机制，使用插件是非常简单的， 只需要实现 Interceptor 接口，并制定想要的方法签名即可。
```java
@Intercepts({
        @Signature(type = Executor.class,
                method = "update",
                args = {MappedStatement.class, Object.class})})
public class ExamplePlugin implements Interceptor {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private Properties properties = new Properties();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // implement pre processing if need
        Object returnObject = invocation.proceed();
        // implement post processing if need
        logger.info("post invocation: {}", invocation);
        logger.info("post returnObject: {}", returnObject);
        logger.info("post properties: {}", properties);
        return returnObject;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
```
```xml
<plugins>
    <plugin interceptor="cn.edu.cqvie.plugin.ExamplePlugin">
        <property name="someProperty" value="100"/>
    </plugin>
</plugins>
```
* 上面的插件将会拦截 Executor 实例中所有的 "update" 方法调用，这里的 Executor 是负责执行底层映射语句的内部对象。
* **覆盖配置类**, 除了用插件来修改 MyBatis 核心行为之外，还可以通过完全覆盖配置类来达到目的。只需继承后覆盖其中的每个方法，再把它传递到 SqlSessionFactoryBuilder.build(myConfig) 方法即可。再次重申，这可能会严重影响 MyBatis 的行为，务请慎之又慎。


### 环境配置（environments）
* 环境管理设置

* 事务管理器设置

* 数据源设置， 
  * dataSource 元素使用标准的 JDBC 数据源接口来配置 JDBC连接对象的资源
    *  许多MyBatis 的应用程序会按照示例来配置数据源。虽然是可选的，单位了使用延迟加载，数据源是必须配置的。
  * 有三种内置数据源类型（也就是 type = "[UNPOOLED| POOLED| JNDI]"）
  * **UNPOOLED**, 这个数据源的实现只是每次被请求时打开和关闭连接，虽然有点慢，但是对于数据库连接可用性方苗苗没有太高的程序来说
是一个很好的选择。不同的数据库在心梗方面的表现也是不一样的，对于某些数据库来说，使用连接池并不重要，这个配置很适合这种情形。UNPOOlED 数据源具有以下属性：
    * driver – 这是 JDBC 驱动的 Java 类的完全限定名（并不是 JDBC 驱动中可能包含的数据源类）。
    * url – 这是数据库的 JDBC URL 地址。
    * username – 登录数据库的用户名。
    * password – 登录数据库的密码。
    * defaultTransactionIsolationLevel – 默认的连接事务隔离级别。

  * **POOLED**, 这种数据源实现了 "池" 的概念将 JDBC 连接对象组织起来了，避免了创建新的连接实例时所必须的初始化和认证时间。这是一种使并发 WEB 应用快速响应请求的流行处理方式。
    * poolMaximumActiveConnections – 在任意时间可以存在的活动（也就是正在使用）连接数量，默认值：10
    * poolMaximumIdleConnections – 任意时间可能存在的空闲连接数。
    * poolMaximumCheckoutTime – 在被强制返回之前，池中连接被检出（checked out）时间，默认值：20000 毫秒（即 20 秒）
    * poolTimeToWait – 这是一个底层设置，如果获取连接花费了相当长的时间，连接池会打印状态日志并重新尝试获取一个连接（避免在误配置的情况下一直安静的失败），默认值：20000 毫秒（即 20 秒）。
    * poolMaximumLocalBadConnectionTolerance – 这是一个关于坏连接容忍度的底层设置， 作用于每一个尝试从缓存池获取连接的线程。 如果这个线程获取到的是一个坏的连接，那么这个数据源允许这个线程尝试重新获取一个新的连接，但是这个重新尝试的次数不应该超过 poolMaximumIdleConnections 与 poolMaximumLocalBadConnectionTolerance 之和。 默认值：3 （新增于 3.4.5）
    * poolPingQuery – 发送到数据库的侦测查询，用来检验连接是否正常工作并准备接受请求。默认是“NO PING QUERY SET”，这会导致多数数据库驱动失败时带有一个恰当的错误消息。
    * poolPingEnabled – 是否启用侦测查询。若开启，需要设置 poolPingQuery 属性为一个可执行的 SQL 语句（最好是一个速度非常快的 SQL 语句），默认值：false。
    * poolPingConnectionsNotUsedFor – 配置 poolPingQuery 的频率。可以被设置为和数据库连接超时时间一样，来避免不必要的侦测，默认值：0（即所有连接每一时刻都被侦测 — 当然仅当 poolPingEnabled 为 true 时适用）。

  * **JNDI**,  这个数据源的实现是为了能在如 EJB 或应用服务器这类容器中使用，容器可以集中或在外部配置数据源，然后放置一个 JNDI 上下文的引用。 这种数据源配置只需要两个属性：
     * initial_context – 这个属性用来在 InitialContext 中寻找上下文（即，initialContext.lookup(initial_context)）。这是个可选属性，如果忽略，那么将会直接从 InitialContext 中寻找 data_source 属性。
     * data_source – 这是引用数据源实例位置的上下文的路径。提供了 initial_context 配置时会在其返回的上下文中进行查找，没有提供时则直接在 InitialContext 中查找。


  * 数据源配置示例：
```xml
<!-- 配置数据源（连接池） -->
<dataSource type="POOLED">
    <!-- 配置数据库的4个基本信息 -->
    <property name="driver" value="${driver}"/>
    <property name="url" value="${url}"/>
    <property name="username" value="${username}"/>
    <property name="password" value="${password}"/>
</dataSource>
```


### 数据库厂商标识 （databaseIdProvider）
* MyBatis 可以根据不同的数据库厂商执行不同的语句，这种多厂商的支持是基于映射语句中的 databaseId 属性。 MyBatis 会加载不带 databaseId 属性和带有匹配当前数据库 databaseId 属性的所有语句。 如果同时找到带有 databaseId 和不带 databaseId 的相同语句，则后者会被舍弃。 为支持多厂商特性只要像下面这样在 mybatis-config.xml 文件中加入 databaseIdProvider 即可：
```xml
<databaseIdProvider type="DB_VENDOR" />
```
* DB_VENDOR 对应的 databaseIdProvider 实现会将 databaseId 设置为 DatabaseMetaData#getDatabaseProductName() 返回的字符串。 由于通常情况下这些字符串都非常长而且相同产品的不同版本会返回不同的值，所以你可能想通过设置属性别名来使其变短，如下：
```xml
<databaseIdProvider type="DB_VENDOR">
  <property name="SQL Server" value="sqlserver"/>
  <property name="DB2" value="db2"/>
  <property name="Oracle" value="oracle" />
</databaseIdProvider>
```
* 在提供了属性别名时，DB_VENDOR 的 databaseIdProvider 实现会将 databaseId 设置为第一个数据库产品名与属性中的名称相匹配的值，如果没有匹配的属性将会设置为 “null”。 在这个例子中，如果 getDatabaseProductName() 返回“Oracle (DataDirect)”，databaseId 将被设置为“oracle”。

* 你可以通过实现接口 org.apache.ibatis.mapping.DatabaseIdProvider 并在 mybatis-config.xml 中注册来构建自己的 DatabaseIdProvider：
```java
public interface DatabaseIdProvider {
  default void setProperties(Properties p) { // Since 3.5.2, change to default method
    // NOP
  }
  String getDatabaseId(DataSource dataSource) throws SQLException;
}
```


### <a name="mappers">映射器（mappers）</a>
* 当我们配置完上述元素过后，我们现在需要定义SQL语句了， 首先我们需要告诉MyBatis 到哪里可以找到咱们的这些语句。 Java在自动查找这方面没有提供
一个比较好方法，所以最佳的方式是告诉mybatis到哪里去找到映射文件。你可以使用相对于类路径的引用，或者完全限定资源定位符（包括：file:// 的 URL），或类名包名等
```xml
<!-- 将包内的映射接口全部注册为映射器(实际开发中最常使用) -->
<mappers>
    <package name="cn.edu.cqvie.example.mapper"/>
</mappers>
```

```xml
<!-- 使用相对于类路径的资源引用 -->
<mappers>
    <mapper resource="cn/edu/cqvie/example/mapper/BlogMapper.xml"/>
    <mapper resource="cn/edu/cqvie/example/mapper/AuthorMapper.xml"/>
</mappers>
```

```xml
<!-- 使用完全限定资源定位符 -->
<mappers>
    <mapper url="file:///var/mapper/mapper/BlogMapper.xml"/>
    <mapper url="file:///var/mapper/mapper/AuthorMapper.xml"/>
</mappers>
```

```xml
<!-- 使用映射器接口实现类的完全限定名 -->
<mappers>
    <mapper class="cn.edu.cqvie.example.mapper.BlogMapper"/>
    <mapper class="cn.edu.cqvie.example.mapper.AuthorMapper"/>
</mappers>
```


[MyBatis XML 文件映射 (next)](https://github.com/zhengsh/document/blob/master/notes/mybatis/3_MyBatis_XML%E6%98%A0%E5%B0%84%E6%96%87%E4%BB%B6.md "MyBatis XML 文件映射")
