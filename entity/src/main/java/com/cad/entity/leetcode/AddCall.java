package com.cad.entity.leetcode;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class AddCall implements Callable {

    private int s,e;
    public AddCall(int s,int e){
        this.s = s;
        this.e = e;
    }
    @Override
    public Integer call() throws Exception {
        int sum = 0;
        for(int i = s;i<=e;i++){
            sum+=i;
        }
        System.out.println(Thread.currentThread().getName() + " 执行完毕! sum=" + sum);
        return sum;
    }
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int a = 0,b = 500,c = 1000;

        AddCall addCall1 = new AddCall(a,b);
        AddCall addCall2 = new AddCall(b+1,c);
        FutureTask futureTask1 = new FutureTask(addCall1);
        FutureTask futureTask2 = new FutureTask(addCall2);
        Thread thread1 = new Thread(futureTask1,"线程1");
        Thread thread2 = new Thread(futureTask2,"线程2");
        thread1.start();
        thread2.start();
        int sum1 = (int) futureTask1.get();
        int sum2 = (int) futureTask2.get();
        System.out.println("ans: " + (sum1 + sum2));
    }
}
