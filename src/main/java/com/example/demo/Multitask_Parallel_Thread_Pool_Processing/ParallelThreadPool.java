package com.example.demo.Multitask_Parallel_Thread_Pool_Processing;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mac on 2019/2/15.
 */

public class ParallelThreadPool {

    //核心线程池大小
    private static int poolSize=Runtime.getRuntime().availableProcessors();
    //控制线程的并行
    private static CountDownLatch countDownLatch;
    //线程池执行器
    private static ThreadPoolExecutor threadPoolExecutor;
    //任务数组
    private static List<String> parallelArr;
    /**
     *@param array 任务数组，测试只放任务名
     * @param countLatch  允许并行的任务数量
     * @param capacity    队列大小为1000
     */
    public ParallelThreadPool(List<String> array,int countLatch, int capacity){
        parallelArr=array;
        //101线程存活时间，时间单位为SECONDS
        threadPoolExecutor=new ThreadPoolExecutor(poolSize,poolSize+1,101, TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>(capacity));
        countDownLatch =new CountDownLatch(countLatch);
    }

     public void run() throws  InterruptedException{
        for (String s:parallelArr){
            threadPoolExecutor.execute(new ParallelThread(s,countDownLatch));
        }
         countDownLatch.await();//等待
     }
}


class ParallelThread implements  Runnable{
    CountDownLatch countDownLatch;
    String name;
    ParallelThread(String  name,CountDownLatch countDownLatch){
       this.countDownLatch=countDownLatch;
       this.name=name;
    }

    @Override
    public void run() {
        System.out.println("任务"+name);
        countDownLatch.countDown();//任务减一
    }
}
