package com.swapCommon.coding;

import io.netty.channel.ChannelId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.channels.Channel;

/**
 * @Data :  2021/2/26 10:57
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private int messageHead;

    private Long localId;

    private Long goalId;

    private Object body;

}
