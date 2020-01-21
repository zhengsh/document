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
7. 直接内存： Direct Memory, 堆外内存，不是由JVM来管理，是通过操作系统来管理的。与Java NIO密切相关的。Java 通过DirectByteBuffer来
操作直接内存。

