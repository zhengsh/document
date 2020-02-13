### RejectedExecutionException异常处理

#### 一、异常可能分析
通过对ThreadPoolExecutorler 的分析，引发java.util.concurrent.RejectedExecutionException主要有2种情况
1.当线程池显示的调用shutdown()之后，再向线程池中提交任务的时候，如果你配置的拒绝策略是ThreadPoolExecutor.AbortPolicy，
这个异常也会被抛出来。

2.当你的排队任务策略为有界限队列，并且设置了拒绝的策略为ThreadPoolExecutor.AbortPolicy, 当线程次的数量达到了
maximumPoolSize的时候，你再向它提交任务，就会抛出RejectedExecutionException异常源码如下
```java

/**
 * A handler for rejected tasks that throws a
 * {@code RejectedExecutionException}.
 */
public static class AbortPolicy implements RejectedExecutionHandler {
    /**
     * Creates an {@code AbortPolicy}.
     */
    public AbortPolicy() { }

    /**
     * Always throws RejectedExecutionException.
     *
     * @param r the runnable task requested to be executed
     * @param e the executor attempting to execute this task
     * @throws RejectedExecutionException always
     */
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        throw new RejectedExecutionException("Task " + r.toString() +
                                             " rejected from " +
                                             e.toString());
    }
}
```


#### 二、结合项目分析

1. 在程序中线程池设置的配置如下
```java
@Configuration
public class MarketMessageConfig {

    @Bean
    public ThreadPoolExecutor marketMessageThreadPool() {
        int nThreads = Runtime.getRuntime().availableProcessors() * 2;
        return new ThreadPoolExecutor(nThreads, nThreads * 2,
                0, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1024),
                new ThreadFactory() {
                    private final AtomicInteger threadNumber = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "market-message-" + threadNumber.getAndIncrement());
                    }
                },
                new ThreadPoolExecutor.AbortPolicy());

    }
}
```
2. 由于这个线程池是定时任务重提交任务用到，可能会出现一次有2000条以上的数据需要处理。

#### 三、解决方案
1.尽量调大maximumPoolSize或者workQueue 例如：Integer.MAX_VALUE, 个人建议调大队列的大小，因为线程池运行线程过多会
导致CPU切换的成本上而降低运行效率。

```java
public class MarketMessageConfig {

    @Bean
    public ThreadPoolExecutor marketMessageThreadPool() {
        int nThreads = Runtime.getRuntime().availableProcessors() * 2;
        return new ThreadPoolExecutor(nThreads, nThreads * 2,
                0, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(Integer.MAX_VALUE),
                new ThreadFactory() {
                    private final AtomicInteger threadNumber = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "market-message-" + threadNumber.getAndIncrement());
                    }
                },
                new ThreadPoolExecutor.AbortPolicy());

    }
}
```