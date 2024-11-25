/*
 ************************************
 * @项目名称: shard
 * @文件名称: TaskExecutorConfig
 * @Date 2018/10/10
 * @Author will.zhao@bbex.io
 * @Copyright（C）: 2018 BitBili Inc.   All rights reserved.
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的。
 **************************************
 */
package io.bbex.bb.server.config;

import io.grpc.internal.GrpcUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@Slf4j
public class TaskExecutorConfig {

    @Bean(name = "bbExecutor")
    public Executor bbExecutor() {
        int corePoolSize = 25;
        int maxPoolSize = 50;
        int queueCapacity = 1000;
        String name = "bb-";
        return initExecutor(corePoolSize, maxPoolSize, queueCapacity, name);
    }

    @Bean(name = "routerExecutor")
    public Executor routerExecutor() {
        int corePoolSize = 100;
        int maxPoolSize = 300;
        int queueCapacity = 1000;
        String name = "router-order-";
        return initExecutor(corePoolSize, maxPoolSize, queueCapacity, name);
    }

    @Bean(name = "settleExecutor")
    public Executor settleExecutor() {
        int corePoolSize = 100;
        int maxPoolSize = 300;
        int queueCapacity = 1000;
        String name = "settle-order-";
        return initExecutor(corePoolSize, maxPoolSize, queueCapacity, name);
    }

    @Bean(name = "queryExecutor")
    public Executor queryExecutor() {
        int corePoolSize = 20;
        int maxPoolSize = 200;
        int queueCapacity = 400;
        String name = "query-executor";
        return initExecutor(corePoolSize, maxPoolSize, queueCapacity, name);
    }

    @Bean(name = "pushExecutor")
    public Executor pushExecutor() {
        int corePoolSize = 50;
        int maxPoolSize = 300;
        int queueCapacity = 1000;
        String name = "grpc-push-";
        return initExecutor(corePoolSize, maxPoolSize, queueCapacity, name);
    }

    @Bean(name = "depositWithdrawExecutor")
    public Executor depositWithdrawExecutor() {
        int corePoolSize = 10;
        int maxPoolSize = 30;
        int queueCapacity = 100;
        String name = "deposit-withdraw-executor-";
        return initExecutor(corePoolSize, maxPoolSize, queueCapacity, name);
    }

    @Bean(name = "positionChangedExecutor")
    public Executor positionChangedExecutor() {
        int corePoolSize = 4;
        int maxPoolSize = 8;
        int queueCapacity = 600;
        String name = "position-changed-";
        return initExecutor(corePoolSize, maxPoolSize, queueCapacity, name);
    }

    @Bean
    public ScheduledExecutorService delayTaskExecutor() {

        int corePoolSize = 4;

        return Executors.newScheduledThreadPool(corePoolSize);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(50);
        scheduler.setThreadNamePrefix("TaskScheduler-");
        scheduler.setAwaitTerminationSeconds(10);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        return scheduler;
    }

    @Bean
    public ExecutorService streamExecutor() {
        int corePoolSize = 10;
        int maxPoolSize = 30;
        long keepAliveTime = 0;

        return new ThreadPoolExecutor(corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new StreamThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    public static class StreamThreadFactory implements ThreadFactory {

        private final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "tradeStream-thread-" + threadNumber.getAndIncrement());
        }
    }

    private ThreadPoolTaskExecutor initExecutor(int corePoolSize, int maxPoolSize, int queueCapacity, String name) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setKeepAliveSeconds(120);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(name);
        executor.initialize();
        return executor;
    }
}