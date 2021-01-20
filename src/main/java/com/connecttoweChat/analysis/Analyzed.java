package com.connecttoweChat.analysis;

public interface Analyzed {
    String id();
    default String type(){return this.getClass() + id();}
}
