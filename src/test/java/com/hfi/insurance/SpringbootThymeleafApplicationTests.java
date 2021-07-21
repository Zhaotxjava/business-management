package com.hfi.insurance;

import com.alibaba.fastjson.JSONObject;
import com.hfi.insurance.model.sign.req.QueryInnerAccountsReq;
import com.hfi.insurance.service.OrganizationsService;
import com.hfi.insurance.service.SignedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 因为completableFuture这套使用异步任务的操作都是创建成了守护线程。那么我们没有调用get方法不阻塞这个主线程的时候。
 * 主线程执行完毕。所有线程执行完毕就会导致一个问题，就是守护线程退出。
 * 那么我们没有执行的代码就是因为主线程不再跑任务而关闭导致的。
 * 可能这个不叫问题，因为在开发中我们主线程常常是一直开着的。
 */
@SpringBootTest
class SpringbootThymeleafApplicationTests {
    @Autowired
    private OrganizationsService organizationsService;

    @Test
    void contextLoads() {
        //第一个异步任务，常量任务
        CompletableFuture<String> first = CompletableFuture.completedFuture("hello world");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture<Void> future = CompletableFuture
                //第二个异步任务
                .supplyAsync(() -> "hello siting", executor)
                // () -> System.out.println("OK") 是第三个任务
                .runAfterBothAsync(first, () -> System.out.println("OK"), executor);
        executor.shutdown();


    }
    @Test
    public void test(){
        QueryInnerAccountsReq req = new QueryInnerAccountsReq();
        req.setUniqueId("jianghua");
        req.setPageIndex("1");
        req.setPageSize("10");
        JSONObject jsonObject = organizationsService.queryInnerAccounts(req);
        System.out.println(jsonObject.toJSONString());
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                System.out.println("executorService 是否为守护线程 :" + Thread.currentThread().isDaemon());
                return null;
            }
        });
        final CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("this is lambda supplyAsync");
            System.out.println("supplyAsync 是否为守护线程 " + Thread.currentThread().isDaemon());
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("this lambda is executed by forkJoinPool"+ Thread.currentThread().getName());
            return "result1";
        });
        final CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
//            try {
//                TimeUnit.SECONDS.sleep(5);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            System.out.println("this is task with executor" + Thread.currentThread().getName());
            System.out.println("supplyAsync 使用executorService 时是否为守护线程 : " + Thread.currentThread().isDaemon());
            return "result2";
        }, executorService);
//        System.out.println(completableFuture.get());
//        System.out.println(future.get());
        executorService.shutdown();
    }

}
