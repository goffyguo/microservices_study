package com.goffy.others.order.orderovertime;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.Calendar;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2021/12/30/16:03
 * @Description:
 */
public class JedisTest {

    private static final String ADDR = "127.0.0.1";
    private static final int PORT = 6379;
    private static JedisPool jedisPool = new JedisPool(ADDR, PORT);

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

    /**
     * 生产者，生成5个订单放进去
     */
    public void productionDelayMessage() {
        for (int i = 0; i < 5; i++) {
            Calendar instance = Calendar.getInstance();
            instance.add(Calendar.SECOND, 3);
            int seconds3Later = (int) (instance.getTimeInMillis() / 100);
            JedisTest.getJedis().zadd("OrderId", seconds3Later, "0ID00000" + i);
            System.out.println((System.currentTimeMillis() + "ms:redis 生成了一个订单任务，订单ID为0ID00000" + i));
        }
    }

    /**
     * 消费者，取订单
     */
    public void consumeDelayMessage(){
        Jedis jedis = JedisTest.getJedis();
        while (true){
            Set<Tuple> items = jedis.zrangeWithScores("OrderId", 0, 1);
            if (items==null || items.isEmpty()){
                System.out.println("当前没有等待的任务");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            int  score = (int) ((Tuple)items.toArray()[0]).getScore();
            Calendar cal = Calendar.getInstance();
            int nowSecond = (int) (cal.getTimeInMillis() / 1000);
            if(nowSecond >= score){
                String orderId = ((Tuple)items.toArray()[0]).getElement();
                jedis.zrem("OrderId", orderId);
                System.out.println(System.currentTimeMillis() +"ms:redis消费了一个任务：消费的订单OrderId为"+orderId);
            }
        }
    }

    public static void main(String[] args) {
        new Thread(()->{
           test();
        },"A").start();
        new Thread(()->{
            test();
        },"B").start();
        new Thread(()->{
            test();
        },"C").start();
        new Thread(()->{
            test();
        },"D").start();
        new Thread(()->{
            test();
        },"E").start();
    }

    static void test(){
        JedisTest jedisTest = new JedisTest();
        jedisTest.productionDelayMessage();
        jedisTest.consumeDelayMessage();
    }

}
