package com.swapServer.analysis;

import com.swapServer.analysis.file.CompressFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Data :  2021/2/25 14:55
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
@Service("CompressFileAnalysisEngine")
public class CompressFileAnalysisEngine extends AnalysisEngine {

    @Resource(name = "CompressFileAnalyzerImpl")
    private Analyzer analyzer;

    @Override
    public void execute(Analyzed analyzed) {
        if (analyzed == null){
            return;
        }
        CompressFile compressFile = (CompressFile) analyzed;
        if (compressFile.isDirectory()) {
            return;
        }

        Task task = new Task(analyzer, compressFile);
        executorService.submit(task);
    }

    class Task implements Runnable {

        private Analyzed analyzed;
        private Analyzer analyzer;

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
