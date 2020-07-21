### happens-before
* happens-before 是用来指定两个操作之间的执行顺序，提供跨线程的内存可见性。
* 在java 内存中，如果一个操作执行的结果需要读另一个操作课件，那么这两个操作之间必然存在
happens-before 关系。
* happens-before 规则如下：
  * 程序顺序规则
  * 监视器锁规则
  * volatile 变量规则
  * 传递性
  * Start 规则
  * Join 规则 

### volatile 变量规则
* 对一个volatile 域的写，happens-before 于任意后续对这个volatile 域的读写。


### happens-before规则
* 《JSR-133:Java Memory Model and Thread Specification》定义了如下happens-before规则。
* 1)程序顺序规则:一个线程中的每个操作，happens-before于该线程中的任意后续操作。
* 2)监视器锁规则:对一个锁的解锁，happens-before于随后对这个锁的加锁。
* 3)volatile变量规则:对一个volatile域的写，happens-before于任意后续对这个volatile域的 读。
* 4)传递性:如果A happens-before B，且B happens-before C，那么A happens-before C。 
* 5)start()规则:如果线程A执行操作ThreadB.start()(启动线程B)，那么A线程的
ThreadB.start()操作happens-before于线程B中的任意操作。 
* 6)join()规则:如果线程A执行操作ThreadB.join()并成功返回，那么线程B中的任意操作
happens-before于线程A从ThreadB.join()操作成功返回。