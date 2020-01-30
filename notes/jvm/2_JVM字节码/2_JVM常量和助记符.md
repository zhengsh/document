
-XX:+TraceClassLoading 用于追踪类的加载信息并打印出来
-XX:+TraceClassUnloading 用于追踪类的卸载

-XX:+<option>, 表示开启option选项
-XX:-<option>, 表示关闭option选项

-XX:<option>=<value> 表示将option选项值设置为value

## 常量
常量会在编一阶段会存入到这个常量的方法所在类的常量池中
 * 本质上，调用这个类并没有直接引用到定义常量类，因此不会
 * 触发定义常量类的初始化


## 助记符
* ldc 表示将 int, float, String 类型的常量池中推送至栈顶。
* bipush 表示将单字节（-128 - 127 ）的常量值推送至栈顶
* sipush 将一个短整形常量值（-32768 - 32767）推送至栈顶
* icount_1 将int类型的数字1推送至栈顶 (icount_m1 ~ icount_5 )