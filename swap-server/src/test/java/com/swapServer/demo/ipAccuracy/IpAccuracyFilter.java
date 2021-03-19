package com.swapServer.demo.ipAccuracy;

public interface IpAccuracyFilter {
    String name();

    void doFilter(String goalIp, IpAccuracyChain ipAccuracyChain);
}
