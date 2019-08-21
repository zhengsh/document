###HosSpot虚拟机

#### 简介
* Amdahl定律（给定问题的并行加速受到问题的连续部分的限制）意味着大多数工作负载无法完美并行化; 某些部分始终是顺序的，不会受益于并行性。Java平台也是如此。特别是，Oracle Java 1.4之前的Java平台虚拟机不支持并行垃圾收集，因此垃圾收集对多处理器系统的影响相对于其他并行应用程序而言也在增长。

垃圾收集器cpu中话费的时间百分比

![avatar](https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/img/jsgct_dt_005_gph_pc_vs_tp.png)

* 这表明在小型系统上开发时可忽略不计的速度问题可能成为扩展到大型系统时的主要瓶颈。但是，减少这种瓶颈的微小改进可以在性能上产生很大的提高。对于足够大的系统，选择正确的垃圾收集器并在必要时进行调整是值得的。

### Ergonomics
* 服务器定义 
  * 内核数量大于2
  * 内存大于2G
  将安装 Server VM 版本
  
* 默认参数以及配置
  * 初始堆大小为1/64的物理内存，最大为1GB
  * 最大堆大小为1/4物理内存，最大为1GB
 

### Generations

* 内存模型

![avatar](https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/img/jsgct_dt_001_armgnt_gn.png)

在初始化的时候，实际上并未分配物理内存，对象存储保留完整地址给空间可分为，年轻代和年老代

  * 堆
     
     * 堆参数说明
        ![avatar](https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/img/jsgct_dt_006_prm_gn_sz.png)
        
     * 堆大小的设置规则
         * 除非遇到暂停问题，尽可能的为虚拟机尽可能的设置更多的内存，默认大小通常太小
         * 通过从虚拟机中删除最终小的大小决策， 设置-Xms和-Xmx相同值可提高可预测性，但是如果做出了糟糕的选择，
    则虚拟机无法进行补偿
         * 通常，增加处理器数量时增加内存，因为分配可以并行化
             
     * 年轻代, 年轻代通过参数NewRatio控制，设置-XX:NewRatio=3意味着，年轻代和年老代之间的比例是1:3
换句话说，伊甸区和幸存区的组合大小将是总堆大小的1/4
     * 幸存区, 幸存区通过参数SurvivorRatio来调整幸存区大小， 这个通常对性能影响不大。例如：-XX:SurvivorRatio=6将伊甸区和幸存区空间比率设置
为6:1,换句话说，每个幸存区空间是伊甸区的六分之一，因此是年轻代的八分之一（不是七分之一，应为有两个幸存区）
     * 

### Available Collectors

* 串行垃圾收集器，适合单处理器机器
  设置 -XX:+UseSerialGC
  
* 并行收集器,默认启用并行压缩。设置-XX:+UserParallelGC

* 大中型数据采集程序
  CMS收集器 -XX:+UseConcMarkSweepGC;
  G1收集器 -XX:+UseG1GC
  
* 选择垃圾回收器
  * 程序具有较小的数据集（100MB）选择-XX:+UseSerialGC
  * 如果单个处理器上运行且没有暂停时间要求，则让VM选择收集器，或者选择带有该选项的串行收集器-XX:+UseSerialGC
  * 如果（a）峰值应用程序是第一优先级并且（b）没有暂停时间要求或1秒或更长的暂停时间可以接受，那么选择-XX:+UseParallelGC
  * 如果响应时间比总吞吐量更重要，并且垃圾收集暂停必须保存短于大约1秒，则使用-XX:+UseConcMarkSweepGC 或者选择并发收集器 -XX:+UseG1GC

说明：
-Xms 表示Java虚拟机堆初始内存分配大小
-Xmx 表示Java虚拟机堆内存可分配的最大上限