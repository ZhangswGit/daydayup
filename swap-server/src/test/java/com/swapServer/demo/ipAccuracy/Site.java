package com.swapServer.demo.ipAccuracy;

import lombok.Builder;
import lombok.Data;

/**
 * @Data :  2021/3/19 18:00
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Data
@Builder
public class Site {

    private String country;

    private String province;

    private String city;
}
