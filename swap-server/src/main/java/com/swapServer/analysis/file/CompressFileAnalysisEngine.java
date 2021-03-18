package com.swapServer.analysis.file;

import com.swapServer.analysis.AnalysisEngine;
import com.swapServer.analysis.Analyzed;
import com.swapServer.analysis.Analyzer;
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

    @Override
    public void execute(Analyzed analyzed) {
        if (analyzed == null){
            return;
        }
        analyzers.forEach((k,v) -> {
            if(v.support(analyzed)){
                CompressFile compressFile = (CompressFile) analyzed;
                if (compressFile.isDirectory()) {
                    return;
                }
                Task task = new Task(v, analyzed);
                executorService.submit(task);
            }
        });
    }
}
