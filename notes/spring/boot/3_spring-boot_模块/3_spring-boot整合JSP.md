# spring-boot 整合 JSP

### 导入配置

```
compile 'org.springframework.boot:spring-boot-starter-web'
compile "javax.servlet:jstl"
compile "org.apache.tomcat.embed:tomcat-embed-jasper"
```

### 添加控制器
```java
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.Map;

@Controller
public class JspController {

    @GetMapping("/jsp")
    public String jsp(Map<String, Object> model) {
        model.put("date", new Date());
        model.put("message", "hello world");
        return "result";
    }

    @RequestMapping("/jspError")
    public String jspError(Map<String, Object> model) {
        throw new RuntimeException("jspError");
    }
}

```
### 添加页面

* 需要在项目的 main 目录下创建webapp/WEB-INF/jsp
* 然后添加2个JSP页面
* "result.jsp" 文件
```jsp
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
</head>
<body>
    Date: ${date} <br/>
    Message: ${message}
</body>
</html>
``` 
* "error.jsp" 文件
```jsp
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
</head>
<body>
    error occured: ${status}, ${error}
</body>
</html>
``` 

### YML配置
* YML 配合信息需要配置
```yaml
spring:
  application:
    name: mytest
  mandatory-file-encoding: UTF-8
  http:
    encoding:
      enabled: true
      charset: UTF-8
  mvc:
    view:
      prefix: /WEB-INF/jsp/
      suffix: .jsp
```

### 访问与测试
* 访问地址
    http://localhost:9090/jsp