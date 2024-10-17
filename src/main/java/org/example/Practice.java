package org.example;

import java.util.*;

public class Practice {
    public static void main(String[] args) {
        MyThread t1 = new MyThread();
        MyThread t2 = new MyThread();
        MyThread t3 = new MyThread();
        MyThread t4 = new MyThread();
        MyThread t5 = new MyThread();

        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
    }
}

class MyThread extends Thread{
    @Override
    public void run() {
        for (int i=1;i<10;i++){
            System.out.println(new Date());
        }
    }
}
