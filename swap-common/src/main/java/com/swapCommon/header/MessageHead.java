package com.swapCommon.header;

/**
 * @Data :  2021/3/1 16:29
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
public class MessageHead {
    public static final byte HEART = 0X74;//心跳

    public static final byte AUTH = 0X75;//认证

    public static final byte AUTH_SUCCESS = 0X76;//认证成功

    public static final byte MUTUAL = 0X77;//交互

    public static final byte OFFLINE = 0X78;//下线
}
