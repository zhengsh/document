### synchronized 关键字
* 在多线程并发编程中synchronized出现较早，很多人都会称呼它为重量级锁。

### sysnchronzied 3种形式
* sysnchronzied 修饰普通方法，内置锁的对象是当前类的实例。
* sysnchronzied 修饰静态方法，内置锁的对象是当前类的类实例（Class字节码对象）。
* sysnchronzied 修饰代码块，内置锁的对象就是 sysnchronzied 传入的对象

### sysnchronzied 在JVM里的实现原理
* JVM基于进入和退出Monitor对 象来实现方法同步和代码块同步，但两者的实现细节不一样。
  * 代码块同步是使用monitorenter 和monitorexit指令实现的
  * 而方法同步是使用另外一种方式实现的，细节在JVM规范里并没有 详细说明。但是，方法的同步同样可以使用这两个指令来实现。

* monitorenter指令是在编译后插入到同步代码块的开始位置，而monitorexit是插入到方法结 束处和异常处，JVM要保证每个monitorenter必须有对应的monitorexit与之配对。任何对象都有 一个monitor与之关联，当且一个monitor被持有后，它将处于锁定状态。线程执行到monitorenter 指令时，将会尝试获取对象所对应的monitor的所有权，即尝试获得对象的锁。

* Java对象头
  * synchronized用的锁是存在Java对象头里的。如果对象是数组类型，则虚拟机用3个字宽 (Word)存储对象头，如果对象是非数组类型，则用2字宽存储对象头。在32位虚拟机中，1字宽 等于4字节，即32bit。
  * 对象头结构
    * Mark Word, 存储对象的hashCode或锁信息等
    * Class Metadata Address, 存储到对象类型数据的指针
    * Array Length, 数组的长度

### 锁的操作和对比
  * 偏向锁
  * 轻量级锁
  *  

### 锁和线程安全
* 所有的锁都是互斥的，在同一个时刻，只有一个线程能够获得锁。那么这样就可以保证共享资源的线程安全性。