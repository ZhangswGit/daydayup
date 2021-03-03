package com.swapServer.netty.handler;

import bean.LoginUser;
import bean.Message;
import bean.SwapUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swapCommon.define.Define;
import com.swapCommon.header.MessageHead;
import com.swapServer.bean.User;
import com.swapServer.constants.ErrorAlertMessages;
import com.swapServer.netty.Model.UserModel;
import com.swapServer.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @Data :  2021/2/26 13:45
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
public class MessageHandler extends ChannelInboundHandlerAdapter {

    private UserService userService;

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static final Set<ChannelHandlerContext> clientCount = new HashSet<>();

    private static final ConcurrentHashMap<ChannelId, ChannelHandlerContext> channelHandlerContextMap = new ConcurrentHashMap<ChannelId, ChannelHandlerContext>();

    private static final ConcurrentHashMap<Long, ChannelId> userChannelIdMap = new ConcurrentHashMap<Long, ChannelId>();

    public MessageHandler(UserService userService){
        this.userService = userService;
    }

    public MessageHandler(){
        this.userService = new UserService();
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object obj) {
        if (!(obj instanceof Message)) {
            log.error("Message:{} format error", obj);
            offline(null, channelHandlerContext);
        }

        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Message message = (Message) obj;

        switch (message.getMessageHead()) {
            case MessageHead.AUTH:
                authenticateHandle(channelHandlerContext, message);
                break;
            case MessageHead.MUTUAL:
                mutualHandle(channelHandlerContext, message);
                break;
        }
    }

    /**
     * 客户端断开连接触发
     *
     * @param channelHandlerContext
     */
    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) {

        InetSocketAddress inetSocketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
        String clientIp = inetSocketAddress.getAddress().getHostAddress();
        int clientPort = inetSocketAddress.getPort();

        offline(null, channelHandlerContext);
        log.info("client [{}:{}] offline", clientIp, clientPort);
    }

    /**
     * @param channelHandlerContext
     * @author xiongchuan on 2019/4/28 16:10
     * @DESCRIPTION: 有客户端连接服务器会触发此函数
     * @return: void
     */
    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
        String clientIp = inetSocketAddress.getAddress().getHostAddress();
        int clientPort = inetSocketAddress.getPort();
        channelHandlerContext.channel().read();//
        log.info("{} --> client [{}:{}] online ", Instant.now(), clientIp, clientPort);
    }

    void authenticateHandle(ChannelHandlerContext channelHandlerContext, Message message) {
        //认证
        Object body = message.getBody();
        UserModel userModel = null;
        try {
            LoginUser loginUser = objectMapper.convertValue(body, LoginUser.class);
            userModel = userService.auth(loginUser.getUserName(), loginUser.getPassWord());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            channelHandlerContext.channel().writeAndFlush(Message.builder()
                    .messageHead(MessageHead.AUTH_FAIL)
                    .build());
            log.info("{} auth fail", body);
            return;
        }

        if (userModel == null) {
            channelHandlerContext.channel().writeAndFlush(Message.builder()
                    .messageHead(MessageHead.AUTH_FAIL)
                    .build());
            log.info("{} auth fail", body);
            return;
        }

        ChannelId channelIdOld = userChannelIdMap.get(userModel.getUserId());
        if (channelIdOld != null) {
            //先下线已经登陆的用户
            offline(userModel, channelHandlerContextMap.get(channelIdOld));
        }
        //上线
        online(userModel, channelHandlerContext);
    }

    public void mutualHandle(ChannelHandlerContext channelHandlerContext, Message message) {
        if (!clientCount.contains(channelHandlerContext)) {
            log.error("local user:{} not certified", message.getLocalSwapUser().getUserName());
            channelHandlerContext.writeAndFlush(Message.builder().messageHead(MessageHead.OFFLINE).body(ErrorAlertMessages.UserNotCertified).build());
            channelHandlerContext.close();
            return;
        }
        ChannelId channelId = userChannelIdMap.get(message.getGoalSwapUser().getUserId());
        if (channelId == null) {
            log.error("goal user:{} offline", message.getGoalSwapUser().getUserName());
            channelHandlerContext.writeAndFlush(Message.builder()
                    .messageHead(MessageHead.MUTUAL)
                    .define(Define.goalUserOffline)
                    .build());
            return;
        }
        ChannelHandlerContext goalChannelHandlerContext = channelHandlerContextMap.get(channelId);
        if (goalChannelHandlerContext == null) {
            log.error("goal user:{} offline", message.getGoalSwapUser().getUserId());
            userChannelIdMap.remove(message.getGoalSwapUser().getUserId());
            channelHandlerContext.writeAndFlush(Message.builder()
                    .messageHead(MessageHead.MUTUAL)
                    .define(Define.goalUserOffline)
                    .build());
            return;
        }
        log.info("user:{} send message to user:{} / message:{}", message.getLocalSwapUser().getUserName(), message.getGoalSwapUser().getUserName(), message.getBody());
        //目标用户id 和 发送方用户id互换，用于客户端理解
        goalChannelHandlerContext.writeAndFlush(Message.builder()
                .messageHead(MessageHead.MUTUAL)
                .goalSwapUser(message.getLocalSwapUser())
                .localSwapUser(message.getGoalSwapUser())
                .body(message.getBody()).build());
    }

    /**
     * 上线
     *
     * @param userModel
     * @param channelHandlerContext
     */
    void online(UserModel userModel, ChannelHandlerContext channelHandlerContext) {
        ChannelId channelId = channelHandlerContext.channel().id();
        channelHandlerContextMap.put(channelId, channelHandlerContext);
        userChannelIdMap.put(userModel.getUserId(), channelId);
        clientCount.add(channelHandlerContext);

        List<UserModel> users = userService.findAllUser();

        channelHandlerContext.channel().writeAndFlush(Message.builder()
                .messageHead(MessageHead.AUTH_SUCCESS)
                .body(users.stream()
                        .filter(user -> !StringUtils.equals(user.getUserName(), userModel.getUserName()))
                        .map(user -> SwapUser.builder()
                                .userId(user.getUserId())
                                .userName(user.getUserName())
                                .build()).collect(Collectors.toList()))
                .localSwapUser(SwapUser.builder()
                        .userName(userModel.getUserName())
                        .userId(userModel.getUserId())
                        .build())
                .build()
        );
        log.info("{} --> localUser:{} auth success; clientCount:{}", Instant.now(), userModel.getUserName(), clientCount.size());
    }

    /**
     * 下线
     *
     * @param userModel
     * @param channelHandlerContext
     */
    void offline(UserModel userModel, ChannelHandlerContext channelHandlerContext) {
        if (channelHandlerContext != null) {
            ChannelId channelId = channelHandlerContext.channel().id();
            channelHandlerContext.writeAndFlush(Message.builder().messageHead(MessageHead.OFFLINE).build());
            channelHandlerContext.close();
            channelHandlerContextMap.remove(channelId);
            clientCount.remove(channelHandlerContext);
        }
        if (userModel != null) {
            userChannelIdMap.remove(userModel.getUserId());
        }
        log.info("{} --> offline; clientCount:{}", Instant.now(), clientCount.size());
    }

}
