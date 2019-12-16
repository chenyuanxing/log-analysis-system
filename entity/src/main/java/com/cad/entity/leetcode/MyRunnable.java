package com.cad.entity.leetcode;

public class MyRunnable implements Runnable {


    private int start, end;
    private int sum = 0;

    public MyRunnable(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " 开始执行!");
        for(int i = start; i <= end; i++) {
            sum += i;
        }
        System.out.println(Thread.currentThread().getName() + " 执行完毕! sum=" + sum);
    }


    public static void main(String[] args) throws InterruptedException {
        int start = 0, mid = 500, end = 1000;
        MyRunnable run1 = new MyRunnable(start, mid);
        MyRunnable run2 = new MyRunnable(mid + 1, end);
        Thread thread1 = new Thread(run1, "线程1");
        Thread thread2 = new Thread(run2, "线程2");

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
        int sum = run1.sum + run2.sum;
        System.out.println("ans: " + sum);
    }
}