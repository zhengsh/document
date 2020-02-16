# XML 映射文件
* MyBatis 的真真强大在于他的映射语句，这是它的魔力所在。由于它的异常强大，映射器的XML文件就显得相对简单。如果拿它跟具有相同功能的JDBC
代码对比，你会立即发现省掉了将近95%的代码。MyBatis 为聚焦SQL而构建，尽可能的为你减少麻烦。

SQL 映射文件只有很少的的几个（按照被定义的顺序列出）
  * cache - 对给定命名空间的缓存配置
  * cache-ref - 对给定的命名空间缓存配置的引用
  * resultMap - 是最复杂也是最强大的元素，用来描述如何从数据库结果集中来加载对象
  * <a href="sql">sql - 可被其他语句引用的可重用语句块</a>
  * insert - 映射插入语句
  * update - 映射更新语句
  * delete - 映射删除语句
  * <a href="select">select - 映射查询语句</a>

### <a name="select">select </a>
* 查询语句是最长用的元素之一，是用来查询数据。它复杂的点在于查询结果集的映射，但是经过MyBaits 的处理一切都变得非常的简单，比如：
```xml
<select id="findById"  parametreType="int" resultType="blog">
    select * from blog where `id` = #{id}
</select>
```
* 这个语句用来通过唯一ID查询，接受一个int 类型的参数id, 返回一个 Blog 类型的对象，MyBatis 会自动把对应的结果列封装到 Blog 对象的对应字段
```ognl
#{id}
```
* 这就告诉 MyBatis 创建一个预处理语句 （PreparedStatement）参数，在JDBC中，这样的一个参数在SLQ中会由一个 "?" 来标识，并被传递到一个新的预处理语句中，如下：
```java
//近似的 JDBC 代码， 非 MyBatis 代码
String sql = "select * from blog id = ?";
PrepareStatement ps = conn.prepareStatement(queryString);
ps.setInt(1, id);
```
* 当然， 如果我们使用JDBC来实现需要我们写更多的代码带来提取结果集并且封装到对象实例中，这就是 MyBatis 能为你带来便利的地方。
```xml
<select 
    id="findById"
    parameterType="int"
    parameterMap="deprecated"
    resultType="blog"
    resultMap="resultMap"
    flushCache="false"
    useCache="true"
    timeout="10"
    fetchSzie="256"
    statementType="PREPARED"
    resultSetType="FORWORD_ONLY"
>
```
* 属性配置配置解释 （部分解释）
  * id ,  在命名空间中唯一的标识符，可以用来引用这条语句和Mapper接口中method name 相同。
  * parameterType , 将会传入这条语句的参数类的完全限定名或别名，这个是可选的属性，因为MyBatis 可以通过类型处理器 （TypeHandler）推断出具体传入语句的参数，默认值为未设置（unset）。
  * resultType , 从这条语句中返回期望类型的完全限定名或别名，注意如果返回的是集合，那应该设置为集合类型包含的类型，而不是集合本身。可以使用resultType 或 resultMap , 但是不能同时使用
  * resultMap , 外部 resultMap 的命名引用。 结果集映射是 MyBatis 最强大的特征，如果你对其理解透彻，很多复杂的问题都可以迎刃而解。可以使用resultType 或 resultMap , 但是不能同时使用
  * useCache , 将其设置为 ture后, 将会导致本条语句的结果被**二级缓存缓存**起来， 默认值：**对select 元素为true**
  * flushCache , 将其设置为 ture, 只要语句被调用，都会导致本地缓存和二级缓存都被清空，默认值为： false

### insert, update 和 delete 

* 数据变更语句 insert, update 和 delete 非常接近, 包含如下属性大多属性和select类似本处不再解释
```xml
<insert 
    id="insert"
    parameterType="cn.edu.cqve.dto.Blog"
    flushCache="true" //默认值为 true (所以我们当我们执行了insert, update 和 delete 后会更新缓存)
    statementType="PREPARED"
    keyProperty=""
    keyColumn=""
    userGeneratedKeys=""
    timeout="20"
    >

<update 
    id="update" 
    parameterType="cn.edu.cqve.dto.Blog"
    flushCache="true" //默认值为 true (所以我们当我们执行了insert, update 和 delete 后会更新缓存)
    statementType="PREPARED"
    timeout="20"
    >

<delete 
    id="delete" 
    parameterType="cn.edu.cqve.dto.Blog"
    flushCache="true" //默认值为 true (所以我们当我们执行了insert, update 和 delete 后会更新缓存)
    statementType="PREPARED"
    timeout="20"
    >
```

* 下面是 insert, update 和 delete 语句示例
```xml
<!-- 新增 -->
<insert id="insert" parameterType="blog">
    insert into blog (`id`, `name`, `title`, `content`)
    values (#{id}, #{name}, #{title}, #{content})
</insert>

<!-- 更新 -->
<update id="update" parameterType="blog">
    update blog
    set `name` = #{name}, `title` = #{title}, `content` = #{title}
    where id = #{id}
</update>

<!-- 删除 -->
<delete id="delete" parameterType="int">
    delete from blog where id = #{id}
</delete>
```
* 如上所示插入的配置规则更加风戽，在插入语句里面有一些额外的属性和子元素用来处理主键生成，而且有多种方式生成。
* 如果数据库支持自动生成主键字段（比如 MySQL 和 SqlServer），那么我们可以设置 useGeneratedKeys="true" 
keyProperty="id" 如果 Blog 表已经使用了id 自动生成的列，可以将语句修改为
```xml
<insert id="insert" parameterType="blog" useGeneratedKeys="true" keyProperty="id">
    insert into blog (`id`, `name`, `title`, `content`)
    values (#{id}, #{name}, #{title}, #{content})
</insert>
```
* 如果需要插入多列可以传入一个数组或者集合，并且返回自动生成的主键列
```xml
<insert id="batchInsert" parameterType="blog" useGeneratedKeys="true" keyProperty="id">
    insert into blog (`id`, `name`, `title`, `content`) values 
    <foreach item="item" collection="list" separator=",">
        (#{id}, #{name}, #{title}, #{content})
    </foreach>
</insert>
```
* 对于不支持自动生成的数据库或不能支持自动生成主键的JDBC驱动， MyBatis 有另外一种方法来生成主键
* 下面有一个简单的案例，**本处的主键生成策略只是作为演示在实际中是不可取的**。
```xml
<insert id="batchInsert" parameterType="blog" useGeneratedKeys="true" keyProperty="id">
    <selectKey keyProperty="id" resultType="int" order="BEFORE">
    select CAST(RANDOM()*1000000 as INTEGER) a from SYSIBM.SYSDUMMY1
    </selectKey>
    insert into blog (`id`, `name`, `title`, `content`) values 
    <foreach item="item" collection="list" separator=",">
        (#{id}, #{name}, #{title}, #{content})
    </foreach>
</insert>
```
* 如果我们需要在**数据写入数据库后，获取主键ID**也可以通过如下方式实现（MySQL 数据库）
```xml
<insert id="insert" parameterType="blog">
    <selectKey resultType="int" keyProperty="id" order="AFTER">
        select last_insert_id()
    </selectKey>
    insert into blog (`id`, `name`, `title`, `content`)
    values (#{id}, #{name}, #{title}, #{content})
</insert>
```
* selectKey 元素描述如下:
```xml
<selectKey
    keyProperty="id" //被设置或者被获取值的目标属性，如果希望得到多个列，用英文逗号分隔属性名称列表
    resultType="int" //匹配属性的返回结果集中的列名称，如果希望得到多个列，用英文逗号分隔属性名称列表
    order="BEFORE"
    statementType="PREPARED"
>
```

### <a name="sql">sql</a>
* 这个元素可以用来定义可重用的SQL代码段，这些SQL代码可以被包含在其他语句中。它可以（在加载的时候）被静态地设置参数。
在不同的包含语句中可以设置不同的值到占位符上。比如：
```xml
<sql id="defaultSelect">
    select * from blog
</sql>
```
* 这个片段可以用在别的语句中，如：
```xml
 <!-- 查询全部 -->
<select id="findAll" resultType="blogDto">
    <include refid="defaultSelect"></include>
    <!-- select * from blog -->
</select>
```
* 添加属性值在 include 元素的 refid 属性中，可以**传递参数**
```xml
<sql id="defaultSelect">
    select * from ${alias}
</sql>


<select id="findAll" resultType="blog">
    <include refid="defaultSelect">
        <property name="alias" value="blog"></property>
    </include>
</select>
```

### 参数
* 你之前见到的所有语句中，使用的都是简单参数。实际上参数是 MyBatis 非常强大的元素。对于简单的使用场景，大约 90% 的情况下你都不需要使用复杂的参数，比如：
```xml
<select id="findById" resultType="blog">
    select * from blog where id = #{id}
</select>
```
* 上面的实例说明了一个非常简单的参数映射，参数类型被设置为 long , 这样这个参数就可以被设置为任何类型。原始类型或简单数据类型（比如： Long 和 String）因为没有相关属性，它会完全用参数值来代替，然而如果传入一个复杂的独享，行为就有点不同了
```xml
<insert id="insert" parameterType="blog" useGeneratedKeys="true" keyProperty="id">
    insert into blog (`id`, `name`, `title`, `content`)
    values (#{id}, #{name}, #{title}, #{content})
</insert>
```
* 如果 Blog 类型的参数中传递到语句中， id, name, title, content 属性将被查找，然后将他们传入预处理的参数中
* 对于向语句中传递的参数来说，这个真是既简单又有效，不过参数映射的功能远不止于此。
* 首先， MyBatis 的其他部分一样，参数也可以指定一个特殊的数据类型，如下:
```
#{property, javaType=int, jdbcType=NUMERIC}
```
* 向MyBatis 的其他部分一样， javaType 几乎总是可以更具参数对象的类型确定下来， 除非该对象是一个 HashMap. 这个时候，你需要显式的指定 javaType 来确保正确的类型处理器（TypeHandler）被使用。

* **JDBC 要求， 如果一个列允许 null 值，并且会传递值 null 的参数，就必须要指定 jdbcType 阅读 PreparedStatement.setNull()的 JavaDoc 文档来获取更多信息。**

* 要更进一步地自定义类型处理方式，你也可以指定一个特殊的类型处理器类（或别名），比如：
```
#{age,javaType=int,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
```
* **字符串替换**，默认情况下 #{} 格式的语法会导致 MyBatis 创建 PreparedStatement 参数占位符并安全地设置参数（就像使用 ？ 一样）。这样做更安全，更迅速，通常也是首选做法，不过有时你就是想直接在SQL 语句中插入一个不转义的字符串比如：
像 ORDER BY , 你可以这样来使用
```
ORDER BY ${columName}
```
* 这里 MyBatis 不会修改或转义字符串
* 当 SQL 语句中的元数据（如表名或列名）是动态生成的时候，字符串替换将会非常有用。 举个例子，如果你想通过任何一列从表中 select 数据时，不需要像下面这样写：
```java
@Select("select * from blog where id = #{id}")
List<Blog> findById(@Param("id") long id);

@Select("select * from blog where name = #{name}")
List<Blog> findByName(@Param("name") String name);

@Select("select * from blog where title = #{title}")
List<Blog> findByTitle(@Param("title") String title);

// and more "findByXxx" method
```
* 可以只写成这样的一个方法
```java
@Select("select * from blog where ${column} = #{value}")
List<Blog> findByColumn(@Param("column") String column, @Param("value") String value);
```
* 其中 ${column} 会被直接替换，而 #{value} 会使用 ？ 预处理。因此你可以像下面这样来达到上述功能。
```java
List<Blog> blogOfId = blogMapper.findByColumn("id", 1L);
List<Blog> blogOfName = blogMapper.findByColumn("name", "name");
List<Blog> blogOfEmail = blogMapper.findByColumn("title", "title");
```
* 这种方法也同样适合于用来替换表
* **注意: 这种方式接受用户的输入，并将其用于语句中的参数是不安全的，会导致潜在的SQL注入工具，因此要么不允许用户输入这些字段，要么自行转义并检验**

### 结果集映射
* resultMap 元素是 MyBatis 中最强大的的元素，他可以让你从 90% 的JDBC ResultSets 数据库提取代码中解放出来，并在一些情况下允许你进行一些 JDBC 不支持的操作。实际上，在位一些比如链接的复杂语句编写映射代码的时候，一份 resultMap 能够替代实际通能功能的长待数千行的代码。ResultMap 的设计事项是，对于简单的语句根本不需要**结果映射**， 而对于复杂一点的语句只需要描述他们之间的关系就可以了。
* 显示指定 resultType 示例 ：（使用别名）
```xml
<!-- mybatis-config.xml 中 -->
<typeAlias type=" cn.edu.cqvie.dto.Blog" alias="blog"/>

<!-- SQL 映射文件中 -->
<select id="findAll" resultType="blog">
    select id, name, content from blog
</select>
```
* 这样的一个 JavaBean 可以被映射到 ResultSet。这种情况是基于数据库字段和 JavaBean字段能够精准匹配的情况，如果无法匹配的话
就会导致结果集映射失败，也可以通过修改查询SQL 字段类的别名来实现。

* ResultMap 最优秀的地方在于，虽然你已经对它相当了解了，但是根本就不需要显式地用到他们。 上面这些简单的示例根本不需要下面这些繁琐的配置。 但出于示范的原因，让我们来看看最后一个示例中，如果使用外部的 resultMap 会怎样，这也是解决列名不匹配的另外一种方式。
```xml
<resultMap type="blog" id="resultMap">
    <id column="id" property="id"/>
    <result column="name" property="name"/>
    <result column="title" property="title"/>
    <result column="content" property="content"/>
</resultMap>
```
* 而在引用它的语句中使用 resultMap 属性就行了（注意我们去掉了 resultType 属性）。比如:
```xml
<select id="select" resultMap="resultMap">
    select id, name, content from blog
</select>
```
#### 高级结果映射
* MyBatis 创建时的一个思想是：数据库不可能永远是你所想或所需的那个样子。 我们希望每个数据库都具备良好的第三范式或 BCNF 范式，可惜它们不总都是这样。 如果能有一种完美的数据库映射模式，所有应用程序都可以使用它，那就太好了，但可惜也没有。 而 ResultMap 就是 MyBatis 对这个问题的答案。
* 一个非常复杂的SQL
```xml
<!-- 非常复杂的语句 -->
<select id="selectBlogDetails" resultMap="detailedBlogResultMap">
  select
       B.id as blog_id,
       B.title as blog_title,
       B.author_id as blog_author_id,
       A.id as author_id,
       A.username as author_username,
       A.password as author_password,
       A.email as author_email,
       A.bio as author_bio,
       A.favourite_section as author_favourite_section,
       P.id as post_id,
       P.blog_id as post_blog_id,
       P.author_id as post_author_id,
       P.created_on as post_created_on,
       P.section as post_section,
       P.subject as post_subject,
       P.draft as draft,
       P.body as post_body,
       C.id as comment_id,
       C.post_id as comment_post_id,
       C.name as comment_name,
       C.comment as comment_text,
       T.id as tag_id,
       T.name as tag_name
  from Blog B
       left outer join Author A on B.author_id = A.id
       left outer join Post P on B.id = P.blog_id
       left outer join Comment C on P.id = C.post_id
       left outer join Post_Tag PT on PT.post_id = P.id
       left outer join Tag T on PT.tag_id = T.id
  where B.id = #{id}
</select>
```
* 你可能想把它映射到一个智能的对象模型，这个对象表示了一篇博客，它由某位作者所写，有很多的博文，每篇博文有零或多条的评论和标签。 我们来看看下面这个完整的例子，它是一个非常复杂的结果映射（假设作者，博客，博文，评论和标签都是类型别名）。 不用紧张，我们会一步一步来说明。虽然它看起来令人望而生畏，但其实非常简单。
```xml
<!-- 非常复杂的结果映射 -->
<resultMap id="detailedBlogResultMap" type="Blog">
  <constructor>
    <idArg column="blog_id" javaType="int"/>
  </constructor>
  <result property="title" column="blog_title"/>
  <association property="author" javaType="Author">
    <id property="id" column="author_id"/>
    <result property="username" column="author_username"/>
    <result property="password" column="author_password"/>
    <result property="email" column="author_email"/>
    <result property="bio" column="author_bio"/>
    <result property="favouriteSection" column="author_favourite_section"/>
  </association>
  <collection property="posts" ofType="Post">
    <id property="id" column="post_id"/>
    <result property="subject" column="post_subject"/>
    <association property="author" javaType="Author"/>
    <collection property="comments" ofType="Comment">
      <id property="id" column="comment_id"/>
    </collection>
    <collection property="tags" ofType="Tag" >
      <id property="id" column="tag_id"/>
    </collection>
    <discriminator javaType="int" column="draft">
      <case value="1" resultType="DraftPost"/>
    </discriminator>
  </collection>
</resultMap>
```
* resultMap 元素有很多子元素和一个值得深入探讨的结构。 下面是resultMap 元素的概念视图。

#### 结果映射（resultMap）
* constructor - 用于在实例化类时，注入结果到构造方法中
  * idArg - ID 参数；标记出作为 ID 的结果可以帮助提高整体性能
  * arg - 将被注入到构造方法的一个普通结果
* id – 一个 ID 结果；标记出作为 ID 的结果可以帮助提高整体性能
* result – 注入到字段或 JavaBean 属性的普通结果
* association – 一个复杂类型的关联；许多结果将包装成这种类型
  * 嵌套结果映射 – 关联本身可以是一个 resultMap 元素，或者从别处引用一个
* collection – 一个复杂类型的集合
  * 嵌套结果映射 – 集合本身可以是一个 resultMap 元素，或者从别处引用一个
* dcriminator – 使用结果值来决定使用哪个 resultMap
  * case – 基于某些值的结果映射
    * 嵌套结果映射 – case 本身可以是一个 resultMap 元素，因此可以具有相同的结构和元素，或者从别处引用一个

##### 关联 (One-To-One)
```xml
<!-- ResultMap -->
<resultMap id="blogAuthorMap" type="blog">
  <id property="id" column="id"/>
  <result column="name" property="name"/>
  <result column="title" property="title"/>
  <result column="content" property="content"/>
  <result column="author_id" property="authorId"/>
  <!-- 一对一关系映射，配置封装author的内容 -->
  <association property="author" javaType="author">
      <id property="id" column="id"/>
      <result property="username" column="username"/>
      <result property="password" column="password"/>
      <result property="email" column="email"/>
      <result property="blo" column="blo"/>
  </association>
</resultMap>

<!--  -->
<select id="findAll" resultMap="blogAuthorMap">
    select b.*, a.username, a.email, a.blo from blog b, author a where b.author_id = a.id
</select>
```
* 关联（association）元素处理“有一个”类型的关系。 比如，在我们的示例中，一个博客有一个用户。关联结果映射和其它类型的映射工作方式差不多。 你需要指定目标属性名以及属性的javaType（很多时候 MyBatis 可以自己推断出来），在必要的情况下你还可以设置 JDBC 类型，如果你想覆盖获取结果值的过程，还可以设置类型处理器。

* 关联的不同之处是，你需要告诉 MyBatis 如何加载关联。MyBatis 有两种不同的方式加载关联：
  * 嵌套 Select 查询：通过执行另外一个 SQL 映射语句来加载期望的复杂类型。
  * 嵌套结果映射：使用嵌套的结果映射来处理连接结果的重复子集。

##### 关联的嵌套 Select 查询
```xml
<resultMap id="blogAuthorMap" type="blog">
  <id property="id" column="id"/>
  <result column="name" property="name"/>
  <result column="title" property="title"/>
  <result column="content" property="content"/>
  <result column="author_id" property="authorId"/>
  <!-- 一对一关系映射，配置封装author的内容 -->
  <association property="author" column="author_id" javaType="author" select="findAuthorById"/> 
</resultMap>

<select id="findAuthorById" resultType="author">
    select * author where where id = #{id}
</select>

<select id="findBlogAll" resultMap="blogAuthorMap">
    select * from blog
</select>
```
* 就是这么简单。我们有两个 select 查询语句：一个用来加载博客（Blog），另外一个用来加载作者（Author），而且博客的结果映射描述了应该使用 selectAuthor 语句加载它的 author 属性。
* 其它所有的属性将会被自动加载，只要它们的列名和属性名相匹配。
* 这种方式虽然很简单，但在大型数据集或大型数据表上表现不佳。这个问题被称为“N+1 查询问题”。 概括地讲，N+1 查询问题是这样子的：
  * 你执行了一个单独的SQL语句来获取一个列表（就是 "+1"）
  * 对列表返回的每一条记录，你执行了一个select 语句来为每条记录加载详细信息（这就是 "N"）
* 这个问题会导致成百上千的 SQL 语句被执行。有时候，我们不希望产生这样的后果。
* 好消息是，MyBatis 能够对这样的查询进行延迟加载，因此可以将大量语句同时运行的开销分散开来。 然而，如果你加载记录列表之后立刻就遍历列表以获取嵌套的数据，就会触发所有的延迟加载查询，性能可能会变得很糟糕。
* 所以我们建议使用上面中方式“关联的嵌套结果映射”

##### 关联 (One-To-Many）
``` xml

<!-- ResultMap -->
<resultMap id="authorBlogMap" type="author">
  <id column="id" property="id"/>
  <result column="username" property="username"/>
  <result column="password" property="password"/>
  <result column="email" property="email"/>
  <result column="blo" property="blo"/>

  <!-- 配置author用户中blog集合的映射 -->
  <collection property="blogList" ofType="blog">
      <!-- aid 是blog.id 的一个别名 -->
      <id column="aid" property="id"/>
      <result column="name" property="name"/>
      <result column="title" property="title"/>
      <result column="content" property="content"/>
      <result column="author_id" property="authorId"/>
  </collection>
</resultMap>

<!-- select -->
<select id="findAll" resultMap="authorBlogMap">
    select * from author a left outer join blog b on a.id = b.author_id
</select>
```
* 上述我们代码示例中， 一个作者（Author）, 有多篇博客。在作者类中可以通过如下方式来表示
```java
/**
  * 用户博客列表
  */
private List<Blog> blogList;
```

##### 关联 (Many-To-Many)
* 由于MyBatis 并没有提供 多对多的实现方式，那么我们在设计中可以通过一对多来间接实现。如下例：
```xml
<!-- 博客关联博客分类 -->
<resultMap type="blog" id="resultMap">
    <id column="id" property="id"/>
    <result column="name" property="name"/>
    <result column="title" property="title"/>
    <result column="content" property="content"/>
    <!-- 配置分类集合 -->
    <collection property="classificationList" ofType="classification">
        <id column="c_id" property="id"></id>
        <result column="c_name" property="name"></result>
        <result column="remarks" property="remarks"></result>
    </collection>
</resultMap>

<!-- 查询博客列表中带有分类 -->
<select id="findAll" resultMap="resultMap">
select b.*,c.id as c_id,c.name as c_name,c.remarks from blog b
    left outer join blog_classification bc on b.id = bc.blog_id
    left outer join classification c on bc.classification_id = c.id
</select>
```
* 上面的例子中， 一个博客（blog）可能会有多个分类，并且一个分类（blog_classification）下面会有多个博客信息，用博客分类映射表（blog_classification）来表示他们之间的关系，**本处只是为了体现多对多的实现，在实际业务中对于这种查询方式并不值得借鉴**

* **MyBatis 对关联映射，并没有深度，广度或组合上的要求。但是在映射时需要留意性能问题，在探索最佳实践的过程中，应用的单元测试和心梗测试会是你的好帮手，而MyBatis 的好处在于，可以在不对你的代码引入重大变更（如果有）的情况下，允许你之后改变你的想法。**
* 高级关联和集合映射是一个深度话题。文档的介绍只能到此为止。配合少许的实践，你会很快了解全部的用法。

### 自动映射
* 正如你在前面一节看到的，在简单的场景下，MyBatis 可以为你自动映射查询结果。但如果遇到复杂的场景，你需要构建一个结果映射。 但是在本节中，你将看到，你可以混合使用这两种策略。让我们深入了解一下自动映射是怎样工作的。
* 当自动映射查询结果时，MyBatis 会获取结果中返回的列名并在 Java 类中查找相同名字的属性（忽略大小写）。 这意味着如果发现了 ID 列和 id 属性，MyBatis 会将列 ID 的值赋给 id 属性。
* 通常数据库列使用大写字母组成的单词命名，单词间用下划线分隔；而 Java 属性一般遵循驼峰命名法约定。为了在这两种命名方式之间启用自动映射，需要将 mapUnderscoreToCamelCase 设置为 true。
* 甚至在提供了结果映射后，自动映射也能工作。在这种情况下，对于每一个结果映射，在 ResultSet 出现的列，如果没有设置手动映射，将被自动映射。在自动映射处理完毕后，再处理手动映射。 在下面的例子中，id 和 userName 列将被自动映射，hashed_password 列将根据配置进行映射。
```xml
<select id="selectUsers" resultMap="userResultMap">
  select
    user_id             as "id",
    user_name           as "userName",
    hashed_password
  from some_table
  where id = #{id}
</select>
```
```xml
<resultMap id="userResultMap" type="User">
  <result property="password" column="hashed_password"/>
</resultMap>
```
* 三种自动映射等级：
  * NONE - 禁用自动映射。仅对手动映射的属性进行映射。
  * PARTIAL - 对除在内部定义了嵌套结果映射（也就是连接的属性）以外的属性进行映射
  * FULL - 自动映射所有属性。
* 默认值是 PARTIAL，这是有原因的。当对连接查询的结果使用 FULL 时，连接查询会在同一行中获取多个不同实体的数据，因此可能导致非预期的映射。 下面的例子将展示这种风险：
```xml
<select id="selectBlog" resultMap="blogResult">
  select
    B.id,
    B.title,
    A.username,
  from Blog B left outer join Author A on B.author_id = A.id
  where B.id = #{id}
</select>
```
```xml
<resultMap id="blogResult" type="Blog">
  <association property="author" resultMap="authorResult"/>
</resultMap>

<resultMap id="authorResult" type="Author">
  <result property="username" column="author_username"/>
</resultMap>
```
* 在该结果映射中，Blog 和 Author 均将被自动映射。但是注意 Author 有一个 id 属性，在 ResultSet 中也有一个名为 id 的列，所以 Author 的 id 将填入 Blog 的 id，这可不是你期望的行为。 所以，要谨慎使用 FULL。

* 无论设置的自动映射等级是哪种，你都可以通过在结果映射上设置 autoMapping 属性来为指定的结果映射设置启用/禁用自动映射。
```xml
<resultMap id="userResultMap" type="User" autoMapping="false">
  <result property="password" column="hashed_password"/>
</resultMap>
```


### 缓存
* MyBatis 内置了一个强大的事务性查询缓存机制，它可以非常方便的配置和定制。为了使它更加强大而且易于配置，MyBatis 3 中的缓存做了许多改进。
* 默认情况下，值启用了本地会话缓存，它仅仅对一个会话中的数据进行缓存。要启用全局的二级缓存，只需要在SQL的配置文件中添加。
```xml
<cache />
```
* 基本上是这样。这个简单的语句有如下的效果：
  * 映射语句中所的有 select 语句的结果集将被缓存。
  * 映射语句汇文件中的所有 insert、update、和 delete 语句会被刷新缓存。
  * 缓存会使用最近最少使用算法（LRU，Least Recently Used）算法来清除不需要的缓存
  * 缓存不会定时进行刷新（也就是说，没有刷新间隔）
  * 缓存会保存列表或对象（无论查询方法返回那种）的1024个引用
  * 缓存会被视为 读/写缓存，这意味着获取到的对象并不是共享的，可以安全地被调用者修改，而不敢要其他调用者或者线程的潜在修改。

* **缓存之作用与 cache 标签所在的映射文件的语句。如果你混合使用 Java API 和 XML 映射文件，在共用接口中的语句不会被默认缓存，你需要使用
@CacheNamespaceRef 注解指定缓存作用域

* 这些属性可以通过 cache 元素的属性来修改。比如：
```xml
<cache
    eviction="FIFO"
    flushInterval="60000"
    size="512"
    readOnly="true"/>
>
```
* 这个更干呕记得配置创建了一个FIFO缓存，每间隔60秒刷新，最多可以存储结果对象或者列表512个引用，并且返回单额对象被认为是只读的，因此对它们进行修改可能会在不同线程
中的调用者产生冲突
可用的清除策略有：
  * LRU - 最近最少使用： 移除最长时间不被修改的对象
  * FIFO - 先进先出：按对象进入缓存和顺序来移除它们。
  * SOFT - 软引用：基于垃圾回收器状态和软引用规则移除对象。
  * WEAK - 弱引用：更积极地基于垃圾收集器状态和弱引用规则移除对象。
* 默认的策略 LRU
* flushInterval（刷新间隔）属性可以被设置为任意正整数，设置的值应该是毫秒为单位的合理时间量。默认情况是不设置，也就是没有刷新间隔，缓存仅仅会在调用与时刷新。
* size*（引用数目）属性可以被设置为任意正整数，要注意被缓存的对象大小和运行环境中可用的内存资源。默认值是 1024.
* readOnly （只读）属性可以被设置为 true 或 false . 只读的缓存会给所有的调用者返回缓存对象的相同实例。因此这些对象不能被修改。这就提供了可观的性能提升。而可读写
的缓存会（通过序列化）返回对象的拷贝。速度上会慢一些，但是更加安全，因此默认值是 flase

* **二级缓存是事务性的， 这意味着，当SqlSession 完成并提交时，或是完成并回滚，但没有执行 flushCache=true 的 insert/update/delete 语句时，缓存会获得更新**

[MyBatis 动态SQL (next)](https://github.com/zhengsh/document/blob/master/notes/mybatis/4_MyBatis_%E5%8A%A8%E6%80%81SQL.md "MyBatis 动态SQL")
