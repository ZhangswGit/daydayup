package com.swapServer.analysis;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AnalysisEngine {

    protected ExecutorService executorService;

    @PostConstruct
    public void run(){
        executorService = new ThreadPoolExecutor(4, 4 * 2, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10240), new ThreadPoolExecutor.DiscardOldestPolicy());

        log.info("CompressFileAnalysisEngine started");
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
        log.info("Analysis engine shutdown");
    }

    public abstract void execute(Analyzed analyzed);
}
