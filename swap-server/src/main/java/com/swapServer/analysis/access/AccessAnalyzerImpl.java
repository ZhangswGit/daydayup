package com.swapServer.analysis.access;

import com.swapServer.analysis.Analyzed;
import com.swapServer.analysis.Analyzer;
import com.swapServer.bean.SystemLog;
import com.swapServer.service.SystemLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccessAnalyzerImpl implements Analyzer {

    @Autowired
    private SystemLogService systemLogService;

    @Override
    public void analyze(Analyzed analyzed) {
        AccessAction accessAction = (AccessAction) analyzed;
        systemLogService.save(SystemLog.builder().build());
    }

    @Override
    public boolean support(Analyzed analyzed) {
        return analyzed instanceof AccessAction;
    }
}
