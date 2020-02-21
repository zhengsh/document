### Lock 接口
* sysnchronized 
* volatile, 保证修饰变量的可见性和防止指令重排序。
* AtomicXXX, Java 所带的原子类

### 特征
* Lock 需要显示地获取和释放锁，使得代码更加的灵活。
* sysnchronized 不需要显示地获取和释放锁，更加的简单。

* 使用 Lock 可以方便的实现公平性
* 非阻塞获取锁
* 中断的获取锁
* 设置获取锁的超时时间