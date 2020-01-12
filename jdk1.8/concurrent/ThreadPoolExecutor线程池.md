### ThreadPoolExecutor

#### 说明

* 线程池解决了两个不同的问题
  * 提升性能：他们通常需要执行大量的异步任务，可以减少每个任务的调用开销，并且他们提供了一种限制和管理资源（包括线程）的方法，是的性能提升。
  * 统计信息：每一个ThreadPoolExecutor还维护了一些基本的统计信息，如已完成的任务数等。
  
* 为了广泛的在上线文中使用，此类提供提供了许多可调整参数和可拓展性钩子。但是，在常见场景中，我们预配置了几种线程池，我们希望开发者使用更
方便的Executors的工厂方法直接使用。
  * Executors.newCachedThreadPool 无界限超线程池，自动线程回收
  * Executors.newFixedThreadPool 固定大小的线程池
  * Executors.newSingleThreadExecutor 单一后台线程
  
* Core and maximun pool sizes 核心和最大线程池数量
