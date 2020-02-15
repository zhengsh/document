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


<select id="findAll" resultType="blogDto">
    <include refid="defaultSelect">
        <property name="alias" value="blog"></property>
    </include>
</select>
```

### 参数

### 结果集映射

### 自动映射

### 缓存