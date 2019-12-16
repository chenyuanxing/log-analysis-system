package com.cad.entity.leetcode;

public class MyThread extends Thread{
    public int sum = 0;
    public int start = 0;
    public int end = 0;

    public MyThread(String name,int start,int end){
        super(name);
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        for (int i = start;i<=end;i++){
            sum+=i;
        }
        System.out.println(this.getName()+" is over. sum is "+sum);
    }


    public static void main(String args[]) throws InterruptedException {
        int a = 0,b=50,c = 100;
        MyThread myThread1 = new MyThread("myThread1 ",a,b);
        MyThread myThread2 = new MyThread("myThread2 ",b+1,c);
        myThread1.start();
        myThread2.start();

        myThread1.join();
        myThread2.join();

        System.out.println(myThread1.sum+myThread2.sum);

    }
}
