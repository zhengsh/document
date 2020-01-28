### CMS 收集器

* CMS（Concurrent Mark Sweep）收集器，以获取最短回收停顿时间为目标，多数应用于互联网网站或者B/网站的服务器端上。

* CMS 是基于 "标记-清除" 算法的实现的，整个过程分为4个步骤：
  * 初始标记（CMS initial mark）
  * 并发编辑（CMS concurrent mark）
  * 重新标记（CMS remark）
  * 并发清除（CMS concurrent sweep）
  
* 其中初始标记、重新标记这个两个步骤需要"Stop The World"；
* 初始标记只是标记一下GC Roots 能直接关联到的对象速度很快；
* 并发标记阶段就是进行GC Roots Tracing 的过程；
* 重新标记阶段则是为了修正并发标记期间因用户程序继续运作而导致标记产生变动的那一部分对象的标记记录，这个阶段的挺短时间一般会比
初始标记阶段稍长一些，但远比并发标记的时间短。

* CMS 收集器的运作步骤如下图所示，在整个过程中最费时的并发标记和并发清除过程都可以与用户线程一起工作。因此，从总体上来看，CMS收集器
的内存回收过程是与用户线程一起并发执行的。
![avatar](../../images/jvm/gc/3.PNG)

* 优点
  * 并发收集、低停顿，Oracle公司的一些官方文档中也成称之为并发低停顿收集器（Concurrent Low Pause Collector）
* 缺点
  * CMS 收集器堆CPU资源非常敏感
  * CMS 收集器无法处理**浮动垃圾（Floating Garbage）**， 可能出现"Concurrent Mode Failure" 失败而导致另一次Full GC的产生。如果在应用中
老年代增长不是太快，可以适当调高参数-XX:CMSInitiatingOccupancyFraction 的值来提高触发百分比，以便减低内存回收次数从而获取更好的的性能。
要是CMS运行期间预留的内存无法满足程序需要时，虚拟机将启动后备预案：临时启用Serial Old收集器来重新进行老年代的垃圾收集，这样停顿时间就长了。所以说参数
-XX:CMSInitiatingOccupancyFraction 设置得太高很容易导致大量的"Concurrent Mode Failure"失败，性能反而降低。
  * 收集结束时会有大量空间碎片产生， 空间碎片过多时， 将会给大独享分配带来很大的麻烦，往往出现老年代还有很大空间剩余，但是无法找到足够大的连续空间来分配当前
对象，不得不提前进行一次Full GC。CMS收集器提供了一个-XX:UseCMSCompactAtFullCollection 开关参数（默认是开启的），用于在CMS收集器顶不住的时候
进行Full GC开启内存碎片的合并整理过程，内存整理的过程是无法并发的，空间碎片问题没有了，但停顿的事件不得不变长。

### 空间分配担保
* 在发生Minor GC之前，虚拟机会先检查老年代最大可用连续空间是否大于新生代所有对象总空间，如果这个条件成立，那么Minor GC 可以确保是安全的。
当大量对象在Minor GC 后依然存活，就需要老年代进行空间分配担保，把Survivor无法容纳的对象直接进入老年代，如果老年代判断到神域空间不足（根据以往每一次
回收晋升到老年代对象容量的平均值作为经验值），则进行一次Full GC