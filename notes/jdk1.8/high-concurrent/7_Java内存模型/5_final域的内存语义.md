### final 域的内存语义
* 写 final 域的重排序规则
* 读 final 域的重排序规则
* final 域为静态类型
* final 域为抽象类型

### 写 final 域的重排序规则
* 写 final 域的重排序的规则禁止把 final 域的写重排序到构造方法之外。
* Java 的内存模型禁止编译器把final 域的写重排序到构造方法之外
* LoadLoad
* StoreStore
* LoadStore
* StoreLoad

### final 域为抽象类型
* 在构造方法内对一个final 引用的对象的成员域的写入，与随后在构造方法外把这个被构造的对象引用赋值给一个引用变量，这个操作之间不能重排序。