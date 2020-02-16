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
* // todo

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

### 环境配置（environments）

### 数据库厂商标识 （databaseIdProvider）

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
