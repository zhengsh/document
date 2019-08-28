### ConcurrentHashMap(1.8) 

* ConcurrentHashMap 是 HashMap的线程安全版本的实现。和HashMap类似， ConcurrentHashMap使用了一个table
来存储Node, ConcurrentHashMap同样使用记录的key的hashCode来寻找记录的存储index, 而处理hash冲突的方式与
HashMap也是类似的，冲突的记录将被存储在同一个位置上，形成一条链条。当链表的长度大于8的时候会将链表转化为以可红黑树
从而将查找的负载度从O(N)下降到了O(logN),下文详细分析ConcurrentHashMap的实现，以及ConcurrentHashMap
是如何保证在并发环境下的线程安全。

#### 初始化方法 initTable

#### 插入数据 put 

#### 查询方法 get

#### 删除方法 remove

#### 拓容方法

#### 保证线程安全

#### 总结

### 参考

* https://juejin.im/entry/59fc786d518825297f3fa968