### ThreadPoolExecutor 构造方法

#### 线程池创建

    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory,
                              RejectedExecutionHandler handler)

1. corePoolSize (线程池的节本大小)：当提交一个任务到线程池时，线程池会创建一个线程来执行任务，及时其他空闲的基本下城能够执行新任务也会创建线程，
 等到需要执行的任务数大于线程池基本大小时就不必在创建。 如果调用线程池的 prestartAllCoreThreads 方法， 线程池会提前创建并启动所有基本线程。

2. maxmumPoolSize (线程池最大数量) 线程池允许最大的线程数， 如果队列满了，并且已经创建的线程数小于最大线程数， 则线程池会再创建新的线程执行任务。
值得注意的是，如果使用了无界的任务队列这个参数就没有用了。

3. keepAliveTime (线程活动时间)： 线程池工作线程空闲后， 保持存活的时间。所以如果任务很多， 并且每一个任务执行的时间比较短，可以调大时间提高线程利用率。

4. TimeUnit (线程存活时间单位)： 可选的单位有天（Days）、小时（HOURS）、分钟（MINUTES）、毫秒（MILLISECONDS）、微秒（MICROSECONDS， 千分之一毫秒）
和纳秒 （NANOSECOND， 千分之一微秒）

5. workQueue (任务队列)： 用于保存等待执行的任务和阻塞队列。 可以选择一下几个阻塞队列。

  * ArrayBlockingQueue 是一个基于数组的有界阻塞队列， FIFO

  * LinkedBlockingQueue 是一个基于链表结构的组设队列， FIFO， 性能高于ArrayBlockingQueue, Executors.newFixedThreadPool()使用了这个队列。

  * SynchronousQueue 一个不存储元素的阻塞队列。 每一个插入操作必须等到另外一个线程调用移除操作，否则插入操作一直处于阻塞状态，性能高于LinkedBlockingQueue()
工厂方法Executors.newCachedThreadPool() 使用这个队列。 

  * PriorityBlockingQueue: 一个具有优先级的无限阻塞队列。 

6. threadFactory 用于创建线程的工厂， 可以通过线程工厂给每一个创建的线程设置线程名称、是否是后台线程等属性。

7. handler （饱和策略）： 当队列和线程池都满了， 说明线程池处于饱和状态，那么必须采取一种策略来处理新提交的任务. 具体模式在线程池概述中有描述，本次不在赘述。