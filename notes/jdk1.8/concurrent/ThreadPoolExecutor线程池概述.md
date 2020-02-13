### ThreadPoolExecutor

#### 概述（参考java doc）

* 线程池解决了两个不同的问题
  * 提升性能：他们通常需要执行大量的异步任务，可以减少每个任务的调用开销，并且他们提供了一种限制和管理资源（包括线程）的方法，是的性能提升。
  * 统计信息：每一个ThreadPoolExecutor还维护了一些基本的统计信息，如已完成的任务数等。
  
* 为了广泛的在上线文中使用，此类提供提供了许多可调整参数和可拓展性钩子。但是，在常见场景中，我们预配置了几种线程池，我们希望开发者使用更
方便的Executors的工厂方法直接使用。
  * Executors.newCachedThreadPool 无界限超线程池，自动线程回收
  * Executors.newFixedThreadPool 固定大小的线程池
  * Executors.newSingleThreadExecutor 单一后台线程
  
* Core and maximun pool sizes 核心和最大线程池数量

一个 ThreadPoolExecutor 会自动调节线程池大小，（见 getPoolSize()）按照 corePoolSize  范围（见 getCorePoolSize()） 和maximumPoolSzie
（见 getMaximumPoolSize()）. 当一个新的任务是在方法 execute(Runnable)提交， 不到 corePoolSize 线程正在运行。将创建新的线程来处理请求， 即使
其他工作线程空闲。如果有超过 corePoolSize 和 maximumPoolSize 相同。你将创建一个固定大小而定线程池。 通过设置maximumPoolSize 到无限大 如
Integer.MAX_VALUE，允许线程池容纳任意并发任务数， 最典型的是核心线程数和最大线程数在运行时设置，但也可以动态更改使用
setCorePoolSize(int) 和 setMaximumPoolSize(int) 动态设置

* 在需求结构

默认情况下, 即使核心线程最初创建和开始任务到达时， 但这个是可以重写动态使用方法 prestartCoreThread 或者平restarAllCoreThreads 
你如果想启动线程池构件一个非空队列。


* 创建新的线程

新的线程使用的是 ThreadFactory 创建，如果没有设置， 将使用executors.defaultThreadFactory() 用于创建线程是子同一个 ThreadGroup 同
NORM_PRIORITY 优先和非守护状态。通过提供不同的threadFactory, 您可以更改线程的名字， 线程组， 优先级， 守护状态等。 如果ThreadFactory
未能创建一个线程， 当被问到newThread返回 null请求时无法创建线程， 则执行程序将继续， 但可能无法执行任务， 线程应该拥有 “modifyThread”
RuntimePermission 如果线程池的工作线程或其他线程不具有此权限， 则服务可能会被降级： 配置更改可能不会及时生效，并且关闭县城次可能处于终止但是未完成状态。


* 存活时间

如果当前线程池具有多余corePoolSize线程， 则如果空闲超过 keepAliveTime （见 getKeepAliveTime(TimeUnit) ） 则多余的线程将被终止。这
提供了当线程未被主动使用是减少资源消耗的方法，如果稍后线程池变得更加活跃， 将构建星的线程。此参数也可以使用方法 setKeepAliveTime(long, TimeUnit)
动态更改。 使用Long.Max_VALUE, TimeUnit.NANOSECNDS 有效地禁用空闲线程在关闭之前终止。默认情况下，仅当存在多余corePoolSize 线程时， 保持活动策略
才适用。 但是方法allowCoreThreadTimeOut(boolean) 也可以用于将这个超时策略应用于核心线程， 只要keepAliveTime 值不为零。

* 队列
任何 BlockingQueue 可用于传输和保留提交的任务， 这个队列的使用与线程池大小相互作用
  * 如果小于corePoolSize 线程正在运行，Executor 会添加一个新的线程而不是排队。
  * 如果 corePoolSize 或更多的线程正在运行，Executor 会将任务排队请求而不是添加一个新的线程。
  * 如果请求无法排队， 则会创建一个先的线程， 除恶分这个讲超出maximumPoolSize, 否则任务将被拒绝。
排队一般由三种策略：

1. 直接切换， 一个工作队列的很好的默认选择是一个 SynchronousQueue, 将任务交给线程， 无需另外控制。 在这里，如果没有线程可以立即运行， 那么
尝试排队任务会失败， 因此将构建一个新的线程。处理可能具有内部依赖的关系的请求集时， 此测录可避免锁定。 直接切换同创需要无限制的maximumPoolSize,
已避免拒绝新提交的任务。 这反过来允许无限线程增长的可能性， 当命令继续以平均速度比他们可以处理的速度更快地到达时。

2. 无界队列，使用无界队列（例如LinkedBlockingQueue没有预定容量）会导致新的任务，在队列中等待， 当所有corePoolSize 线程都很忙。 因此，
不会再创建corePoolSizes线程 （因此， 最大值大小的值没有影响） 每一个任务完成独立于其他任务时， 这可能是适当的， 因此任务不会影响其他执行；例如，
在网页服务器中， 虽然这种排队佛你搞个可以助于平滑瞬态突发的请求， 但是当命令继续到达的平均速度比可以处理的速度更快时， 它承认无界工作队列增长的可能性。

3. 有边界的队列，有限队列（例如, ArrayBlockingQueue）有助于在使用 maxPoolSizes 时，防止资源耗尽，但可能更加难调整和控制。队列大小和最大池大小可能彼此交易:
使用大队列和小型池可以最大限度的减少cpu使用率，OS资源和上下文切换开销，但可能导致人为的低吞吐量。 如果任务频繁阻塞（例如，如果他们是I/O绑定），则系统可能能够安排
比您允许的更多线程的时间。 使用小型队列冲程需要较大的线程池大小，这样可以使CPU繁忙，但可能会遇到不可接受的调度开销， 也降低了吞吐量。


* 决绝策略

方法 execute(Runnable) 中提交的新任务将在执行程序关闭时被拒绝，并且当执行程序对最大线程和工作队列容量使用有限边界并且饱和时。 在任一情况下，
execute 方法调用 RejectedExecutionHandler。rejectedExcution(Runnable, ThreadPoolExecutor) 其的方法 RejectedExecutionHandler. 
提供了4个预定义的处理程序策略。

1. 在默认ThreadPoolExecutor.AbortPolicy ， 处理程序会引发运行RejectedExecutionException排斥反应。

2. 在ThreadPoolExecutor.CallerRunsPolicy 中，调用execute 本身的线程运行任务， 这提供了一个简单的反馈控制机制，降低新任务提交的速度。

3. 在ThreadPoolExecutor.DiscardPolicy 中， 简单地删除无法执行的任务。

4. 在ThreadPoolExecutor.DiscardOldestPolicy 中， 如果执行程序没有关闭， 则工作队列头部的任务被删除， 然后重试执行（可能会再次失败， 导致重复）

可以定义和使用其他类型的RejectedExecutionHandler 类。 这样做需要特别注意， 特别是当策略被设计为仅在特定容量或排队策略下工作时。

* 钩子方法

该类提供了在每一个任务执行之前和之后调用的protected 覆盖的 beforeExecute(Thread, Runnable) 和 afterExecute(Runnable, Throwable) 
方法，这些可以用来操纵执行环境; 例如：重新初始化ThreadLocals ， 收集统计信息或添加日志条目。 另外， 方法terminated() 可被覆盖， 以执行
执行程序完全终止后需要执行的任何特殊处理。

如果内部钩子或回调方法抛出异常， 内部工作线程可能会失败并突然终止。

* 队列维护

方法getQueue() 允许访问工作队列进行监视和调试。 强烈不鼓励此方法用于任何其他的目的。 当提供大量排队任务被取消时， 两种提供的方法 remove(Runnable)
和 purge() 可用于协助进行存储回收。 

* 拓展示例

这个类大部分覆盖了一个或多个受保护的钩子方法， 例如， 这里是一个添加一个简单的暂停/恢复功能的子类。
```java
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PausableThreadPoolExecutor extends ThreadPoolExecutor {

    private boolean isPaused;
    private ReentrantLock pauseLock = new ReentrantLock();
    private Condition unoaused = pauseLock.newCondition();

    public PausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        pauseLock.lock();
        try {
            while (isPaused) {
                unoaused.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            pauseLock.unlock();
        }
    }


    public void pause() {
        pauseLock.lock();
        try {
            isPaused = true;
        } finally {
            pauseLock.unlock();
        }
    }

    public void resume() {
        pauseLock.lock();
        try {
            isPaused = false;
            unoaused.signalAll();
        } finally {
            pauseLock.unlock();
        }
    }

}
``` 



