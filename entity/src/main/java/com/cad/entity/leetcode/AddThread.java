package com.cad.entity.leetcode;

public class AddThread extends Thread {

    private int start, end;

    private int sum = 0;

    public AddThread(String name, int start, int end) {
        super(name);
        this.start = start;
        this.end = end;
    }
    public void run() {

        System.out.println("Thread-" + getName() + " 开始执行!");
        for (int i = start; i <= end; i ++) {
            sum += i;
        }
        System.out.println("Thread-" + getName() + " 执行完毕! sum=" + sum);
    }

    public static void main(String[] args) throws InterruptedException {
        int start = 0, mid = 500, end = 1000;

        AddThread thread1 = new AddThread("线程1", start, mid);
        AddThread thread2 = new AddThread("线程2", mid + 1, end);

        thread1.start();
        thread2.start();


        // 确保两个线程执行完毕
        thread1.join();
        thread2.join();

        int sum = thread1.sum + thread2.sum;
        System.out.println("ans: " + sum);
    }
}