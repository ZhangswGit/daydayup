package com.swapServer.analysis.image;

import com.swapServer.analysis.Analyzed;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Data :  2021/3/17 11:39
 * @Author : zhangsw
 * @Descripe : 车辆照片详情
 * @Version : 0.1
 */
@Data
@Builder
public class VehiclePhoto implements Analyzed {

    private MultipartFile multipartFile;

    @Override
    public String id() {
        return null;
    }
}
