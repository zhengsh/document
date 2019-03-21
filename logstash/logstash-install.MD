### logstash 安装教程
#### 增加logstash 配置文件
    
    cd config
    vim logstash-tcp.conf
    input {
      tcp {
        mode => "server"
        host => "上述log4j的IP地址"
        port => 上述log4j的端口
      }
     }
     output {
       elasticsearch {
        hosts => "ES的IP地址:端口"
        index => "logstash-test"
      }
        stdout {}
     }
#### log4j.properties 配置
    # appender 添加 logstash
    log4j.rootLogger=logstash

    # Logstash appender
    log4j.appender.logstash=org.apache.log4j.net.SocketAppender  
    log4j.appender.logstash.RemoteHost=IP地址
    log4j.appender.logstash.port=端口
    log4j.appender.logstash.Threshold=INFO
    log4j.appender.logstash.ReconnectionDelay=10000
    log4j.appender.logstash.LocationInfo=true
    
