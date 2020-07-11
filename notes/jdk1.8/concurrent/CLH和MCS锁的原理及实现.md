## CLH、MCS锁的原理及实现

### 背景
* SMP (Symmetric Multi-Processor)

 对称多处理器结构， 它是相对于非对称多出力技术而言的、应用十分广泛的并行技术。在这种架构中，一天计算机由多个CPU组成，并共享内存和其他资源，
所有的CPU都可以平等的访问内存，I/O和外部中断。虽然同时使用多个CPU，但是从管理的角度来看，他们的表现就像一台单机一样。操作系统任务队列对称
的分布在多个CPU上，从而极大的提高了整个系统的数据处理能力。但是随着CPU数量的增加，每一个CPU都需要访问相同的内存资源，共享内存可能会成为系统
瓶颈，导致CPU资源浪费。

* NUMA (Non-Uniform Memory Access)

 非一致存访问，将CPU分为CPU模块，每个CPU模块有多个CPU组成，并且具有独立的本地内存、I/O槽等，模块之间可以通过互联模块相互访问，访问本地内存
 （本地CPU模块的内存）的速度远远高于访问远程内存（其他的CPU模块的内存）的速度，这也是非一致存储访问的来由。NUMA较好地解决SMP的拓展问题，当CPU
 增加的时候，因为访问远程内存延迟远远大于本地内存，所以系统性能不能线性增加。


### CLH 锁
  CLH （Craig，Landin and Hagersten）是一种基于单向链表的高性能、公平的自旋锁。申请加锁的线程通过前驱节点的变量进行自旋。在前置节点解锁后，
当前节点会结束自旋，并进行加锁。在SMP架构下，CLH更具有优势。在NUMA架构下，若干当前节点与前驱节点在不同的CPU模块下，跨CPU模块会带来额外的开销，
而MCS锁更适用于NUMA架构

加锁过程：
1. 获取当前下层的锁节点，如果为空，进行初始化。
2. 永不方法获取链表的尾节点，并将当前节点设置为尾节点，此时原来的节点为当前节点的前置节点。
3. 如果尾节点为空，表示当前节点是第一个节点。直接加锁成功。
4. 如果尾节点不为空，则基于前置节点的锁值（locked == true） 进行自旋，知道前置节点的锁变为false。

解锁过程：
1. 获取当前线程对应锁的节点，如果节点为空或者为false，则无需解锁，直接返回
2. 同步方法为尾节点赋空值，复制不成功表示当前节点不是尾节点，则需要将当前节点的locked = false解锁节点。如果当前节点是尾节点，则无需为该节点设置。

Demo 如下：
```java
public class CLHLock {

    private final AtomicReference<Node> tail;
    private final ThreadLocal<Node> myNode;
    private final ThreadLocal<Node> myPred;

    public CLHLock() {
        tail = new AtomicReference<>(new Node());
        myNode = ThreadLocal.withInitial(Node::new);
        myPred = ThreadLocal.withInitial(null);
    }

    public void lock() {
        Node node = myNode.get();
        node.locked = true;

        Node pred = tail.getAndSet(node);
        myPred.set(pred);
        while (pred.locked) {
        }
    }

    public void unLock() {
        Node node = myNode.get();
        node.locked = false;
        myNode.set(myPred.get());
    }

    static class Node {
        volatile boolean locked = false;
    }


    private static ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static void main(String[] args) {
        CLHLock lock = new CLHLock();
        Runnable runnable = new Runnable() {
            private int a;

            @Override
            public void run() {
                lock.lock();
                try {
                    a++;
                    Thread.sleep(500);
                    System.out.println("Thread Name:" + Thread.currentThread() + " ; a=" + a);
                } catch (Throwable t) {
                    t.printStackTrace();
                } finally {
                    lock.unLock();
                }
            }
        };
        for (int i = 0; i < 10; i++) {
            executorService.submit(runnable);
        }
    }
}
```

### MCS 锁
  MSC (John Mellor-Crummey and Michael Scott)与CLH最大的不同并不是链表是隐式还是显式，而是线程自选的规则不同：CLH是在前驱节点额locked
域上自旋转等待，而MCS在自己的节点上locked域上自旋等待。正是如此，它解决了CLH在NUMA系统架构中获取locked域状态内存过远的问题。

MCS锁具体实现规则：
1. 队列初始化没有节点，taIl = null
2. 线程A想要获取锁，将自己置于队尾，由于它是一个节点，他的locaked域为false
3. 线程B和线程C相继加入队列，a -> next = b, b -> next = c, B 和C没有获取到锁，处于等待状态，所以locked域为true， 尾指针指向线程C对应的节点。
4. 线程A释放锁后，顺着它的next指正找到了线程B，并把B的locked 域设置为false ,这一动作会触发线程B获取锁。
Demo 如下：
```java
public class MCSLock {

    private final AtomicReference<Node> tail;
    private final ThreadLocal<Node> myNode;

    public MCSLock(AtomicReference<Node> tail, ThreadLocal<Node> myNode) {
        this.tail = tail;
        this.myNode = myNode;
    }

    public MCSLock() {
        this.tail = new AtomicReference<>();
        this.myNode = ThreadLocal.withInitial(Node::new);
    }

    public void lock() {
        System.out.println("Thread Name:" + Thread.currentThread() + " ; - lock start --------------------");
        Node node = myNode.get();
        Node pred = tail.getAndSet(node);
        if (pred != null) {
            node.locked = true;
            pred.next = node;
            while (node.locked) {

            }
        }
        System.out.println("Thread Name:" + Thread.currentThread() + " ; - lock succ --------------------");
    }

    public void unLock() {
        System.out.println("Thread Name:" + Thread.currentThread() + " ; - unLock start --------------------");
        Node node = myNode.get();
        if (node.next == null) {
            if (tail.compareAndSet(node, null)) {
                return;
            }

            while (node.next == null) {
            }
        }
        node.next.locked = false;
        node.next = null;
        System.out.println("Thread Name:" + Thread.currentThread() + " ; - unlock succ --------------------");

    }

    class Node {
        volatile boolean locked = false;
        Node next = null;
    }


    private static ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static void main(String[] args) {
        MCSLock lock = new MCSLock();
        Runnable runnable = new Runnable() {
            private int a;

            @Override
            public void run() {
                lock.lock();
                try {
                    for (int i = 0; i < 2; i++) {
                        a++;
                        Thread.sleep(500);
                    }
                    System.out.println("Thread Name:" + Thread.currentThread() + " ; a=" + a);
                } catch (Throwable t) {
                    t.printStackTrace();
                } finally {
                    lock.unLock();
                }
            }
        };
        for (int i = 0; i < 10; i++) {
            executorService.submit(runnable);
        }
    }
}
```