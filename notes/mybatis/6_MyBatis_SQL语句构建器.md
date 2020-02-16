### SQL 语句构建器

### 问题
* 面对代码中最痛苦的事情就是在 Java 代码中嵌入 SQL 语句。 这样做的目的通常是为了来动态生成SQL，否则可以将它放到外部文件或者存储过程中。正如我们已经看到的那样，MyBatis 在它的 XML映射特征中有很强大的SQL生成方案，但是有时候 Java 代码呢哦不创建SQL语句也是必要的。此时， MyBatis 的另外一个特征可以帮助到我们，来减少典型的添加加好，引号，新行，格式化问题和嵌入条件来处理多余的逗号或者AND 连接词之前。我们采用Java 来拼接SQL代码其实是非常差的设计， 比如：
```java
String sql = "SELECT P.ID, P.USERNAME, P.PASSWORD, P.FULL_NAME, "
"P.LAST_NAME,P.CREATED_ON, P.UPDATED_ON " +
"FROM PERSON P, ACCOUNT A " +
"INNER JOIN DEPARTMENT D on D.ID = P.DEPARTMENT_ID " +
"INNER JOIN COMPANY C on D.COMPANY_ID = C.ID " +
"WHERE (P.ID = A.ID AND P.FIRST_NAME like ?) " +
"OR (P.LAST_NAME like ?) " +
"GROUP BY P.ID " +
"HAVING (P.LAST_NAME like ?) " +
"OR (P.FIRST_NAME like ?) " +
"ORDER BY P.ID, P.FULL_NAME";
```

### 解决方案
* MyBatis 3 提供了方便的根据类来帮助解决该问题。使用SQL类， 简单地创建一个实例来调用方法生成SQL语句。上面示例中的问题采用SQL类来重写
```java
private String selectPersonSql() {
   return new SQL() {{
    SELECT("P.ID, P.USERNAME, P.PASSWORD, P.FULL_NAME");
    SELECT("P.LAST_NAME, P.CREATED_ON, P.UPDATED_ON");
    FROM("PERSON P");
    FROM("ACCOUNT A");
    INNER_JOIN("DEPARTMENT D on D.ID = P.DEPARTMENT_ID");
    INNER_JOIN("COMPANY C on D.COMPANY_ID = C.ID");
    WHERE("P.ID = A.ID");
    WHERE("P.FIRST_NAME like ?");
    OR();
    WHERE("P.LAST_NAME like ?");
    GROUP_BY("P.ID");
    HAVING("P.LAST_NAME like ?");
    OR();
    HAVING("P.FIRST_NAME like ?");
    ORDER_BY("P.ID");
    ORDER_BY("P.FULL_NAME");
  }}.toString();
}
```
*  这样做的好处是咱们不必担心偶然出现的 "AND" 关键字，或者在 "WHERE" 和 "AND" 之间的选择，抑或者什么都不用选。它已经帮我们完美的解决了。

### SQL类
这里给出一些示例， 
```java

//Provider
public String insertSql() {
    return new SQL()
            .INSERT_INTO("blog")
            .INTO_COLUMNS("id", "name", "title", "content")
            .INTO_VALUES("#{id}", "#{name}", "#{title}", "#{content}")
            .toString();
}

public String updateSql() {
    return new SQL()
            .UPDATE("blog")
            .SET("name = #{name}")
            .SET("title = #{title}")
            .SET("content = #{content}")
            .WHERE("id = #{id}")
            .toString();
}

public String findAllSql() {
    return new SQL() {{
        SELECT("name, title, content");
        FROM("blog");
    }}.toString();

}

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


//Mapper
public interface BlogMapper {

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

    @InsertProvider(type = BlogProvider.class, method = "insertSql")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Blog dto);

    @UpdateProvider(type = BlogProvider.class, method = "updateSql")
    void update(Blog dto);
}
```

[MyBatis 日志 (next)](https://github.com/zhengsh/document/blob/master/notes/mybatis/7_MyBatis_%E6%97%A5%E5%BF%97.md "MyBatis 日志")