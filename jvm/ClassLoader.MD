### ClassLoader 类详解

1. 类加载器是负责装载类的对象，ClassLoader是一个抽象类, 
给定类的二进制名称，类加载器应尝试查找获生成构成类定义的数据，一种典型
的策略就是将名称转换为文件名，然后读取来自文件系统该名称的"类文件"。
  * 二进制名称举例
  "java.lang.String"
  "javax.swing.JSpinner$DefaultEditor"
  "java.security.KeyStore$Builder$FileBuilder$1"
  "java.net.URLClassLoader$3$1" => URLClassLoader 中的第一个匿名内部类中的第一个匿名内部类。

2. 每一个Class对象都包含了一个ClassLoader对象, 定义到Class.#getClassCloader。

3. 针对数组类对象不是由类加载创建的。但会根据JVM虚拟机运行时自动创建，
数组类的类加载器是由元素的类加载加载相同类型，如果元素类型是原生类型，则数组类
没有类加载器。
  * **数组的类加载实与元素类型的类加载器是一样的结果**
  * **如果数组元素实基础类型，那么数组没有类加载器**

4. 应用中需要自定义类加载器，以ClassLoader的子类拓展Java虚拟机动态加载方式。

5. 类加载器通常可以安全管理人员用来指定安全域

6. 类使用委托模型（delegation model）寻找类和资源。 ClassLoader 的每一个实例都有一个关联的
父类加载器。当要求找到一个类或者资源的时候， 实例将委派搜索尝试找到与其父类加载器的类
或资源类或资源本生。虚拟机的内置加载器，称为"根类（启动类）加载器（bootstrap class loader）"
它本身没没有双亲，但是他可以作为其他类的双亲。

7. 支持并发加载的类加载器称为 parallel capable 类加载器，需要注册自己在初始
化时通过调用 ClassLoader.registerAsParallelCapable 方法， 请注意ClassLoader 类
被注册为并行，默认情况下可用。但是，其之类仍需要注册自己如果它具有并行功能。在委托模型不严格的环境中。
类加载器需要具有并行能力，否则类加载器可以导致死锁，因为类加载器锁是为类加载过程中的持续时间。

8. 通常，JAVA虚拟机从本地文件加载类系统加载类，与平台相关形势，例如在UNIX系统上，虚拟机从目录
指定的目录中加载类在CLASSPATH 环境变量中。

9. 然而，某些类可能不是源自文件，有的可能是其他来源。
如：网络，也可以是由应用程序。方法 defineClass 将字节数组转换为class的实例
可以通过Class.newInstance 方法来创建一个类的实例。

10. 由类加载器创建的对象的方法和构造函数可能引用其它类。为了确定所引用的类
，Java 虚拟机调用 loaderClass 方法最初创建类的类加载器。

11. 例如，一个应用程序可以创建一个网络类加载器来从服务器下载类文件代码示例如下：
    
    ClassLoader loaderClass = new NetworkClassLoader(host, port);
    Object main = loaderClass.loadClass("Main", true).newInstance();

12. 网络类加载器子类必须定义方法 findClass, 和 loadClass 来加载来自网络的类
下载类的字节后应该使用方法 defineClass 来创建一个类的实例，代码如下：

    class NetworkClassLoader extends ClassLoader {
        String host;
        int port;
        
        //加载字节数组，然后加载类对象
        public Class findClass() {
             byte[] b = loadClassData(name);
             return defineClass(name, b, 0 , b.length);
        }
        
        //类的数据以字节数组返回
        private byte[] loadClassData(St
2. 每一个Class对象都包含了一个ClassLoader对象, 定义到Class.#getClassCloader。

3. 针对数组类对象不是由类加载创建的。但会根据JVM虚拟机运行时自动创建，
数组类的类加载器是由元素的类加载加载相同类型，如果元素类型是原生类型，则数组类
没有类加载器。
  * **数组的类加载实与元素类型的类加载器是一样的结果**
  * **如果数组元素实基础类型，那么数组没有类加载器**

4. 应用中需要自定义类加载器，以ClassLoader的子类拓展Java虚拟机动态加载方式。

5. 类加载器通常可以安全管理人员用来指定安全域

6. 类使用委托模型（delegation model）寻找类和资源。 ClassLoader 的每一个实例都有一个关联的
父类加载器。当要求找到一个类或者资源的时候， 实例将委派搜索尝试找到与其父类加载器的类
或资源类或资源本生。虚拟机的内置加载器，称为"根类（启动类）加载器（bootstrap class loader）"
它本身没没有双亲，但是他可以作为其他类的双亲。

7. 支持并发加载的类加载器称为 parallel capable 类加载器，需要注册自己在初始
化时通过调用 ClassLoader.registerAsParallelCapable 方法， 请注意ClassLoader 类
被注册为并行，默认情况下可用。但是，其之类仍需要注册自己如果它具有并行功能。在委托模型不严格的环境中。
类加载器需要具有并行能力，否则类加载器可以导致死锁，因为类加载器锁是为类加载过程中的持续时间。

8. 通常，JAVA虚拟机从本地文件加载类系统加载类，与平台相关形势，例如在UNIX系统上，虚拟机从目录
指定的目录中加载类在CLASSPATH 环境变量中。

9. 然而，某些类可能不是源自文件，有的可能是其他来源。
如：网络，也可以是由应用程序。方法 defineClass 将字节数组转换为class的实例
可以通过Class.newInstance 方法来创建一个类的实例。

10. 由类加载器创建的对象的方法和构造函数可能引用其它类。为了确定所引用的类
，Java 虚拟机调用 loaderClass 方法最初创建类的类加载器。

11. 例如，一个应用程序可以创建一个网络类加载器来从服务器下载类文件代码示例如下：
    
    ClassLoader loaderClass = new NetworkClassLoader(host, port);
    Object main = loaderClass.loadClass("Main", true).newInstance();

12. 网络类加载器子类必须定义方法 findClass, 和 loadClass 来加载来自网络的类
下载类的字节后应该使用方法 defineClass 来创建一个类的实例，代码如下：

    class NetworkClassLoader extends ClassLoader {
        String host;
        int port;
        
        //加载字节数组，然后加载类对象
        public Class findClass() {
             byte[] b = loadClassData(name);
             return defineClass(name, b, 0 , b.length);
        }
        
        //类的数据以字节数组返回
        private byte[] loadClassData(String name) {
            // load the class data from the connection
        }
    }
    
13. 总结：
   loadClass、findClass、defineClass.
   
   * loadClass
   方法默认实现按照如下得方法来寻找类：
     * 1. 调用findLoadedClass 来检查该类是否被加载过了
     * 2. 调用父类loadClass 来加载该类，如果父加载器为空，那么就会使用虚拟机的根类加载器。
     * 3. 调用findClass来寻找该类
   如果类使用上述步骤被找到，就会调用resolveClass解析类的方法。
   ClassLoader 类的之类建议重写 findClass 类方法
   如果该类已经被加载了，为了保障类只被加载一次，会同步到getClassLoadingLock
   
   * findClass
   findClass 首先需要加载指定的字节数组输出流（得到二进制字节码流），然后委托defineClass方法来获取
Class 对象。 

   * defineClass
   defineClass 就是将字节数组转换为一个类的实例， 在Class被使用之前它必须被解析。默认会分配一个
保护域（ProtectionDomain）给这个类，确保返回的Class类的信息都是正常的（比如：包名这些正常）。
   参数说明：
       name 类的名称
       b    字节数组，off 到 off+len-1 的字节码信息要符合java虚拟机规范
       off  起始的偏移量
       len  字节码长度
   返回说明：
       返回一个期待的class对象
       jdk 不允许定义 java. 开头包名称。会报SecurityException