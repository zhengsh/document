### JVM 内存模型

#### 内存结构

1. 虚拟机栈: Stack Frame 栈帧, 方法执行过程中的压栈和出栈的执行过程。
2. 程序计数器 (Program Counter), 字节码执行顺序。
3. 本地方法栈: native 来获取的JVM提供的本地方法。
4. 堆(Heap): JVM管理的最大的一块内存空间。与堆相关的是一个重要概念是垃圾收集器。现代几乎所有的垃圾收集器都是采取的分代收集算法，
所以对空间也是基于这一点进行了相应的划分：新生代与年老代. Eden 空间, From Survivor 空间与 To Survivor 空间。
5. 方法区(Method Area): 存储元数据信息。永久代 (Permanent Generation) 从JDK1.8开始彻底废弃永久代，
使用元空间(Meta Space)来替代。
6. 运行时常量池：方法区的一部分内容。
7. 直接内存： Direct Memory, 堆外内存，不是由JVM来管理，是通过操作系统来管理的。与Java NIO密切相关的。Java 通过DirectByteBuffer来操作直接内存。

#### Java对象的创建过程

new 关键创建对象的3个步骤
1. 在堆内存中创建出对象的实例。
2. 为对象的实例成员变量赋初始值。
3. 将对象的引用返回。

指针碰撞 (前提是堆中的空间通过一个指针进行分割，一侧是已经被占用的空间，另一侧是未被占用的空间) 
空闲列表 (前提是堆空间中已经被使用，未被使用的是交织在一起的。这时虚拟机就需要通过一个列表来记录哪些是可以使用的，哪些是已经被使用的，
接下来找出可以容纳新创建的对象的未被使用的空间，再此空间存放对象， 同时还要修改列表上的记录)。

对象在内存中的布局：
1. 对象头
2. 实例数据 (即我们再一个类中声明)
3. 对齐填充 (可选)

引用访问对象的方式:
1. 使用句柄的方式。
2. 使用直接指针的方式。


#### 分析工具jvisualvm

1. 配置JVM参数
```jvm
-Xms2m
-Xmx2m
-XX:+HeapDumpOnOutOfMemoryError

-XX:MaxMetaspaceSize=10m  //设置元空间大小
```

#### Jdk1.8 元空间
元空间存储类的基本元数据,如类的层级信息,方法数据和方法信息（如字节码，栈和变量大小),运行时常量池,已确定的符号引用和虚方法表。


#### jcmd（从jdk1.7开始新增加的命令）

1. jcmd pid VM.flags 查看jvm的启动参数
2. jcmd pid help 查看当前可用命令
3. jcmd pid help JFR.dump 查看具体命令的选项
4. jcmd pid PerfCounter.print 查看JVM性能相关的参数
5. jcmd pid VM.uptime 查看类的启动时长
6. jcmd pid GC.class_histogram: 查看类的统计信息
7. jcmd pid Thread.print: 查看线程的堆栈信息
8. jcmd pid GC.heap_dump filename: 导出Heap Dump文件, 导出的文件可以通过jvisualvm 查看
9. jcmd pid VM.system_properties: 查看JVM的属性
10. jcmd pid VM.version: 查看JVM进程的版本信息
11. jcmd pid VM.command_line: 查看JVM启动的命令行参数信息

#### Jstack
查看或者导出Java进程中的堆栈信息
jmc: 
jhat: 分析堆转储信息, 在jdk9 以后已被移除. 移除原因: https://www.infoq.com/news/2015/12/OpenJDK-9-removal-of-HPROF-jhat/

#### JVM内存溢出分析场景
1. 堆溢出, 如果不断的创建对象，那么在对象的数量到达最大堆的容量后就会产生堆溢出
2. 虚拟机栈和本地方法栈溢出。
  * 如果栈的深度大于虚拟机允许的最大深度。则抛出 StackOverflowError异常。
  * 如果虚拟机在拓展栈的时候，无法申请到足够内存。则抛出 OutOfMemoryError异常。
3. 方法区和运行时常量池溢出。
  * 如果在运行时不断地创建大量的类最会导致方法区溢出。
4. 本机直接内存溢出，可以通过反射Unsafe实例来分配直接内存或通过DirectByteBuffer类