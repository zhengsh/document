### volatile 关键字

### volatile 可见性底层原理

底层实现主要是通过汇编 lock 前缀指令，它会锁定这块内存区域的缓存（缓存行锁定）并回写到主内存

IA-32 软件架构开发者手册lock指令的解释：

1）会将当前处理器缓存行的数据<font color="red">**立即**</font>写回到系统内存

2）这个写回内存的操作会引起其他CPU 里缓存了该内存地址的数据无效（MESI 协议）

### Java 程序汇编查看代码

``-server -Xcomp -XX:+UnlockDiagnosticVMOptions -XX:PrintAssembly -XX:CompileCommand=compileonly,*VolatileVisbilityTest.prepareData``

可以参考博客 [java 中volatile 关键字的实现](https://www.cnblogs.com/xrq730/p/7048693.html)

### volatile 可见性、原子性与有序性

* 并发编程三大特征：``可见性``、``原子性``、``有序性``
* ``volatile`` 保证可见性与有序性，但是<font color="red">**不保证原子性**</font>，保证原子性需要借助 ``synchronized``这样的锁机制。

### volatile 总结

* 保证内存可见性
* 防止指令重排序
* 不保证原子性

