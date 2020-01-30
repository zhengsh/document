### G1 GC日志分析
* 代码案例
```java
public class MyTest {

    public static void main(String[] args) {
        int size = 1024 * 1024;
        byte[] myAlloc1 = new byte[size];
        byte[] myAlloc2 = new byte[size];
        byte[] myAlloc3 = new byte[size];
        byte[] myAlloc4 = new byte[size];

        System.out.println("hello world");
    }
}
```
* 运行时JVM参数
```
-verbose:gc  //报告每个垃圾收集事件，输出虚拟机中垃圾回收的详细日志
-Xms10M      //初始化堆空间大小
-Xmx10M      //最大堆空间大小，-Xms和-Xmx设置成一样可以避免垃圾回收造成的抖动问题
-XX:+UseG1GC //使用G1垃圾收集器
-XX:+PrintGCDetails       //打印GC回收详细日志
-XX:+PrintGCDateStamps    //打印GC时间戳
-XX:MaxGCPauseMillis=200m //GC执行应用最大暂停时间
```

* GC日志
```
2020-01-30T11:00:13.034-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark), 0.0100740 secs]
   [Parallel Time: 1.8 ms, GC Workers: 4]
      [GC Worker Start (ms): Min: 2011.8, Avg: 2011.9, Max: 2012.1, Diff: 0.3]
      [Ext Root Scanning (ms): Min: 0.4, Avg: 0.5, Max: 0.8, Diff: 0.5, Sum: 2.1]
      [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
         [Processed Buffers: Min: 0, Avg: 0.0, Max: 0, Diff: 0, Sum: 0]
      [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      [Object Copy (ms): Min: 0.5, Avg: 0.5, Max: 0.6, Diff: 0.1, Sum: 2.2]
      [Termination (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
         [Termination Attempts: Min: 1, Avg: 1.2, Max: 2, Diff: 1, Sum: 5]
      [GC Worker Other (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 0.3]
      [GC Worker Total (ms): Min: 1.0, Avg: 1.2, Max: 1.3, Diff: 0.3, Sum: 4.6]
      [GC Worker End (ms): Min: 2013.1, Avg: 2013.1, Max: 2013.1, Diff: 0.0]
   [Code Root Fixup: 0.0 ms]
   [Code Root Purge: 0.0 ms]
   [Clear CT: 0.1 ms]
   [Other: 8.2 ms]
      [Choose CSet: 0.0 ms]
      [Ref Proc: 8.0 ms]
      [Ref Enq: 0.0 ms]
      [Redirty Cards: 0.0 ms]
      [Humongous Register: 0.0 ms]
      [Humongous Reclaim: 0.0 ms]
      [Free CSet: 0.0 ms]
   [Eden: 3072.0K(4096.0K)->0.0B(2048.0K) Survivors: 0.0B->1024.0K Heap: 4976.2K(10.0M)->2739.5K(10.0M)]
 [Times: user=0.00 sys=0.00, real=0.01 secs] 
2020-01-30T11:00:13.045-0800: [GC concurrent-root-region-scan-start]
2020-01-30T11:00:13.046-0800: [GC concurrent-root-region-scan-end, 0.0012011 secs]
2020-01-30T11:00:13.046-0800: [GC concurrent-mark-start]
2020-01-30T11:00:13.046-0800: [GC concurrent-mark-end, 0.0001928 secs]
2020-01-30T11:00:13.049-0800: [GC remark 2020-01-30T11:00:13.049-0800: [Finalize Marking, 0.0001507 secs] 2020-01-30T11:00:13.049-0800: [GC ref-proc, 0.0001743 secs] 2020-01-30T11:00:13.049-0800: [Unloading, 0.0005663 secs], 0.0010505 secs]
 [Times: user=0.01 sys=0.00, real=0.01 secs] 
2020-01-30T11:00:13.050-0800: [GC cleanup 3763K->3763K(10M), 0.0002342 secs]
 [Times: user=0.00 sys=0.00, real=0.00 secs] 
hello world
Heap
 garbage-first heap   total 10240K, used 4787K [0x00000007bf600000, 0x00000007bf700050, 0x00000007c0000000)
  region size 1024K, 2 young (2048K), 1 survivors (1024K)
 Metaspace       used 3200K, capacity 4592K, committed 4864K, reserved 1056768K
  class space    used 341K, capacity 424K, committed 512K, reserved 1048576K
Disconnected from the target VM, address: '127.0.0.1:50657', transport: 'socket'

Process finished with exit code 0
```

* GC日志解析
```
//由于 Humongous 分配导致GC暂停发生在年轻代，初始标记耗时0.0100740秒
2020-01-30T11:00:13.034-0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark), 0.0100740 secs]
   //运行时间1.8ms, GC有4个工作线程
   [Parallel Time: 1.8 ms, GC Workers: 4]
      //GC 工作线程开始。最小时间 2011.8ms, 平均时间2011.9ms, 最大时间2012.1ms, 相差0.3ms
      [GC Worker Start (ms): Min: 2011.8, Avg: 2011.9, Max: 2012.1, Diff: 0.3]
      //根扫描
      [Ext Root Scanning (ms): Min: 0.4, Avg: 0.5, Max: 0.8, Diff: 0.5, Sum: 2.1]
      //更新RSet
      [Update RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
         //记录引用变化缓存空间
         [Processed Buffers: Min: 0, Avg: 0.0, Max: 0, Diff: 0, Sum: 0]
      //RSet 扫描
      [Scan RS (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      //根扫描的时间
      [Code Root Scanning (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
      //对象拷贝
      [Object Copy (ms): Min: 0.5, Avg: 0.5, Max: 0.6, Diff: 0.1, Sum: 2.2]
      //GC线程执行完之前的耗时, 检查其他线程的引用（处理引用队列）
      [Termination (ms): Min: 0.0, Avg: 0.0, Max: 0.0, Diff: 0.0, Sum: 0.0]
         //执行完成
         [Termination Attempts: Min: 1, Avg: 1.2, Max: 2, Diff: 1, Sum: 5]
      //GC线程在其他任务上花费的时间
      [GC Worker Other (ms): Min: 0.0, Avg: 0.1, Max: 0.2, Diff: 0.2, Sum: 0.3]
      //GC线程总花费时间
      [GC Worker Total (ms): Min: 1.0, Avg: 1.2, Max: 1.3, Diff: 0.3, Sum: 4.6]
      //GC线程结束花费时间
      [GC Worker End (ms): Min: 2013.1, Avg: 2013.1, Max: 2013.1, Diff: 0.0]
   [Code Root Fixup: 0.0 ms]
   [Code Root Purge: 0.0 ms]
   //Card Table, Key 指向当前Region，然后Value就是当前的值
   [Clear CT: 0.1 ms]
   [Other: 8.2 ms]
      //选择回收集合
      [Choose CSet: 0.0 ms]
      //引用处理（软引用，弱引用处理）
      [Ref Proc: 8.0 ms]
      //引用处理进入队列
      [Ref Enq: 0.0 ms]
      [Redirty Cards: 0.0 ms]
      [Humongous Register: 0.0 ms]
      [Humongous Reclaim: 0.0 ms]
      [Free CSet: 0.0 ms]
   //执行完Young GC 后的结果
   [Eden: 3072.0K(4096.0K)->0.0B(2048.0K) Survivors: 0.0B->1024.0K Heap: 4976.2K(10.0M)->2739.5K(10.0M)]
 [Times: user=0.00 sys=0.00, real=0.01 secs] 
//并发扫描开始
2020-01-30T11:00:13.045-0800: [GC concurrent-root-region-scan-start]
//并发扫描结束
2020-01-30T11:00:13.046-0800: [GC concurrent-root-region-scan-end, 0.0012011 secs]
//并发标记开始
2020-01-30T11:00:13.046-0800: [GC concurrent-mark-start]
//并发标记结束
2020-01-30T11:00:13.046-0800: [GC concurrent-mark-end, 0.0001928 secs]
//最终标记
2020-01-30T11:00:13.049-0800: [GC remark 2020-01-30T11:00:13.049-0800: [Finalize Marking, 0.0001507 secs] 2020-01-30T11:00:13.049-0800: [GC ref-proc, 0.0001743 secs] 2020-01-30T11:00:13.049-0800: [Unloading, 0.0005663 secs], 0.0010505 secs]
 [Times: user=0.01 sys=0.00, real=0.01 secs] 
//内存清理
2020-01-30T11:00:13.050-0800: [GC cleanup 3763K->3763K(10M), 0.0002342 secs]
 [Times: user=0.00 sys=0.00, real=0.00 secs] 
hello world
Heap
 garbage-first heap   total 10240K, used 4787K [0x00000007bf600000, 0x00000007bf700050, 0x00000007c0000000)
  //分区大小1024K, 2个Region作为young, 1个Region Survivors, 其他的待分配
  region size 1024K, 2 young (2048K), 1 survivors (1024K)
 Metaspace       used 3200K, capacity 4592K, committed 4864K, reserved 1056768K
  class space    used 341K, capacity 424K, committed 512K, reserved 1048576K
Disconnected from the target VM, address: '127.0.0.1:50657', transport: 'socket'

Process finished with exit code 0
```