package com.swapCommon;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private byte messageHead;

    private Long localId;

    private Long goalId;

    private Object body;
}
