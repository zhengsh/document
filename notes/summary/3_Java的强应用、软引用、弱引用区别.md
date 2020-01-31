### 强引用
* 只要存在强引用，垃圾收集器永远不会回收
例如：Object obj = new Object();

* 帮助垃圾对象回收 obj = null, ArrayList 源码实现
```
/**
 * Removes all of the elements from this list.  The list will
 * be empty after this call returns.
 */
public void clear() {
    modCount++;

    // clear to let GC do its work
    for (int i = 0; i < size; i++)
        elementData[i] = null;

    size = 0;
} 
```
###  软引用
* 用来描述一些还有用但并非必须的对象。对于软应用关联着的对象，在系统将要发生内存泄漏溢出之前，
将会把这些对象列进回收范围之中进行二次回收，如果这次回收还没有足够的内存，就会抛出OOM（内存溢出）异常。

* 在JDK1.2 之后提供了SoftReference类来实现软引用， 这个特征非常适合在：网页缓存、图片缓存等。
  * 浏览器网页缓存实例
  ```
  //获取页面进行浏览
  Browser prev = new Browser();
  //浏览完毕后置为软引用
  SoftReference sr = new SoftReference();
  if (sr.get() == null) {
      prev = rs.get();
  } else {
      prev = new Browser();
      sr = new SoftReference(prev); 
  }
  ```
  * 软引用可以和一个引用队列(ReferenceQueue) 联合使用，如果软引用的对象被垃圾回收器回收，虚拟机会把这个软引用加入到与之关联的引用
  队列中。
  
### 弱引用
* 它与软引用的区别在于：只具有弱引用的对象拥有更短暂的生命周期。在垃圾回收器线程扫描它所管辖的内存区域的过程中，一旦发现了只具有弱引用
的对象，不管当前内存空间足够与否，都会回收它的内存。

* JDK1.2之后，提供了WeakReference 类来实现弱引用

* 示例
```java
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.LinkedList;


public class ReferenceTest {
    private static ReferenceQueue<VeryBig> rq = new ReferenceQueue<>();
    
    public static void  checkQueue() {
        Reference<? extends VeryBig> ref = null;
        while ((ref = rq.poll()) != null) {
            if (ref != null) {
                System.out.println("In queue:" + ((VeryBigWeakRefece) (ref)).id);
            }
        }
    }
    
    public static void main(String[] args){
        int size = 3;
        LinkedList<WeakReference<VeryBig>> weakList = new LinkedList<>();
        for (int i=0; i<size; i++) {
            weakList.add(new VeryBigWeakReference(new VeryBig("Weak-"+i), rq));
            System.out.println("Just created weak: " + weakList.getLast());
        }
        System.gc();
        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
```

### 虚引用
* 虚引用称为幽灵引用或幻影引用。它是最弱的引用关系，一个独享是否有虚引用的存在，完全不会对其生存时间构成影响，也无法通过虚引用来获取对象
实例。为一个对象设置虚应用关联的唯一目的就是能在这个对象被收集器回收时收到一个系统通知。在JDK1.2后，提供了PhantomReference来实现虚引用。

### 四种引用的区别

| 引用类型  | GC回收时间  | 用途  | 生存时间  |
| :------------ | :------------ | :------------ | :------------ |
| 强引用  | never   |  对象的一般状态 | JVM停止运行时  |
| 软引用  | 内存不足 |  对象缓存      | 内存足时终止  |
| 弱引用  | GC时    |  对象缓存      | GC后终止  |
| 虚引用  | unknow  |  unknow       | unknow   |

