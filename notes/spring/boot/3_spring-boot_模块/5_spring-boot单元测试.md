# spring-boot 开发者工具与单元测试

### idea 配置
* Mac OS 环境配置 
  * 首先打开 Preferences -> Build, Execution, Deployment 然后设置 Compiler -> Build project automatically 为勾选。
  * 打开维护状态 shift + alt + command + ? 然后选择 Registry -> compiler.automake.allow.when.app.running 为勾选。
  
### 添加依赖

```groovy
//dev-tools
complie "org.springframework.boot:spring-boot-devtools"
//test
complie "org.springframework.boot:spring-boot-starter-test"
```

### 单元测试示例
```java
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void test() throws Throwable {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/test")
                  .contentType(MediaType.APPLICATION_JSON)
                  .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}
```
