package cn.edu.cqvie.jvm;

/**
 *
 *  对于静态字段类说，只有直接定义了该字段，的类才会被初始化，
 *  当一个类在的初始化时，要求其父类全部都已经初始化完毕了
 *
 * @author ZHENG SHAOHONG
 */
public class MyTest01 {


    public static void main(String[] args) {
        System.out.println(Child1.str);
    }

}

class MyParent1 {
    public static String str = "hello world";

    static {
        System.out.println("MyParent1 static block");
    }
}

class Child1 extends MyParent1 {
    public  static  String str2 = "welcome";

    static {
        System.out.println("MyChild1 static block");
    }
}