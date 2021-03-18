package com.swapServer.analysis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AnalysisEngine {

    @Autowired
    private ApplicationContext applicationContext;

    protected ExecutorService executorService;

    protected Map<String, Analyzer> analyzers;

    @PostConstruct
    public void run(){
        int corePoolSize = Math.max(Runtime.getRuntime().availableProcessors(), 2);
        executorService = new ThreadPoolExecutor(corePoolSize, corePoolSize * 2, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10240), new ThreadPoolExecutor.DiscardOldestPolicy());
        analyzers = applicationContext.getBeansOfType(Analyzer.class);
        log.info("Analysis engine started");
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
        log.info("Analysis engine shutdown");
    }

    protected class Task implements Runnable{
        Analyzer analyzer;
        Analyzed analyzed;

        public Task(Analyzer analyzer, Analyzed analyzed){
            this.analyzed = analyzed;
            this.analyzer = analyzer;
        }

        @Override
        public void run() {
            analyzer.analyze(analyzed);
        }
    }

    public void execute(Analyzed analyzed){
        analyzers.forEach((k,v) -> {
            if(v.support(analyzed)){
                Task task = new Task(v, analyzed);
                executorService.submit(task);
            }
        });
    }
}
