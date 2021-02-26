package com.swapServer.analysis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("LocalAnalysisEngine")
public class LocalAnalysisEngine extends AnalysisEngine{

    @Autowired
    private ApplicationContext applicationContext;

    private ExecutorService executorService;

    private Map<String, Analyzer> analyzers;

    @Override
    public void execute(Analyzed analyzed){
        analyzers.forEach((k,v) -> {
            if(v.support(analyzed)){
                Task task = new Task(v, analyzed);
                executorService.submit(task);
            }
        });
    }

    @PostConstruct
    @Override
    public void run(){
        executorService = new ThreadPoolExecutor(4, 4 * 2, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10240), new ThreadPoolExecutor.DiscardOldestPolicy());
        analyzers = applicationContext.getBeansOfType(Analyzer.class);
        log.info("Analysis engine started");
    }

    class Task implements Runnable{
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

}
