### ReadWriteLock

#### 概述

ReadWriteLock 维护了一对相关的锁，一个用于只读操作，另外一个用于写入操作，只要没有writer, 读取锁可以由多个reader线程保持，写入是独占锁。

所有的ReadWriteLock实现都必须保证writeLock操作的内存同步效果也要保持相关的readLock联系，也就是说，成功获取读锁的线程会看到写入锁
之前版本所做的所有更新。

与互斥锁（如：ReentrantLock）相比读-写锁允许对共享数据进行更高级别的并发访问。虽然只有一个线程可以修改共享数据，但在许多情况下，任何线程‘
都可以同事读取共享数据（reader 线程），读-写允许利用了这一点，理论上讲，与互斥锁相比，使用读-写锁允许的并发性增强带来更大的性能提高。
在实践中，只有多喝处理器上并且只有在访问者模式适用于共享数据时，才能完成时限并发性增强。

与互斥锁相比，使用读-写锁能否提升性能则取决于读写操作期间读取数据相对于修改数据的频率（如：内容缓存），以及数据的争用--即在同一时间视图对
该数据执行读取或者写入的线程数，例如，某个最初用户数填充并且之后不经常对其修改的collection, 因为经常对其进行搜索（比如: 搜索某种目录），
若依这样的collection是使用读-写锁的理想候选者。但是，如果数据更新变得频繁，数据在大部分时间都被独占锁，这时，就算存在并发性增强，也是
微不足道的，进一步将，如果读取操作的时间太短，则读-写锁实现（它本身就比互斥锁更复杂）的开销将成为主要的执行成本，在许多读-写锁实现依然通过
一小段代码将所有的线程序列化时更是如此，最终，只有通过分析和测量，才能确定应用程序是否适用于读-写锁。

尽管读-写锁的基本操作是直接了当，但实现依然必须做出血多决策，这些决策可能影响给定引用程序中读写锁的想过，这些策略的例子包括：
* 在writer释放写锁时，reader和writer都处于等待状态，在这个时候确定授予读取锁还是授予写入锁，writer优先比较普遍，因为预期写入所需的时间
较短并不那么频繁。Reader优先不太普遍，因为如果reader正如预期那样频繁和持久，那么它将导致写入操作来说较长的延迟，公平或者“按次序”实现也是有可能的。
* 在reader 处于活动状态而writer处于等待状态时，雀东是否想请求读取锁的reader授予读取锁，reader优先会无限期的延迟writer，而writer优先会
减少可能的并发。
* 可以确定重新写入锁：可以使用带有写入所的线程重新获取它吗？可以在保持写入所的同时读取锁吗？可以重新进入读写锁本身吗？
* 可以将写入所在不允许其他writer 干涉的相框下降级为读取锁吗？ 可以优先等待的reader 或 writer 将读取锁升级为写入所吗？

当评估该定时限是否适合当前程序时，应该考虑所有这些情况。


### ReentrantReadWriteLock
支持 ReentrantLock 和 ReadWriteLock 的实现
此类具有如下属性

* 获取顺序
  此类不会讲读取这优先或者写入这优先强加给锁访问的拍讯，但是它支持公平策略。
  
* 重入
  此锁允许reader 和writer 按照 ReentrantLock 的样式重新获取读锁或者写锁，在写入线程保持所有写入锁都已经释放后，才允许reader使用他们。
此外，writer 可以获取读锁，但是反过来不成立，在其他应用中，当在调用或者回调哪些在读取锁状态下才执行读取操作的方法期间保持写入锁时，重入
很有作用。如果reader 视图获取读写锁，那么永远不会获得成功。

* 降级锁
  重入还允许写入锁降级为读取锁，其实现方式是：先获取写入锁，然后获取读取锁，最后释放写入锁，但是，从读取锁升级到写入锁是不可能的。
  
* 锁获取的中断
  读取锁和写入锁支持锁获取期间的中断
  
* Condition 支持
  写入锁提供了一个 Condition 实现，对于写锁来说，实现的行为与 ReentrantLock.newCondition() 提供的 Condition锁做的行为相同。当然，
Condition 只能用于写锁。读取锁不支持 Condition， readLock().newCondition() 会抛出UnsupportedOperationException

* 检测
  此类支持一些确定是保持还是争用锁的方法。这些方法设计用于监视系统状态，而不是同步控制。此类行为的序列化方法与内置锁的相同；反序列化的锁
处于解锁状态，无论序列化该锁时其状态如何。

用法实例：

#### 1. 降级锁
* 锁降级是指当前线程把持有的写锁再去获取读锁，随后释放先前拥有的写锁的过程。

* 读锁时共享锁，写锁是排它锁。如果写锁释放之前释放了写锁。会造成别的线程很快又拿到了写锁，然后阻塞读锁。造成数据的不可控性，也造成了不必要的cpu
资源浪费，写只需要一个线程来执行，然后共享锁，不需要多线程都去获取这个写锁，如果先释放写锁，然后再去获取读锁后果也是如此。

jdk 提供的案例如下：

```java
class CachedData {
   Object data;
   volatile boolean cacheValid;
   final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

   void processCachedData() {
     rwl.readLock().lock();
     if (!cacheValid) {
       // Must release read lock before acquiring write lock
       rwl.readLock().unlock();
       rwl.writeLock().lock();
       try {
         // Recheck state because another thread might have
         // acquired write lock and changed state before we did.
         if (!cacheValid) {
           data = ...
           cacheValid = true;
         }
         // Downgrade by acquiring read lock before releasing write lock
         rwl.readLock().lock();
       } finally {
         rwl.writeLock().unlock(); // Unlock write, still hold read
       }
     }

     try {
       use(data);
     } finally {
       rwl.readLock().unlock();
     }
   }
}
```

*总结
  * 写锁释放后，读锁全部会争夺资源。
  * 如果在释放写锁之前去拿到读锁，再去释放写锁，可以规避读锁的一些资源争抢。
  
* 思考
  * 没有感知到数据变化的读锁会冲进来继续占用写锁，阻塞已完成写操作的线程会继续获取读锁。
  * 为了性能，因为读锁的抢占必将引起资源分配和判断等操作，降级锁减少了线程柱塞和唤醒，试试连续性更强。