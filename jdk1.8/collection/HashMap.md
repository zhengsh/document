### HashMap（1.8）

* hashmap 最外层是一个散列表

* 如果存在hash 冲突的时候会放入同一个索引下，
然后存放方式为链表或者红黑树

* hashmap 默认的长度是 16

* hashmap 拓容的长度负载因子是0.75

* hashmap 在存放过程中是无序的

* hashmap 在链表长度大于8的时候采用红黑树存储，主要是为了提高
存取效率

* hashmap 数量大于6的时候会变回链表存储