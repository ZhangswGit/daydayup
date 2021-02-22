package com.swapServer.analysis.access;

import com.swapServer.analysis.Analyzed;
import com.swapServer.analysis.Analyzer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccessAnalyzerImpl implements Analyzer {
    @Override
    public void analyze(Analyzed analyzed) {
        AccessAction accessAction = (AccessAction) analyzed;
        log.info(accessAction.id());
    }

    @Override
    public boolean support(Analyzed analyzed) {
        return analyzed instanceof AccessAction;
    }
}
