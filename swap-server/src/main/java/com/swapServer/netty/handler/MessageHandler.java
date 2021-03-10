package com.swapServer.netty.handler;

import bean.LoginUser;
import bean.Message;
import bean.SwapUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swapCommon.define.Define;
import com.swapCommon.header.MessageHead;
import com.swapServer.constants.ErrorAlertMessages;
import com.swapServer.netty.Model.ClientUserModel;
import com.swapServer.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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

    //统计上线客户端
    private static final Set<ChannelHandlerContext> clientCount = new HashSet<>();
    //存储ChannelHandlerContext与用户关系
    private static final ConcurrentHashMap<ChannelHandlerContext, Long> channelHandlerContextUserMap = new ConcurrentHashMap<ChannelHandlerContext, Long>();
    //存储用户与ChannelHandlerContext关系
    private static final ConcurrentHashMap<Long, ChannelHandlerContext> userChannelHandlerContextMap = new ConcurrentHashMap<Long, ChannelHandlerContext>();

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
            offline(channelHandlerContext, false);
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

        offline(channelHandlerContext, false);
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

    private void authenticateHandle(ChannelHandlerContext channelHandlerContext, Message message) {

        Object body = message.getBody();
        ClientUserModel clientUserModel;
        try {
            //账号密码认证
            LoginUser loginUser = objectMapper.convertValue(body, LoginUser.class);
            clientUserModel = userService.auth(loginUser.getUserName(), loginUser.getPassWord());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            channelHandlerContext.channel().writeAndFlush(Message.builder()
                    .messageHead(MessageHead.AUTH_FAIL)
                    .define(Define.userOrPassWordError)
                    .build());
            log.info("{} auth fail", body);
            return;
        }

        if (clientUserModel == null) {
            //认证失败
            channelHandlerContext.channel().writeAndFlush(Message.builder()
                    .messageHead(MessageHead.AUTH_FAIL)
                    .define(Define.userOrPassWordError)
                    .build());
            log.info("{} auth fail", body);
            return;
        }

        ChannelHandlerContext channelHandlerContextOld = userChannelHandlerContextMap.get(clientUserModel.getUserId());
        if (channelHandlerContextOld != null) {
            //先下线已经登陆的用户
            offline(channelHandlerContextOld, true);
        }
        //上线
        online(clientUserModel, channelHandlerContext);
    }

    private void mutualHandle(ChannelHandlerContext channelHandlerContext, Message message) {
        if (!clientCount.contains(channelHandlerContext)) {
            log.error("local user:{} not certified", message.getLocalSwapUser().getUserName());
            channelHandlerContext.writeAndFlush(Message.builder().messageHead(MessageHead.OFFLINE).body(ErrorAlertMessages.UserNotCertified).build());
            channelHandlerContext.close();
            return;
        }
        ChannelHandlerContext channelHandlerContextOld = userChannelHandlerContextMap.get(message.getGoalSwapUser().getUserId());
        if (channelHandlerContextOld == null) {
            log.error("goal user:{} offline", message.getGoalSwapUser().getUserName());
            channelHandlerContext.writeAndFlush(Message.builder()
                    .messageHead(MessageHead.MUTUAL)
                    .define(Define.goalUserOffline)
                    .build());
            return;
        }
        log.info("user:{} send message to user:{} / message:{}", message.getLocalSwapUser().getUserName(), message.getGoalSwapUser().getUserName(), message.getBody());
        //目标用户id 和 发送方用户id互换，用于客户端理解
        channelHandlerContextOld.writeAndFlush(Message.builder()
                .messageHead(MessageHead.MUTUAL)
                .goalSwapUser(message.getLocalSwapUser())
                .localSwapUser(message.getGoalSwapUser())
                .body(message.getBody()).build());
    }

    /**
     * 上线
     * @param userModel
     * @param channelHandlerContext
     */
    void online(ClientUserModel userModel, ChannelHandlerContext channelHandlerContext) {
        userChannelHandlerContextMap.put(userModel.getUserId(), channelHandlerContext);
        channelHandlerContextUserMap.put(channelHandlerContext, userModel.getUserId());
        clientCount.add(channelHandlerContext);

        //除当前用户之外的用户
        List<ClientUserModel> users = userService.findAllUser();

        channelHandlerContext.channel().writeAndFlush(Message.builder()
                .messageHead(MessageHead.AUTH_SUCCESS)
                .body(Optional.ofNullable(users).map(x ->x.stream()
                        .filter(user -> !StringUtils.equals(user.getUserName(), userModel.getUserName()))
                        .map(user -> SwapUser.builder()
                                .userId(user.getUserId())
                                .userName(user.getUserName())
                                .build()).collect(Collectors.toList())).orElse(null))
                .localSwapUser(SwapUser.builder()
                        .userName(userModel.getUserName())
                        .userId(userModel.getUserId())
                        .build())
                .build()
        );
        log.info("{} --> localUser:{} online success; clientCount:{}", Instant.now(), userModel.getUserName(), clientCount.size());
    }

    /**
     * 下线
     * @param channelHandlerContext
     * @param force 是否强制下线
     */
    void offline(ChannelHandlerContext channelHandlerContext, boolean force) {
        if (channelHandlerContext != null) {
            channelHandlerContext.writeAndFlush(Message.builder()
                    .messageHead(MessageHead.OFFLINE)
                    .define(force ? Define.userForceOffline : null)
                    .build());
            channelHandlerContext.close();
            Long userId = channelHandlerContextUserMap.get(channelHandlerContext);
            if (userId != null) {
                userChannelHandlerContextMap.remove(userId);
            }
            channelHandlerContextUserMap.remove(channelHandlerContext);
            clientCount.remove(channelHandlerContext);
        }
        log.info("{} --> offline; clientCount:{}", Instant.now(), clientCount.size());
    }

}
