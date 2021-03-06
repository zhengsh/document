### 安装步骤
    
#### 添加用户
    useradd elasticsearch 
    
    # 设置目录权限
    chown -R elasticsearch elasticsearch-6.0.0
    mkdir -p /data/es6
    chown -R elasticsearch /data/es6
    
    
#### 修改系统参数
    vim /etc/security/limits.conf
    
    root soft nofile 65535
    root hard nofile 65535
    * soft nofile 655350
    * hard nofile 655350

    # max number of threads [1024] for user [lish] likely too low, increase to at least [2048]
    vim /etc/security/limits.d/90-nproc.conf
    *          soft    nproc     8096

    # max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]
    vim /etc/sysctl.conf
    
    vm.max_map_count=262144
    fs.file-max=655350
    
    # load system param value
    sysctl -p
    
#### 修改日志目录和配置参数
    # 创建目录
    mkdir -p /data/es6/data
    mkdir -p /data/es6/logs
    
    vim elasticsearch.yml
    # java.lang.UnsupportedOperationException: seccomp unavailable: CONFIG_SECCOMP not compiled ...
    bootstrap.memory_lock: false
    bootstrap.system_call_filter: false    

    # 设置目录
    path.data: /data/es6/data
    path.log: /data/es6/logs

#### 启动命令 (后台运行)注意需要用elasticsearch 账号来启动
    su elasticsearch
    ./bin/elasticsearch -d
