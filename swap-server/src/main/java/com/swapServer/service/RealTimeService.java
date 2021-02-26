package com.swapServer.service;

import com.swapServer.model.response.RealTimeResponse;
import com.swapServer.webSocket.ConsoleMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Data :  2021/2/25 16:57
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Service
@Slf4j
public class RealTimeService {

    public void sendCompress(String remote, RealTimeResponse realTimeResponse) {
        ConsoleMessageHandler.sendMessage(remote, realTimeResponse);
    }
}
