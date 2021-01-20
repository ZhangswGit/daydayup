package com.connecttoweChat.Utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.http.HttpHeaders;

/**
 * @Data : 2020/12/25
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
public class HeaderUtils {
    public static <T> HttpHeaders generatePaginationHttpHeaders(IPage<T> page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", Long.toString(page.getTotal()));
        headers.add("X-Current-Page", Long.toString(page.getPages()));
        headers.add("X-Current-Size", Long.toString(page.getSize()));
        return headers;
    }
}
