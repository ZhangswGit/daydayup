package bean;

import com.swapCommon.define.Define;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * @Data :  2021/2/26 10:57
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Data
public class Message {

    private byte messageHead;//交互标识

    private Define define;//交互状态

    private SwapUser localSwapUser;//当前用户

    private SwapUser goalSwapUser;//目标用户

    private Object body;//消息

    public Message() {}

    public Message(byte messageHead, Define define, SwapUser localSwapUser, SwapUser goalSwapUser, Object body) {
        this.messageHead = messageHead;
        this.define = define;
        this.localSwapUser = localSwapUser;
        this.goalSwapUser = goalSwapUser;
        this.body = body;
    }

    public static Message builder() {
        return new Message();
    }

    public Message messageHead(byte messageHead){
        this.messageHead = messageHead;
        return this;
    }

    public Message define(Define define){
        this.define = define;
        return this;
    }

    public Message localSwapUser(SwapUser localSwapUser){
        this.localSwapUser = localSwapUser;
        return this;
    }

    public Message goalSwapUser(SwapUser goalSwapUser){
        this.goalSwapUser = goalSwapUser;
        return this;
    }

    public Message body(Object body){
        this.body = body;
        return this;
    }

    public Message build() {
        return new Message(this.messageHead, this.define == null ? Define.normal : this.define, this.localSwapUser, this.goalSwapUser, this.body);
    }
}
