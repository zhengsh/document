#### isAssignableFrom()方法与instanceof关键字区别\
* Class.isAssignableFrom() 是用来判断Class1 和另一个类 Class2 是否相同或者是另一个类的父类或者接口。

```java
    A.class.isAssignableFrom(B.class)
```
调用者和参数都是java.lang.Class类型，上面的例子如果返回为true, 则表示A是B的父类或者接口，B可以是一个类或者接口

* instanceof 是用来判断一个对象实例是否是一个类或接口额或者其子类接口的实例。

```java
    o instanceof TypeName
```
第一个参数是对象实例名，第二个参数是具体的类名或者接口名

* isInstance() 方法，用来检查实例
```java
    C.class.isInstance(b)
``` 
如果返回true， 则表示b 是C的实例或者子类

#### 完整例子

```java
public class MyTest1 {

    public static void main(String[] args) {
        B b = new B();
        if (C.class.isAssignableFrom(B.class)) {
            System.out.println(1);
        }
        System.out.println(2);
        if (b instanceof C) {
            System.out.println(3);
        }
        System.out.println(4);
        if (C.class.isInstance(b)) {
            System.out.println(5);
        }
    }

    static class A {

    }

    static class B extends A implements C {

    }

    static interface C {

    }

}

```

输出结果:

    1
    2
    3
    4
    5
