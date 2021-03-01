package com.swapCommon.coding;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

/**
 * @Data :  2021/2/26 10:59
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
public class MessageEncoder extends MessageToByteEncoder<Message> {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        byte[] bytes = null;
        if (ObjectUtils.isNotEmpty(message.getBody())) {
            bytes = objectMapper.writeValueAsBytes(message);
        }
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
