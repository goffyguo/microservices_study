package com.goffy.others.order.orderovertime;

import sun.awt.windows.ThemeReader;

import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2021/12/30/16:57
 * @Description:
 */
public class ThreadTest {
    private static final int threadNum = 10;
    private static CountDownLatch cdl = new CountDownLatch(threadNum);

    static class DelayMessage implements Runnable{


        @Override
        public void run() {
            try {
                cdl.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JedisTest jedisTest = new JedisTest();
            jedisTest.consumeDelayMessage();
        }
    }

    public static void main(String[] args) {
        JedisTest jedisTest = new JedisTest();
        jedisTest.productionDelayMessage();
        for (int i = 0; i < threadNum; i++) {
            new Thread(new DelayMessage()).start();
            cdl.countDown();
        }
    }

}
