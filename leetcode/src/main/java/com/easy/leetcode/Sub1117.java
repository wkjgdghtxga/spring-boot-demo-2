package com.easy.leetcode;

/*

1117. H2O 生成

现在有两种线程，氢 oxygen 和氧 hydrogen，你的目标是组织这两种线程来产生水分子。

存在一个屏障（barrier）使得每个线程必须等候直到一个完整水分子能够被产生出来。

氢和氧线程会被分别给予 releaseHydrogen 和 releaseOxygen 方法来允许它们突破屏障。

这些线程应该三三成组突破屏障并能立即组合产生一个水分子。

你必须保证产生一个水分子所需线程的结合必须发生在下一个水分子产生之前。

换句话说:

如果一个氧线程到达屏障时没有氢线程到达，它必须等候直到两个氢线程到达。
如果一个氢线程到达屏障时没有其它线程到达，它必须等候直到一个氧线程和另一个氢线程到达。
书写满足这些限制条件的氢、氧线程同步代码。

 

示例 1:

输入: "HOH"
输出: "HHO"
解释: "HOH" 和 "OHH" 依然都是有效解。
示例 2:

输入: "OOHHHH"
输出: "HHOHHO"
解释: "HOHHHO", "OHHHHO", "HHOHOH", "HOHHOH", "OHHHOH", "HHOOHH", "HOHOHH" 和 "OHHOHH" 依然都是有效解。
 

限制条件:

输入字符串的总长将会是 3n, 1 ≤ n ≤ 50；
输入字符串中的 “H” 总数将会是 2n；
输入字符串中的 “O” 总数将会是 n。

来源：力扣（LeetCode）
链接：https://leetcode-cn.com/problems/building-h2o
*/
public class Sub1117 {
    public static void main(String[] args) throws InterruptedException {
        //测试用例字符串
        String test = "HOHOHHOOHOHHHHHOHHHOH";

        //生成结果字符串
        StringBuffer result = new StringBuffer();

        //注意：创建的Runnable任务，无法启动线程，必须依托其他类或线程启动
        //创建生成氧气任务
        Runnable releaseHydrogen = () -> result.append("H");

        //创建生成氧气任务
        Runnable releaseOxygen = () -> result.append("O");

        //保存线程数组
        Thread threads[] = new Thread[test.length()];

        H2O h2o = new H2O();
        for (int i = 0; i < test.length(); ++i) {
            Thread thread = null;
            //根据获得的字符调用相应的氧气或氢气线程
            if (test.charAt(i) == 'O') {
                thread = new OGenerator(h2o, releaseOxygen);
            } else if (test.charAt(i) == 'H') {
                thread = new HGenerator(h2o, releaseHydrogen);
            }
            //开始线程
            thread.start();
            //保存到线程数组
            threads[i] = thread;
        }

        //等侍所有线程执行完
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }

        //输出结果串
        System.out.println(result.toString());
    }
}

//氢气生成线程
class HGenerator extends Thread {
    H2O h2o;
    Runnable releaseHydrogen;

    public HGenerator(H2O h2o, Runnable releaseHydrogen) {
        this.h2o = h2o;
        this.releaseHydrogen = releaseHydrogen;
    }

    @Override
    public void run() {
        try {
            h2o.hydrogen(releaseHydrogen);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

//氧气生成线程
class OGenerator extends Thread {
    H2O h2o;
    Runnable releaseOxygen;

    public OGenerator(H2O h2o, Runnable releaseOxygen) {
        this.h2o = h2o;
        this.releaseOxygen = releaseOxygen;
    }

    @Override
    public void run() {
        try {
            h2o.oxygen(releaseOxygen);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class H2O {
    public H2O() {
    }

    int h = 0;

    public synchronized void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
        while (h == 2) {
            this.wait();
        }
        releaseHydrogen.run();
        ++h;
        this.notify();
    }

    public synchronized void oxygen(Runnable releaseOxygen) throws InterruptedException {
        while (h < 2) {
            this.wait();
        }
        releaseOxygen.run();
        h = 0;
        this.notify();
    }
}