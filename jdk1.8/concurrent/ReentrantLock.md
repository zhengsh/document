### ReentrantLock

可重入锁具有如下几个特征：

* 它是一个可重入互斥锁，它具有使用synchronized 方法和语句所访问的隐式监视器锁相同的行为和语义，但功能更加强大

* ReentrantLock 将会被最近一次成功获取到锁且尚未释放锁的的线程拥有。当锁没有被其他线程拥有，调用lock的线程将成功获取该锁
并返回。可以使用isHeldByCurrentThread() 和 getHoldCount() 方法来检查此情况是否发生。

* 此类的够着方法接受一个可选的 公平 参数。当设置为true时，在多个线程竞争的情况下，这些锁倾向于访问授予等待时间最长的线程。
否则次所将无法保证任何特定访问顺序。与采用默认设置（非公平锁）相比，使用公平锁的程序在多线程访问效率较低，但是在获得锁和保证锁分配的均衡性
时差异很小，不过要注意的是，公平锁不能保证线程调度的公平性。因此使用公平锁的众多线程中的一员可能获得多倍的的成功机会，这种情况发生在
其他活动线没有被处理并且目前并未保持锁时，还要注意的是， 未定时的tryLock方法并没有使用公平设置。因为即使其他线程在等待，只要该所是可用的
此方法就可以获得成功

* 典型的代码建议
```
    class X {
        private final ReentrantLock lock = new ReentrantLock();
        // ....
        public void m() {
            lock.lock();
            try {
                // ... method body
            } finally {
                lock.unlock();
            }
        }
    }
```

* 该类除了实现Lock接口外，还定义了 isLock和 getLockQueueLength 方法，以及相关的protected方法，这些方法对检测和监视可能有用。

* 该类的序列化与内置锁的行为方式相同，一个反序列化的锁处于解除锁定状态，不管它是被序列化的状态是怎么样的。

* 此锁最多支持同一个线程发起的 2147483648 (Integer.MAX_VALUE) 个递归锁，视图超过此限制会导致由锁方法抛出的Error。

#### 可重入性

可重入性： 就是一个线程获取到锁过后然后再在程序内部再次（多次）申请锁。如果一个线程已经获得锁，然后内部可以多次申请
该锁成功。那么我们就可以称为该锁为可重入锁。

解锁的问题：如果锁被获取N次，那么只有锁在被释放同样的n次之后，该锁才能算是完全释放成功。
核心代码：

1.非公平锁判断能够获取到锁


    final boolean nonfairTryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        //1.如果该锁违背任何线程占有，该所能被当前线程获取
        if (c == 0) {
            if (compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
            }
        }
        //2.若被占有，检查占有线程是否是当前线程
        else if (current == getExclusiveOwnerThread()) {
            //3.再次获取，计数器加一
            int nextc = c + acquires;
            if (nextc < 0) // overflow
                throw new Error("Maximum lock count exceeded");
            setState(nextc);
            return true;
        }
        return false;
    }
    
2.释放锁


    protected final boolean tryRelease(int releases) {
        //1.同步状态减一
        int c = getState() - releases;
        if (Thread.currentThread() != getExclusiveOwnerThread())
            throw new IllegalMonitorStateException();
        boolean free = false;
        if (c == 0) {
            //2.只有当前同步状态为0时，表示锁被成功释放，返回true
            free = true;
            setExclusiveOwnerThread(null);
        }
        //3.锁未被完全释放返回false
        setState(c);
        return free;
    }

#### 公平性和非公平性

公平性和非公平性是正对获取锁而言的，如果是一个公平锁，那么锁的获取顺序符合绝对时间的请求顺序满足FIFO。
核心代码:

1.构造非公平锁

    public ReentrantLock() {
        sync = new NonfairSync();
    }

另外提供了一种方式，传入一个boolean值，true标识为公平锁，false 标识为非公平锁

    public ReentrantLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
    }

2.获取锁，我们可以看到，唯一不同的是增加了hasQueuedPredecessors来判断，如果有前驱几点说明有线程比当前线程更早的请求资源，根据公平性，
当前线程请求资源失败。如果有当前节点没有前驱节点的话，在做后面的逻辑判断的必要性。**公平锁每次都是从同步队列中的第一个节点获取到锁，
而非公平所则不一定，有可能刚释放锁的线程能够再次获取到锁**。

    protected final boolean tryAcquire(int acquires) {
        final Thread current = Thread.currentThread();
        int c = getState();
        if (c == 0) {
            if (!hasQueuedPredecessors() &&
                compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
            }
        }
        else if (current == getExclusiveOwnerThread()) {
            int nextc = c + acquires;
            if (nextc < 0)
                throw new Error("Maximum lock count exceeded");
            setState(nextc);
            return true;
        }
        return false;
    }
    
    
区别：

1. 公平锁每次获取得到锁为同步队列中的第一个节点，**保证请求资源时间上的绝对顺序**， 而非公平锁有可能刚释放的锁线程下次继续获取该锁，
则有可能导致其他线程永远无法获取到锁，**造成“饥饿”现象**。

2. 公平锁为了保证时间上的绝对顺序，需要频繁的切换上下文，而非公平锁会降低一定的上下文切换，降低性能开销。因此，
ReentrantLock默认选择的是非公平锁，则是为了减少一部分上下文切换，**保证系统更大的吞吐量**。

### 
