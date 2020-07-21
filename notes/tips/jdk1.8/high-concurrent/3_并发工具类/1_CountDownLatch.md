### 线程之间的通讯-CountDownLatch

* CountDownLatch允许一个或多个线程等待其他线程完成操作。

* 对一个文本中所有的数字并行求和
```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Demo2 {

    private int[] nums;

    public Demo2(int line) {
        nums = new int[line];
    }

    public void calc(String line, int index, CountDownLatch latch) {
        String[] nums = line.split(",");
        int total = 0;
        for (String num : nums) {
            total += Integer.parseInt(num);
        }
        this.nums[index] = total;
        System.out.println(Thread.currentThread() + " 执行计算任务 。。。。" + line + " 结果为：" + total);
        latch.countDown();
    }

    public void sum() {
        System.out.println("汇总线程开始执行。。。。");
        int total = 0;
        for (int i = 0; i < this.nums.length; i++) {
            total += this.nums[i];
        }
        System.out.println("最终的结果为。。。。" + total);
    }


    @SneakyThrows
    public static void main(String[] args) {
        List<String> list = readFile();
        int size = list.size();
        CountDownLatch latch = new CountDownLatch(size);
        Demo2 demo = new Demo2(size);
        for (int i = 0; i < size; i++) {
            final int j = i;
            new Thread(() -> {
                demo.calc(list.get(j), j, latch);
            }).start();
        }

        latch.await();

        demo.sum();
    }

    @SneakyThrows
    public static List<String> readFile() {
        List<String> list = new ArrayList<>();
        String line = null;
        URL url = Demo.class.getClassLoader().getResource("nums.txt");
        assert url != null;
        FileReader in = new FileReader(url.getFile());
        BufferedReader br = new BufferedReader(in);
        while ((line = br.readLine()) != null) {
            list.add(line);
        }
        br.close();
        in.close();
        return list;
    }
}

```

* 文件 “nums.txt”
```txt
1,2,3
4,5,6
7,8,9
```