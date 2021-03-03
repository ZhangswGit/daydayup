import bean.SwapUser;
import com.swapClient.clent.ChatClient;
import com.swapClient.window.LoginInterFace;
import com.swapClient.window.MainInterface;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @Data :  2021/3/1 18:03
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */

@Slf4j
public class Client1 {

    public static void main(String[] args) {

        MainInterface mainInterface = new MainInterface("聊天窗口");
        LoginInterFace loginInterFace = new LoginInterFace("登录界面");
        ChatClient chatClient1 = new ChatClient(mainInterface, loginInterFace);
        chatClient1.start();

    }
}
