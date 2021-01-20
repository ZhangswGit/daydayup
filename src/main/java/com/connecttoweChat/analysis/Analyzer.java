package com.connecttoweChat.analysis;

public interface Analyzer {
    void analyze(Analyzed analyzed);
    boolean support(Analyzed analyzed);
}
