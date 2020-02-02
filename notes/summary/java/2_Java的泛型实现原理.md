### Java泛型原理
* Java泛型在编译期间会进行泛型擦除，如：List<String>, 最终编译后会存储为List并且会记录类型。

* 在底层List存储的是一个Object类，通过记录的类型来Check外部push对象类型是否匹配或者通过类型转换来实现。