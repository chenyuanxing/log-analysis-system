package com.cad.entity.leetcode;

import org.omg.CORBA.TRANSACTION_REQUIRED;

import java.util.concurrent.*;

public class AddPool implements Callable<Integer> {
    private int start, end;

    public AddPool(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public Integer call() throws Exception {
        int sum = 0;
        System.out.println(Thread.currentThread().getName() + " 开始执行!");
        for (int i = start; i <= end; i++) {
            sum += i;
        }
        System.out.println(Thread.currentThread().getName() + " 执行完毕! sum=" + sum);
        return sum;
    }

    public static void main(String[] arg) throws ExecutionException, InterruptedException {
        int start=0, mid=500, end=1000;
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<Integer> future1 = executorService.submit(new AddPool(start, mid));
        Future<Integer> future2 = executorService.submit(new AddPool(mid+1, end));

        int sum = future1.get() + future2.get();
        System.out.println("sum: " + sum);
        executorService.shutdown();
    }


}