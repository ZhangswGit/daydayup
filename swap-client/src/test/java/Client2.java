import com.swapClient.clent.ChatClient;
import com.swapCommon.Message;
import com.swapCommon.header.MessageHead;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @Data :  2021/3/1 18:03
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */

@Slf4j
public class Client2 {

    public static void main(String[] args) {
        try {
            long userId = 333888l;
            long goalUserId = 12138l;
            ChatClient chatClient1 = new ChatClient(userId);
            ChannelFuture channelFuture = chatClient1.getChannelFuture();

            while (true) {
                log.info("user :{}输入信息：", userId);
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String input = br.readLine();
                channelFuture.channel().writeAndFlush(Message.builder()
                        .messageHead(MessageHead.MUTUAL)
                        .localId(userId)
                        .goalId(goalUserId)
                        .body(input).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
