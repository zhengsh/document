### 动态sql
* MyBatis 的强大特征之一便是它的动态SQL，如果你有使用JDBC或者类似框架的经验。你就会体会到根据不同条件拼接SQL的痛苦。例如拼接是要确保不能忘记必要的空格，还要注意去掉最后一个列的逗号。利用动态SQL这一特征可以彻底摆脱这种痛苦。

* 虽然在以前使用动态SQL并非是一件易事。 但正是MyBatis 提供了可以被正在任意SQL 映射语句中的强大动态SQL语言得以改进这种情况。

* 动态SQL元素的 JSTL 或基于 XML的文本处理器相似。 在MyBatis 之前版本中，有很多元素需要花时间了解。 MyBatis 3 大大的精简了元素种类，现在只需要学习以前的一半元素即可。MyBatis 采用功能更加强大的基于 OGNL的表达式来淘汰大部分元素

  * <a href="if">if</a>
  * <a href="choose">choose (when, otherwise)</a>
  * <a href="trim">trim(where, set)</a>
  * <a href="foreach">foreach</a>

### <a name="if">if</a>
动态SQL通常要做的事情是更具条件包含where子句的一部分。比如：
```xml
<sql id="defaultSelect">
    select * from ${alias}
</sql>
<!-- 根据传入参数条件-->
<select id="findUserByCondition" resultMap="blogMap" parameterType="blog">
    <include refid="defaultSelect">
        <property name="alias" value="blog"/>
    </include>
    <where>
        <if test="name != null and name != ''">
            and `name` = #{name}
        </if>
        <if test="title != null">
            and `title` = #{title}
        </if>
    </where>
</select>
```
* 这语句提供了一种可选的查询文本功能， 如果没有传 name 或者传入 name 是空字符串。BLOG表中的全部数据都会返回。

### <a name="choose">choose (when, otherwise)</a>
* 有时我们不想应用查询到所有的条件，而是想从中选择其一个项目。 针对这种情况，MyBatis 提供了 choose 元素，它有点像 Java 中的 switch 语句。 

* 还是上面的例子，我们可以为 name 字段添加一个默认的查询条件，如果当 name 字段不为空的时候我们将调用条件查询，如果当name 等于 null 的时候我么就去查询 "HuaZhang" 的数据
```xml
<!-- 根据传入参数条件-->
<select id="findUserByCondition" resultMap="blogMap" parameterType="blog">
    <include refid="defaultSelect">
        <property name="alias" value="blog"/>
    </include>
    <where>
        <choose>
            <when test="name != null">
                and `name` = #{name}
            </when>
            <otherwise>
                and `name`='HuaZhang'
            </otherwise>
        </choose>
    </where>
</select>
```

### <a name="trim">trim(where, set)</a>
* 前面几个例子已经解决了一个动态SQL的问题，现在回到 "if" 的实例如果查询条件中无固定的条件那么会发生什么问题呢?
```xml
<!-- 根据条件模糊查询 -->
<select id="findBlogLike" parameterType="blog" resultType="blog">
    select * from blog where
    <if test="name != null">
        `name` like #{name}
    </if>
    <if test="title != null">
       and `title` like #{title}
    </if>
</select>
```
* 如果这些条件没有一个能够匹配上会出现如下这条SQL：
```sql
select * from blog where
```
* 这会导致查询失败，如果仅仅第二个条件匹配又会怎么样呢？
```sql
select * from blog where and `title` like 'someTitle'
```
* 这样会导致查询失败。 这个问题不能用简单的条件语句来解决，你也能犯过类似的问题，但是以后再也没有这样做过了
* MyBatis 有一个简单的处理，这个在 90% 的情况下都会有用。而在不能使用的地方，你可以自定义处理方式令其正常工作，简单的处理如下：
```xml
<select id="findBlogLike" parameterType="blog" resultType="blog">
    select * from blog
    <where>
        <if test="name != null">
        and `name` like #{name}
        </if>
        <if test="title != null">
        and `title` like #{title}
        </if>
    </where>
</select>
```
* where 元素只会在至少有一个子元素的条件返回SQL子句的情况下插入 ”where“ 子句。而且，若语句的开头为 ”AND“ 或者 ”OR“ where 子句会将其去掉。
* 如果我们不用 where 标签，我们可以自定义 trim 元素来定制 where 元素的功能， 做如下的等价操作：
```xml
<trim prefix="where" prefixOverrides="AND |OR ">
</trim>
```
* prefixOverrides 属性会忽略管道分隔文本序列（注意此例子中的空格也是必须的）。他的作用是移除 prefixOverrides 属性中的内容，并且插入 prefix 属性中指定的内容
* 类似的动态语句的解决方案叫做 set 。 set 元素可以用于动态包含需要更新得列，而舍弃其他的。比如
```xml
<!-- 更新 -->
<update id="update" parameterType="blog">
    update blog
    <set>
        <if test="name != null and name != ''">
            `name` = #{name},
        </if>
        <if test="title != null and title != ''">
            title = #{title},
        </if>
        <if test="content != null and content != ''">
            content = #{content},
        </if>
    </set>
        where id = #{id}
</update>
```
* 这里 set 关键字，同时也会**删除无关的逗号，我们需要在条件语句后可能会生成SQL的后面增加逗号。**
* set 等价的自定义元素 trim 元素代码如下：
```xml
<trim prefix="SET" suffixOverrides=",">
    ...
</trim>
```

### <a name="foreach">foreach</a>
* 动态SQL的另外一个常用的操作需求是对一个集合进行遍历，通常是在构建 IN 条件语句的时候。比如
```xml
<!-- 根据VO中的ID集合来查询列表 -->
<select id="findBlogInIds" resultMap="blogMap" resultType="queryVO">
    select * from blog
    <where>
        <if test="ids != null and ids.size() > 0">
            <foreach collection="ids" open="and id in (" close=")" item="id" separator=",">
                #{id}
            </foreach>
        </if>
    </where>
</select>
```
* foreach 元素的功能非常强大，它允许你指定一个集合，声明元素体内的集合项（item）和索引（index）变量。它也允许你指定开头与结尾
字符串以及在迭代结果之间放置分隔符。这个元素是很只能的。因此它不会偶然的附加多余的分隔符。
* **我们可以将任何迭代对象（如 List、Set 等）、Map 对象或者数组对象传递给 foreach 作为集合参数。当使用可迭代对象或数组时， index 是当前迭代次数， item 的值是本次迭代获取的元素。当前使用Map 对象（或 Map.Entry 对象的集合）时， index 是键，item 是值**

### script
* 要在带注解的映射器接口类中使用动态SQL，可以使用 script 元素。比如：
```java
@Update({"<script>",
    "update blog",
    "  <set>",
    "    <if test=\" name != null and name != '' \">",
    "      name = #{name},",
    "    </if>",
    "    <if test=\" title != null and title != '' \">",
    "      title = #{title},",
    "    </if>",
    "    <if test=\" content != null and content != '' \">",
    "      content = #{content},",
    "    </if>",
    "  </set>",
    "where id = #{id}",
    "</script>"})
void update(Blog blog);
```

### bind
* bind 元素可以从 OGNL 表达式中创建一个变量并且绑定到上下文。比如：
```xml
<select id="findUserByCondition" resultMap="blogMap" parameterType="blog">
    <bind name="name" value="'%' + _parameter.name + '%'"></bind>
    select * from blog
    <where>
        <if test="name != null and name != ''">
            and `name` like #{name}
        </if>
</select>
```

### 多数据库支持
* 一个配置了 "_database_id" 变量 databaseIdProvider 可用于动态代码中，这样就可以更具不同的数据库厂商构建特定的语句。比如下面的例子：
```xml
<insert id="insert">
    <selectKey keyProperty="id" resultType="int" order="BEFORE">
        <if test="_databaseId == 'oracle'">
            select seq_blog.nextval form dual
        </if>
        <if test="_databaseId == 'db2'">
            select nextval for seq_blog form sysibm.sysdummy1
        </if>
    </selectKey>
    insert into blog (`id`, `name`, `title`, `content`) values (#{id}, #{name}, #{title}, #{content})
</insert>
```

### 动态 SQL 中的可插拔脚本语言（了解）
* MyBatis 3.2开始支持可插拔的脚本语言，这允许咱们插入一种脚本语言驱动，并给予这种驱动来编写动态SQL查询语句
* 可以通过实现以下接口来插入一种语言。
```java
public interface LanguageDriver {
  ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql);
  SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType);
  SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType);
}
```
* 一旦设置了自定义语言驱动，可以在 mybatis-config.xml 文件中将它设置为默认语言
```xml
<typeAliases>
    <typeAlias name="cn.edu.cqvie.MyLanguage" alias="myLanguage"></typeAlias>
</typeAliases>
<settings>
    <setting name="defaultScriptLanguage" value="myLanguage"/>
</settings>
```
* 特殊的查询语句指定特定语言查询，可以通过 lang 属性来完成。
```xml
<select id="findAll" lang="myLanguage">
    select * from blog
</select>
```
* 或者，如果你使用的是接口映射器的接口类。可以在抽象方法上加上 @Lang 注解即可
```java
public interface Mapper {
    @Lang(MyLanguage.class)
    @Select("select * from blog")
    List<Blig> fingAll();
}
```
* **可以将 Apache Velocity 作为动态语言来使用，跟多的细节 可以参考 MyBatis-Velocity**
* 在咱们之前所看到的所有的 xml 标签都是默认由 MyBatis 语言提供的， 而它由别名为 xml 的语言驱动器 
org.apache.ibatis.scripting.xmltags.XMLLanguageDriver 所提供。


[MyBatis JAVA API (next)](https://github.com/zhengsh/document/blob/master/notes/mybatis/5_Mybatis_Java_API.md "MyBatis JAVA API")