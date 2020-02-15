### 动态sql
* MyBatis 的强大特征之一便是它的动态SQL，如果你有使用JDBC或者类似框架的经验。你就会体会到根据不同条件拼接SQL的痛苦。例如拼接是要确保不能忘记必要的空格，还要注意去掉最后一个列的逗号。利用动态SQL这一特征可以彻底摆脱这种痛苦。

* 虽然在以前使用动态SQL并非是一件易事。 但正是MyBatis 提供了可以被正在任意SQL 映射语句中的强大动态SQL语言得以改进这种情况。

* 动态SQL元素的 JSTL 或基于 XML的文本处理器相似。 在MyBatis 之前版本中，有很多元素需要花时间了解。 MyBatis 3 大大的精简了元素种类，现在只需要学习以前的一半元素即可。MyBatis 采用功能更加强大的基于 OGNL的表达式来淘汰大部分元素

  * if 
  * choose (when, otherwise)
  * trim(where, set)
  * foreach

### if 
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

### choose (when, otherwise)
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

### trim(where, set)


### foreach

### script

### bind

### 多数据库支持

### 动态 SQL 中的可插拔脚本语言