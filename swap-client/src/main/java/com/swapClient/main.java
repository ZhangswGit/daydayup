package com.swapClient;

import com.swapClient.clent.ChatClient;
import com.swapClient.window.LoginInterFace;
import com.swapClient.window.MainInterface;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @Data :  2021/3/2 14:17
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
public class main {

    public static void main(String[] args) {
        if (args.length == 0) {
            log.info("client start fail ,please check the configuration! {}", args);
            return;
        }
        try {
            new URL("http", args[0], args[1]);
        } catch (MalformedURLException e) {
            log.info("client start fail ,please check the configuration!", args);
        }
        MainInterface mainInterface = MainInterface.getMainInterface();
        LoginInterFace loginInterFace = LoginInterFace.getLoginInterFace();
        ChatClient chatClient = new ChatClient(mainInterface, loginInterFace, args[0], Integer.parseInt(args[1]));
        chatClient.start();
    }
}
