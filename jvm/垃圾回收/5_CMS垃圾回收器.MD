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

### CMS收集器收集步骤
* Phase 1: Initial Mark （初始标记）
  * 这个是CMS两次stop-the-world事件的其中一次，这个阶段的目标是：标记哪些直接被GC Root 引用或者被年轻代存活对象所引用的所有对象。
  ![avatar](../../images/jvm/gc/4.PNG)

* Phase 2: Concurrent Mark （并发标记）
  * 在这个阶段 Garbage Collector 会遍历老年代，然后标记所有存活的对象，它会更具上个阶段找到的GC Roots 遍历查找。并发标记阶段，
他会与用户的应用程序并发运行。 并不是老年代所有的存活对象都会被标记， 因为在标记期间用户的程序可能会改变一些引用
  ![avatar](../../images/jvm/gc/5.PNG)
  * 在上面的图中，与阶段1的图进行对比就会发现有一个对象的引用已经发生额变化。
  
* Phase 3: Concurrent Preclean (并发预先清理)
  * 这个也是一个并发阶段，与应用的线程并发运行，并不会stop应用的线程。在并发运行的过程中，一些对象可能会发生变化，但是这种情况发生时。JVM
将会包含这个对象的区域（Card）标记为Dirty, 这也就是Card Marking.
  * 在pre-clean 阶段，哪些能够从Dirty 对象到达的对象也会被标记，这个标记做完后，dirty card 标记就会被清除了。
  
* Phase 4: Concurrent Abortable Preclean （并发预先可能失败的清理）
  * 这也是一个并发阶段，但是同样不会影响用户的应用线程，这个阶段是为了尽量承担 STW （stop-the-world）中最终标记阶段的工作。这个阶段
  持续时间依赖于很多的因素，由于这个阶段是在重复做很多相同的工作，直接满足一些条件（比如：重复迭代的次数、完成的工作量或者时钟时间等）。
  
* Phase 5: Final Remark （最终重新标记）
  * 这个阶段是第二个STW阶段，也是 CMS的最后一个，这个阶段的目标是标记老年代所有的存活对象，由于之前的阶段是并发执行的，GC线程可能跟不上应用程序
  的变化，为了完成标记老年代所有存活对象的目标，STW就非常有必要了
  * 通常CMS的Final Remark 阶段会在年轻代尽可能干净的时候运行，目的是为了减少STW发生的可能性（年轻代存回对象过多的话，
  也会导致老年代设计的存活对象会很多）。这个阶段会比前面几个阶段更复杂一些。
  * 标记阶段完成
    * 经历过这5个阶段之后，老年代所有存活的对象都被标记过了，现在可以通过清除算法去清理哪些老年代不再使用的对象。
  
* Phase 6: Concurrent Sweep （并发清理）
  * 这里不需要STW，它是与用户的应用程序并发运行的，这个阶段是：清除哪些不再使用的对象，回收他们占用的空间为将来使用。
  
* Phase 7: Concurrent Reset （并发重置）
  * 这个阶段是并发执行的，它会重设 CMS 内部的数据结构，为下次的GC做准备。
  
### 总结
CMS 通过将大量工作分散到并发处理阶段来减少STW时间，在这块做的非常优秀，但是CMS也有一些其他的问题。
* CMS 收集器无法吹李浮动垃圾（Flating Garbage）, 可能出现 "Concurrent Mode Failure" 失败而导致另一次 Full GC 的产生，可能引发串行Full GC
* 空间碎片, 导致无法分配大对象，CMS收集器提供了一个 —XX:+UseCMSCompactAtFullCollection 开关参数（默认就是开启的），用于在CMS收集器顶部主要
记性Full GC时开启内存碎片的合并整理过程，内存整理的过程是无法并发的，空间碎片问题没有了，但停顿时间不得不变长。
* 对于堆较大的应用，GC的时间难以预估

### CMS GC日志实例
```
[GC (CMS Initial Mark) [1 CMS-initial-mark: 8194K(10240K)] 12935K(19456K), 0.0004987 secs] [Times: user=0.00 sys=0.01, real=0.00 secs] 
[CMS-concurrent-mark-start]
[CMS-concurrent-mark: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[CMS-concurrent-preclean-start]
[CMS-concurrent-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[CMS-concurrent-abortable-preclean-start]
[CMS-concurrent-abortable-preclean: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[GC (CMS Final Remark) [YG occupancy: 4741 K (9216 K)][Rescan (parallel) , 0.0010423 secs][weak refs processing, 0.0000103 secs][class unloading, 0.0003310 secs][scrub symbol table, 0.0006947 secs][scrub string table, 0.0001765 secs][1 CMS-remark: 8194K(10240K)] 12935K(19456K), 0.0023344 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[CMS-concurrent-sweep-start]
[CMS-concurrent-sweep: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
[CMS-concurrent-reset-start]
[CMS-concurrent-reset: 0.000/0.000 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
```