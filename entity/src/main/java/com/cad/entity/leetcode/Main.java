package com.cad.entity.leetcode;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Comparator comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return b-a;
            }
        };

        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(comparator);

        priorityQueue.add(100);
        priorityQueue.add(10);
        priorityQueue.add(1);
        priorityQueue.add(1000);
        priorityQueue.add(10000);
        int k   = priorityQueue.size();
        for (int i = 0;i<k;i++){
            System.out.println(priorityQueue.poll());
        }
    }

}