package com.swapServer.model.response;

import lombok.Builder;
import lombok.Data;

/**
 * @Data :  2021/2/25 16:48
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Data
@Builder
public class RealTimeResponse<T> {
    T body;
}
