## CAS和AQS原理

### CAS（Compare And Swap）

CAS（Compare And Swap）, 即比较并交换。是解决多线程并行情况下使用锁造成性能损耗的一种机制，CAS操作包含三个操作数---内存位置（V）、
预期原值（A）和新值（B）。如果内存位置与预期原值匹配，那么处理器会自动将位置值更新为新值，否则处理器不做任何操作。无论哪种情况，他都会
在CAS指令之前返回该位置的值。CAS有效的说明了“我认为位置V应该包含A值，如果包含该值，则将B放到这个位置，否则不要更改该位置，
只告诉我这个位置现在的值即可”。


### AQS （AbstractQueuedSynchronizer）
AQS (AbstractQueuedSynchronizer) , AQS 是JDK下提供的一套实现基于FIFO等待队列的阻塞锁和相关的同步器的一个同步框架。这个抽象类被
设计为作为一些可以用原子int值来表示状态的同步器的基类，如果你有看过类似的CountDownLatch 类的源码实现。就会发发现其内部有继承了 
AbstractQueuedSynchronizer 的内部类 Sync。 可见 CountDownLatch 是基于AQS框架来实现的一个同步器，类似的同步器在JUC下还有很多  