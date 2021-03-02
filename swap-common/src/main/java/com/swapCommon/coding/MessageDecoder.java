package com.swapCommon.coding;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swapCommon.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Data :  2021/2/26 11:09
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
public class MessageDecoder extends ByteToMessageDecoder {

    private static final int HEAD_LENGTH = 4;
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {

        if (in.readableBytes() < HEAD_LENGTH) {  //这个HEAD_LENGTH是我们用于表示头长度的字节数。  由于Encoder中我们传的是一个int类型的值，所以这里HEAD_LENGTH的值为4.
            return;
        }

        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) {
            channelHandlerContext.close();
        }

        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }

        byte[] body = new byte[dataLength];
        in.readBytes(body);
        Message message = objectMapper.readValue(body, Message.class);
        out.add(message);
    }
}
