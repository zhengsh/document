# spring-boot 整合 WebSocket

### 导入依赖
```groovy
complie "org.springframework.boot:spring-boot-starter-websocket"
complie "org.springframework.boot:spring-boot-starter-json"
```

### 代码逻辑
* 在 spring-boot 的启动类上添加启用 WebSocket 的注解 @EnableWebSocket
* 注册 WebSocket 服务, 项目启动类如下
```java
import cn.edu.cqvie.echo.DefaultEchoService;
import cn.edu.cqvie.echo.EchoService;
import cn.edu.cqvie.echo.EchoWebSocketHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.PostConstruct;

@EnableWebSocket
@SpringBootApplication
public class MyApplication implements WebSocketConfigurer {

    public static void main(String[] args) {
        System.out.println(MyApplication.class.getClassLoader());
        SpringApplication.run(MyApplication.class, args);
    }

    @Bean
    public EchoService echoService() {
        return new DefaultEchoService("You said \"%s\"!");
    }

    @Bean
    public WebSocketHandler echoWebSocketHandler() {
        return new EchoWebSocketHandler(echoService());
    }

    /**
     * 注册 WebSocket
     * @param registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(echoWebSocketHandler(), "/echo").withSockJS();
    }
}

```
* 处理 TextWebSocketHandler 相关的事件定义 EchoWebSocketHandler 类来处理 WebSocket 请求

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class EchoWebSocketHandler extends TextWebSocketHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private EchoService echoService;

    public EchoWebSocketHandler(EchoService echoService) {
        this.echoService = echoService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("建立连接");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        session.close(CloseStatus.SERVER_ERROR);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String m = this.echoService.getMessage(message.getPayload());
        session.sendMessage(new TextMessage(m));
    }
}

```
* 通过 EchoService 来处理消息并且返回应答内容
  * 定义逻辑处理接口 EchoService
```java
public interface EchoService {
  String getMessage(String message);
}  
```  
  * 逻辑处理实现类 DefaultEchoService
```java
public class DefaultEchoService implements EchoService {

  private String echoFormart;

  public DefaultEchoService(String echoFormart) {
      this.echoFormart = (null != echoFormart) ? echoFormart : "%s";
  }

  @Override
  public String getMessage(String message) {
      return String.format(this.echoFormart, message);
  }
}
```
### 添加页面

* 添加页面文件 "echo.html"
```html
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>WebSocket Sample</title>
    <style type="text/css">
        #connect-container {
            float: left;
            width: 400px
        }

        #connect-container div {
            padding: 5px;
        }

        #console-container {
            float: left;
            margin-left: 15px;
            width: 400px;
        }

        #console {
            border: 1px solid #CCCCCC;
            border-right-color: #33333333;
            border-bottom-color: #999999;
            height: 170px;
            overflow-y: scroll;
            padding: 5px;
            width: 100%;
        }

        #console p {
            padding: 0;
            margin: 0;
        }
    </style>
    <script src="https://cdn.bootcss.com/sockjs-client/0.3.4/sockjs.min.js"></script>
    <script type="text/javascript">
        var ws = null;
        var transports = [];

        function setConnected(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('echo').disabled = !connected;
        }

        function connect() {
            var target = document.getElementById("target").value;
            ws = new SockJS(target);
            ws.onopen = function () {
                setConnected(true);
                log('Info: WebSocket connection opened.');
            };
            ws.onmessage = function (event) {
                log('Received: ' + event.data);
            };
            ws.onclose = function (event) {
                setConnected(false);
                log('Info: WebSocket connection closed.');
            };
        }

        function disconnect() {
            if (ws != null) {
                ws.close();
                ws = null;
            }
            setConnected(false);
        }

        function echo() {
            if (ws != null) {
                var message = document.getElementById('message').value;
                log('Sent: ' + message);
                ws.send(message);
            } else {
                alert('connection not established, please connect.');
            }
        }

        function log(message) {
            var console = document.getElementById('console');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode(message));
            console.appendChild(p);
            while (console.childNodes.length > 25) {
                console.removeChild(console.firstChild);
            }
            console.scrollTop = console.scrollHeight;
        }
    </script>
</head>
<body>
<noscript><h2 style="color:#ff0000">Seems your browser doesn't supportJavascript!Websockets
    rely on Javascript being enabled. Please enable
    Javascript and reload this page!</h2></noscript>
<div>
    <div id="connect-container">
        <div>
            <input id="target" type="text" size="40" style="width: 350px;" value="/echo">
        </div>
        <div>
            <input type="button" id="connect" onclick="connect()" value="Connect"/>
            <input type="button" id="disconnect" disabled="disabled" onclick="disconnect()" value="Disconnect"/>
        </div>
        <div>
            <textarea id="message" style="width:350px">a message to be sent</textarea>
        </div>
        <div>
            <input type="button" id="echo" onclick="echo()" disabled="disabled" value="Echo message"/>
        </div>
    </div>
    <div id="console-container">
        <div id="console"></div>
    </div>
</div>
</body>
</html>
```


