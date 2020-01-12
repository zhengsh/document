### ThreadPoolExecutor 

#### 执行原理
 
 分析ThreadPoolExecutor的执行原理， 直接从execute 方法开始。
 
```
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


整体判断流出如下图所示

![avatar](../image/1.png)


在execute 方法中，用到了 double-check 的思想， 我们看到上述代码并没有同步控制都是基于
乐观锁的check , 如果任务可以创建则进入andWorker(Runnable firestTask, boolean core) 方法，注意上述代码中的三种方式：
  
  * andWorker(command, true): 创建核心线程执行任务
  * andWorker(command, false): 创建非核心线程任务
  * andWorker(null, false): 创建非核心线程，当前任务为空
  
andWorker 的返回值是 boolean, 不保证操作成功，下面具体时间 andWorker 方法如下:
   
```

private boolean andWorker(Runable firestTask, boolean core) {
    // 第一部分： 自旋、CAS、重读ctl 等结合，直到确定是否可以创建worker
    // 可以则跳出循环继续操作，否则返回false
    retry:
    for (;;) {
        int c = ctl.get();
        int rs = runStateOf(c);
        
        //Check is queue empty only if necessary.
        if (rs > SHUTDOWN &&
            !(rs == SHUTDOWN && firstTask == null && !workQueue.isEmpty())
            ) {
            return false;
        }
        
       for (;;) {
           int wc = workCountOf(c);
           if (wc >= CAPACITY || 
               wc >= (core ? corePoolSize : maximumPoolSize)) {
               return false;
           }
           if (compareAndIncrementWorkCount(c)) {
               break retry;
           }
           c = ctl.get();
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
        w = new Worker(firstTask);// 创建worker
        final Thread t = w.thread;
        if (t != null) {
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                // 获取到锁以后任需检查ctl, 可能在上一个获取到锁处理的线程可能会改变runState
                // 如 ThreadFactory 创建失败或县城次被 shutdown 等
                
                int rs = runStateOf(ctl.get());
                if(rs < SHUTDOWN || 
                 (rs == SHUTDOWN && firstTask == null)){
                    if (t.isAlive) {
                        throw new IllegalThreadStateException();
                    }
                    worker.add(w);
                    int s = workers.size();
                    if (s > largestPoolSize) {
                        largestPoolSize = s;
                    }
                    workerAdded = true;
                }
            
            } finally {
                mainLock.unlock();
            }
            
            if (workerAdded) {
                t.start(); //启动线程
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

andWorker 的工作分为2部分

  * 第一部分： 原子操作， 判断是否可以创建worker. 通过自旋， CAS， ctl 等操作。 判断继续创建还是返回false, 自旋周期一般很短。 
  
  * 第二部分： 同步创建worker, 并启动线程。
  