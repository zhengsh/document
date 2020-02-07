# Redis 概述

### redis.io
* Redis 是一个开放源代码（BSD许可）的内存数据库。用作数据存储，缓存和消息代理。它支持数据结构: string, hash, list, set, 
stored sets with range queries(有序集合), bitmaps(位图), hyperloglogs（超级日志）, geospatial indexes with radius 
queries and streams(具有半径查询和流的地理空间索引)。Redis 具有内置的复制，Lua 脚本, LRU 缓存， 事务和不同级别的磁盘持久性，
并通过 Redis Sentinel 和 Redis Cluster 自动分区提供高可用性。 主要作用是用来做数据缓存，将热点数据存储在Redis中可以减少业务流量
对底层数据的访问压力，获得更高的并发和更快的请求响应速度。

### 

### Redis 的 LRU 缓存策略




### 名词解释

* 位图(bitmaps)，位图不是时机的数据类型，而是在 String 类型的基础上定义的一组面向位的操作。由与字符串是二进制安全 Blob, 并且最大长度为 512MB，
因此他们适合设置多达 1L << 32 不同的位。
  * 操作分为两类: 固定时间的单个位操作（如将一个位设置为1或者0获取其值），以及对位组的操作，例如计算给定范围内设置的位的数量（例如：人口统计）。
  * 位图的最大优点之一是，他们在存储信息时通常可以节省大量空间。例如，在以增量用户ID不同的系统中，仅适用 512MB内存就可以记住40亿用为的，
  一位信息（例如：知道用户是否要接受新闻通讯）。
  适用 SETBIT 和 GETBIT 命令设置检索位:
  ```shell script
  localhost:6379> setbit key 10 1
  (integer) 1
  localhost:6379> getbit key 10
  (integer) 1
  localhost:6379> getbit key 11
  (integer) 0
  ```
  * 所叙SETBIT 命令采用作为第一个参数的比特数，作为第二个参数的值设置所表述位，其为1或0的命令放大字符串，如果寻址位是当前字符串长度之外。
  * GETBIT 知识返货指定索引处的位的值。超出范围的位（寻址超出存储在目标键中的字符串长度的位）始终被视为零。
  在位组上有三个命令
    * BITTOP在不同的字符串之间执行按位运算。提供运算符为 AND，OR，XOR和NOT。
    * BITCOUNT执行填充计数，报告设置为1的位数
    * BITTOPS查找指定值的0或1的第一位
  * 无论BITOPS和BITCOUNT 能够与字符串的字节范围进行操作，而不是该字符串的整个长度运行，一下是BITCOUNT调用的一个简单示例:
  ```shell script
  localhost:6379> setbit key 0 1
  (integer) 0
  localhost:6379> setbit key 100 1
  (integer) 0
  localhost:6379> bitcount key
  (integer) 2
  ```
  * 位图的常见用例是：
    * 各种实时分析。
    * 存储对象ID相关的空间高效且高性能的 boolean 信息。
  * 例如，假设您想知道网站用户每天访问量最长的时间。您从0开始计算天数，即从您公开网站的那一天开始，并在用户每次访问该网站时对SETBIT
  进行设置。作为索引，您只需用当前的unix时间，减去初始偏移量。然后除以一天中的秒数（通常 3600 * 24）。
  * 这样，对于每个用户，您都有一个小的字符串，其中包含每天的访问信息，使用BITCOUNT， 可以轻松获得给定用户访问网站的天数，而只需几个BITTOPS
  调用，或者仅获取和分析客户端你的位图，就可以轻松计算出登录次数最多的用户。
  * 位图很容易分成多个键，例如，为了分片数据集，并且应为通常最好避免使用大键。要在不同的key上拆分位图，而不是将所有的位都设置为key, 一个
  简单的策略就是为每个Key存储M位，并使用来获取KEY名称，使用获取 bit-number/M 第N位bit-number MOD M。
  
* HLL(HyperLogLog), Redis 在 2.8.9 版本添加了 HyperLogLog 结构。 Redis HyperLogLog 是用来做基数统计的算法，HyperLogLog 的优点是，
在输入元素的数量或者体积非常大时，计算基数所需的空间总是固定的、并且很小的。
  * 在Redis 里面，每个HyperLogLog 键只需要花费 12kb 内存，就可以计算出进 2^64 个数不停元素的基数。这个计算基数是，元素越多耗费内存
  就越多的集合形成鲜明对比。
  * 但是，因为 HyperLogLog 只会更具输入元素来计算基数，而不会存储输入元素本省，所以 HyperLogLog 不能像集合那样， 返回输入的各个元素。
  * 基数，比如数据集合 {1, 3, 5, 7, 5, 7, 8}，那么这个数据集基数集为 {1, 3, 5, 7, 8}, 基数（不重复元素）为5。基数估计就是在误差
  可接范围内，快速计算基数。
 
