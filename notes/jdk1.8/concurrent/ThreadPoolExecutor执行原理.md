### ThreadPoolExecutor执行原理 


#### execute 方法

 分析ThreadPoolExecutor的执行原理， 直接从execute 方法开始。

```java
public void execute(Runnbale command) {
    if (commond == null) {
        throw new NullPointerException()
    }
    
    int c = ctl.get();
    
    //1.工作线程 < 核心线程
    if (workerCountOf(c) < corePoolSize) {
        if (andWork(command, true)) {
            retrun;
        }
        c = ctl.get();
    }
    
    //2、运行态， 并尝试将任务加入队列
    if (isRunning(c) && workQueue.offer(command)) {
        int recheck = ctl.get();
        if (! isRunning(recheck) && remove(command)) {
            reject(command);
        } else if (workerCountOf(recheck == 0)) {
            andWorker(null, false);
        }
    } 
    // 3、使用尝试使用最大线程运行
    else if (!addWorker(command, flase)) {
        reject(command);
    }
}
```

在执行execute() 方法时如果状态一直是RUNNING时， 的执行过程如下：

1. 如果 workerCount < corePoolSize, 则创建并启动一个下城来执行新提交的任务;
2. 如果 workerCount >= corePoolSize, 且线程池内的阻塞队列未满, 则将任务添加到该阻塞队列中;
3. 如果 workerCount >= corePoolSize && workerCount < maximumPoolSize, 且线程池内的阻塞队列已满, 则创建并切动一个线程来执行新提交的任务。
4. 如果 workerCount >= maximumPoolSize, 并且线程池的阻塞队列已满， 则根据拒绝策略来处理任务，默认的处理方式是直接抛出异常。

注意： andWorker(null, false); 也是创建一个线程, 但并没有传入任务, 因为任务已经被添加到了workerQueue中了，所以worker在执行的时候，
会直接从workQueue 中获取任务。所以，在workerCountOf(recheck) == 0 时执行andWorker(null); 也是为了保证县城次在RUNNING 状态下必须要有一个线程了执行任务。

整体判断流出如下图所示

![avatar](images/jdk/concurrent/1.png)

#### addWorker 方法

在execute 方法中，用到了 double-check 的思想， 我们看到上述代码并没有同步控制都是基于
乐观锁的check , 如果任务可以创建则进入andWorker(Runnable firestTask, boolean core) 方法，注意上述代码中的三种方式：

  * andWorker(command, true): 创建核心线程执行任务
  * andWorker(command, false): 创建非核心线程任务
  * andWorker(null, false): 创建非核心线程，当前任务为空

andWorker 的返回值是 boolean, 不保证操作成功，下面具体时间 andWorker 方法如下:

```java

private boolean andWorker(Runable firestTask, boolean core) {
    retry:
    //由于线程执行过程中，各种情况都可能处于，通过自旋的方式来保证worker的增加
    for (;;) {
        int c = ctl.get();
        //获取线程运行状态
        int rs = runStateOf(c);
        
        //如果 rs >= SHUTDOWN, 则表示此时不再接受新任务
        //接下来三个条件 通过 && 链接，只要一个不满足就返回false;
        //1. rs == SHUTDOWN, 表示关闭状态，不再接受提交的任务，但却可以继续处理阻塞队列中已经存在的任务;
        //2. firstTask 为空
        //3. Check is queue empty only if necessary.
        if (rs > SHUTDOWN &&
            !(rs == SHUTDOWN && firstTask == null && !workQueue.isEmpty())
            ) {
            return false;
        }
        
       for (;;) {
            //获取线程池中线程数
           int wc = workCountOf(c);
           
           //如果线程数 >= CAPACITY, 也就是ctl 的低29位的最大值，则返回false;
           //这里core来判断 限制线程数量的上限是corePoolSize还是maximumPoolSize;
           //如果core是true表示根据corePoolSize来比较
           //如果core是false表示根据maximumPoolSize来比较
           if (wc >= CAPACITY || 
               wc >= (core ? corePoolSize : maximumPoolSize)) {
               return false;
           }
           //通过CAS原子的方式来增加线程数量，
           //如果成功则跳出第一个for循环;
           if (compareAndIncrementWorkCount(c)) {
               break retry;
           }
           c = ctl.get();
           //如果当亲运行的状态不等于rs, 则说明线程池的状态已经改变了，则返回第一个for循环继续执行
           if (runStateOf(c) != rs) {
               countinue retry;
           }
           // else CAS failded due to workerCount changge; retry inner loop
       }
    }
    
    // 第二部分：创建worker, 这部分使用ReentrantLock 锁
    boolean workerStated = false; // 线程启动标识位
    boolean workerAdded = false; // 线程是否键入workers 标志位
    Worker w = null;
    try {
        //根据firstTask创建worker
        w = new Worker(firstTask);
        //每一个Worker对象会创建一个线程
        final Thread t = w.thread;
        if (t != null) {
            //创建可重入锁
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                //获取线程池的状态
                int rs = runStateOf(ctl.get());
                
                //线程池的状态小于 SHUTDOWN, 表示线程池处于RUNNING状态
                //如果rs 是RUNNING状态或rs 是SHUTDOWN状态并且firstTask为null, 向线程池中添加线程
                //因为再SHUTDOWN状态时不会再添加新的任务， 但是还是处理workerQueue中的任务
                if(rs < SHUTDOWN || 
                 (rs == SHUTDOWN && firstTask == null)){
                    if (t.isAlive) {
                        throw new IllegalThreadStateException();
                    }
                    //worker 是一个hashset
                    worker.add(w);
                    int s = workers.size();
                    //largestPoolSize记录线程池中出现的最大的线程数量
                    if (s > largestPoolSize) {
                        largestPoolSize = s;
                    }
                    workerAdded = true;
                }
            
            } finally {
                mainLock.unlock();
            }
            
            if (workerAdded) {
                //启动线程，Worker 中实现了running 方法，此时会调用Worker的run方法。
                t.start(); 
                workerStated = true;
            }
        }
    } finally {
        if (workerAdded) {
            addWorkerFailed(w); // 失败操作
        }
    }
    return workerStated;
}
```

#### Worker类
线程池中每一个线程对象被封装成了一个Worker对象， ThreadPool 维护的就是一组Worker对象。Worker类继承了AQS， 并实现了Runnable接口。
其中包含两个重要的属性; firstTask 用来保存传入的任务, thread 是在调用的构造方法是通过ThreadFactory 来创建的线程，是用来处理任务的线程。

```java
private final class Worker
        extends AbstractQueuedSynchronizer 
        implements Runnable {

    final Thread thread;
    Runnable firstTask;
    volatile long completedTasks;
    
    Worker (Runnable firstTask)  {
        //把state设置为-1，阻止终端知道调用runWorker方法
        //因为AQS默认state是0, 如果刚创建一个Worker对象， 还没有执行任务时，这时候不应该被中断
        setState(-1);
        this.firstTask = firstTask;
        //创建一个线程, newThread方法传入的参数是this, 因为Worker本身继承了Runnable接口， 也就是一个线程;
        //所以Worker对象在启动的时候悔调用Worker对象中的run方法
        this.thread = getThreadFactory().newThread(this);
    }
}
```
Worker 类继承了AQS, 使用AQS来实现独占锁功的功能， 为什么不使用 ReentranLock 来实现？ 可以看出tryAcquire方法，他是不允许重入的， 而
ReentrantLock 是允许重入的。
1. lock 方法一旦获取独占锁， 表示当前线程正在执行任务中;
2. 如果执行任务，则不应该中断线程;
3. 如果该线程现在不是独占锁的状态，也是空闲状态，说明它没有处理任务，这时可以对该线程进行中断;
4. 线程池中执行shutdown 方法或 tryTerminate 方法时会调用 interruptldleWorkers 方法来中断空闲线程, interruptldleWorkers 方法会使用
tryLock方法来判断线程池中的线程是否是空闲状态。
5. 之所以设置为不可重入的， 是因为在任务调用setCorePoolSize 这类线程池控制的方法时，不会中断正在运行的线程所以，Worker 继承自AQS，用于判断线程池
是否空闲以及是否处于被中断。

```
protected boolean tryAcquire(int unused) {
    //cas修改state, 不可重入;
    //state根据0来判断，所以worker 构造方法中将state设置为-1是为了禁止在执行任务前对线程进行中断;
    //因此，在runWorker方法中会先调用Worker对象中的unlock方法将state设置为0
    if (compareAndSetState(0, 1)) {
        setExclusiveOwnerThread(Thread.currentThread());
        return true;
    }
    return false;
}
```

#### runWorker方法
在Worker类中run方法丢用了runWorker方法来执行任务
```
final void runWorker(Worker w) {
    Thread wt = Thread.currentThread();
    //获取第一个任务
    Runable task = w.firstTask;
    w.firstTask = null;
    //允许中断
    w.unlock();
    //是否因异常退出循环
    boolean completedAbruptly = true;
    try {
        //如果task为空，则通过getTask来获取任务
        while (task != null || (task = getTask() != null)) {
            w.lock();
            
            //如果线程池正在停止，那么要保证当前线程是中断状态;
            //如果不是的话，则要保证当前线程不是中断状态
            if (runStateAtLeast(ctl.get()) ||
                (Thread.interrupted() &&
                    runStateAtLeast(ctl.get(), STOP))) &&
                !wt.isInterrupted()) {
                wt.interrupt();
            }
            
            try {
                //beforeExecute 和 afterExecute 是留给子类来实现的
                beforeExecute(wt, task);
                Throwable thrown = null;
                try {
                    //通过任务方式执行，不是线程方式
                    task.run();
                } catch (RuntimeException x) {
                    thrown = x;
                    throw x;
                } catch (Error x) {
                    thrown = x;
                    throw x;
                } catch (Throwable x) {
                    thrown = x;
                    throw x;
                } finally {
                    afterExecute(task, thrown);
                }
            } finally {
                task = null;
                w.cimpletedTasks++;
                w.unlock();
            }
        }
        completedAbruptly = true;
        
    } finally {
        //processWorkerExit会对completedAbruptly 进行判断，表示在执行过程中是否出现异常
        processWorkerExit(w, completedAbruptly);
    }
} 
```
总结：
1. while 循环不断的通过getTask方法来获取任务;
2. getTask 方法从阻塞队列中获取任务
3. 如果线程池正在停止, 那么要保证当前线程处于中断状态，否则要保证单签线程不是中断状态
4. 调用 task.run() 执行任务;
5. 如果task 为null 则会跳出循环，执行processWorkerExit 方法; 
6. runWorker 方法执行完毕，也代表着Worker 中的run 方法执行完毕，销毁线程。

#### getTask方法
```
private Runnable getTask() {
    //timeOut 变量的值表示上次从阻塞队列中获取任务是否超时
    boolean timeOut = false;
    for (;;) {
        int c = ctl.get();
        int rs = runStateOf(c);
        
        //如果 rs >= SHUTDOWN, 表示线程池非RUNNING状态, 需要再次判断:
        //1. rs >= STOP, 线程池是否在STOP
        //2. 阻塞队列是否为空
        //满足上述条件之一，则将workerCount减一, 并返回null;
        //因为如果当前线程池的状态处于STOP及以上或队列为空，不能从阻塞队列中获取任务。
        if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
            decrementWorkerCount();
            return null;
        }
        
        int wc = workerCountOf();
        
        //timed 变量用于判断是否需要进行超时控制;
        //allowCoreThreadTimeOut 默认为false, 也就是核心线程不允许进行超时;
        //wc > corePoolSuze, 表示当前线程数最大核心线程数量
        //对于超过核心线程数量的线程，需要进行超时控制
        boolean timed = allowCoreThreadTimeOut || wc < corePoolSize;
        
        //wc > maximumPoolSize 的情况是因为可能在此方法执行阶段同事执行了setMaximumPoolSzie 方法
        //timed && timeOut 如果为true, 表示当前操作需要进行超时控制，并且上次从阻塞队列中获取任务发生了超时
        //接下来判断， 如果有效减少数量大于1，或者workerQueue为空，那么将尝试workerCout 减一;
        //如果减一失败也返回重试;
        //如果wc == 1时, 也说明当前线程是线程池的唯一线程 
        if ((wc > maximumPoolSize) || (timed && timeOut)
            && (wc > 1 || workerQueue.isEmpty())) {
            if (compareAndDecrementWorkerCount(c)) {
                return null;
            }
            continue;
        }
        
        //timed为true, 则通过workQueue的poll方法进行超时控制，如果keepAliveTime 时间内没有获取任务， 否则通过take方法， 如果队列为空
        //则take方法会阻塞队列到队列中不为空
        try {
            Runable r = timed ? 
                workerQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                workerQueue.take();
            if (r != null) {
                //如果 r == null, 说明已经超时了，timeOut = true;
                return r;
            }
            timeOut = false;
        } catch (InterruptedException retry) {
            //如果获取任务时当前线程发生了中断，则将timeOut = true
            timeOut = false;
        }
    } 
}
```
注意：第二个if判断, 目的是为了控制线程池的有效线程数量。有上文分析得到，在execute方法时，如果当前线程池的线程数量超过corePoolSize 且
小于maximumPoolSize, 并且阻塞队列已满时， 则可以通过增加工作线程。 但是如果工作线程在超时时间内没有获取到任务。
timeOut=true, 说明workQueue 为空，也就是说当前线程池不需要那么多线程池不需要那么多线程来执行任务了，可以把多余的从corePoolSize 数量的线程
销毁掉，保证线程数量在corePoolSize即可。

什么时候会销毁线程? 当然是 runWorker 方法执行完后，也就是Worker方法执行完成后，由JVM自动回收。
#### processWorkerExit方法
```
private void processWorkerExit(Worker w, boolean completedAbruptly) {
    
    //如果completedAbruptly 为true, 则说明线程执行出行异常，需要将workerCount数量减一
    //如果completedAbruptly 为false, 说明getTask方法中已经对workerCount减一， 这里不需要再减
    if (completedAbruptly) {
        decrementWorkerCount();
    }
    
    final ReentrantLock mainLock = this.mainLock;
    mainLock.lock();
    try {
        //统计完成任务数
        completedTaskCount += w.completedTask;
        //从workers中移除，也就是表示从线程池中移除一个工作线程
        workers.remove(w);
    } finally {
        mainLock.unlock();
    }
    
    //钩子函数
    tryTerminate();
    
    int c = ctl.get();
    
    //当前线程是RUNNING 或者 SHUTDOWN 时, 如果worker 是异常结束那么会直接 addWorker; 
    //如果allowCoreThreadTimeOut = true, 那么等待队列有任务至少保留一个 worker;
    //如果allowCoreThreadTimeOut = flase, workerCount 少于 corePoolSize
    if (runStateLessThan(c, STOP)) {
        if (!completedAbruptly) {
            int min = allowCoreThreadTimeOut ? 0 : corePoolSize;
            if (min == 0 && workQueue,isEmpty()) {
                min = 1;
            }
            if (workerCountOf(c) >= min) {
                return;
            }
            addWorker(null, false);
        }
    }
}
```
至此，processWorkerExit执行完成后，工作线程被销毁 

#### 整体执行流程

工作线程的生命周期，从 execute 方法开始， Worker 使用ThreadFactory 创建型的工作线程，runWorker 通过getTask获取任务，然后执行任务， 
如果getTask返回null, 进入processWorkerExit, 整个线程结束。

![avatar](images/jdk/concurrent/2.png)