package com.swapServer.analysis;

import com.swapServer.analysis.file.CompressFile;
import com.swapServer.analysis.file.CompressFileAnalyzerImpl;
import com.swapServer.analysis.file.DetectionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @Data :  2021/2/25 14:55
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Service("CompressFileAnalysisEngine")
@Slf4j
public class CompressFileAnalysisEngine extends AnalysisEngine {

    @Autowired
    CompressFileAnalyzerImpl compressFileAnalyzer;

    @Override
    public void execute(Analyzed analyzed) {
        if (analyzed == null){
            return;
        }
        CompressFile compressFile = (CompressFile) analyzed;
        if (compressFile.isDirectory()) {
            return;
        }

        Task task = new Task(compressFileAnalyzer, compressFile);
        executorService.submit(task);
    }

    class Task implements Runnable {

        private Analyzed analyzed;
        private CompressFileAnalyzerImpl compressFileAnalyzer;

        public Task(CompressFileAnalyzerImpl compressFileAnalyzer, Analyzed analyzed){
            this.analyzed = analyzed;
            this.compressFileAnalyzer = compressFileAnalyzer;
        }

        @Override
        public void run() {
            CompressFile compressFile = (CompressFile) analyzed;
            log.info("开始解析{}文件", compressFile.getName());
            compressFileAnalyzer.analyze(analyzed);
        }
    }
}
