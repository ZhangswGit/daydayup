package com.swapServer.analysis.file;

import lombok.Builder;
import lombok.Data;

/**
 * @Data :  2021/2/25 13:43
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Data
@Builder
public class DetectionResult {

    private String fileName;

    private int fileCount;

    private long duration;
}
