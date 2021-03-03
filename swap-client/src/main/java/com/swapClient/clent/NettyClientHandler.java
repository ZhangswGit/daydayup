package com.swapClient.clent;

/**
 * @Data :  2021/2/26 15:36
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */

import bean.Message;
import bean.SwapUser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swapClient.window.LoginInterFace;
import com.swapClient.window.MainInterface;
import com.swapCommon.define.Define;
import com.swapCommon.header.MessageHead;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

@Slf4j
@Data
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private MainInterface mainInterface;

    private LoginInterFace loginInterFace;

    private ObjectMapper objectMapper = new ObjectMapper();

    private DateTimeFormatter formatter =
            DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                    .withLocale( Locale.CHINESE )
                    .withZone( ZoneId.systemDefault() );

    public NettyClientHandler (MainInterface mainInterface, LoginInterFace loginInterFace){
        this.mainInterface = mainInterface;
        this.loginInterFace = loginInterFace;
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
        log.info("{} --> swap-server active", Instant.now());
    }

    /**
     * @param channelHandlerContext
     * @author xiongchuan on 2019/4/28 16:10
     * @DESCRIPTION: 有服务端端终止连接服务器会触发此函数
     * @return: void
     */
    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.close();
        log.info("{} --> swap-server :{} closed", Instant.now());
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {
        if (!(msg instanceof Message)) {
           log.error("Message:{} format error", msg);
            channelHandlerContext.close();
        }
        Message message = (Message) msg;
        switch (message.getMessageHead()) {
            case MessageHead.AUTH_FAIL :
                log.info("{} --> server auth fail", Instant.now());
                loginInterFace.getJTextArea().setText("账号或密码错误！");
                break;
            case MessageHead.AUTH_SUCCESS :
                log.info("{} --> server auth success", Instant.now());
                loginInterFace.setVisible(false);
                //接受之后 LinkHashMap 使用 objectMapper进行转换
                mainInterface.visible(objectMapper.convertValue(message.getBody(), new TypeReference<List<SwapUser>>(){}));
                mainInterface.setLocalSwapUser(message.getLocalSwapUser());
                mainInterface.setVisible(true);
                break;
            case MessageHead.MUTUAL :
                Define define = message.getDefine();
                if (define == Define.goalUserOffline) {
                    log.info("{} --> goal user not exits", Instant.now());
                    mainInterface.getJTextArea().append(formatter.format(Instant.now()) + "\r\n");
                    mainInterface.getJTextArea().append(Define.goalUserOffline.getDetail() + "\r\n");
                } else {
                    log.info("{} --> {} send message {}", Instant.now(), message.getGoalSwapUser().getUserName(), message.getBody());
                    mainInterface.getJTextArea().append(String.format("%s   %s", message.getGoalSwapUser().getUserName(), formatter.format(Instant.now()) + "\r\n"));
                    mainInterface.getJTextArea().append(message.getBody() + "\r\n");
                }
                break;
            case MessageHead.OFFLINE :
                channelHandlerContext.close();
                log.info("{} --> forced offline", message.getLocalSwapUser().getUserName());
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        cause.printStackTrace();
        log.info("server :[{}] have some error {}", channelHandlerContext.channel().remoteAddress(), cause.getMessage());
        channelHandlerContext.close();
    }
}


