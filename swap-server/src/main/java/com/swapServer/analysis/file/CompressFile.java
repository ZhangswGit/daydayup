package com.swapServer.analysis.file;

import com.swapServer.analysis.Analyzed;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

/**
 * @Data :  2021/2/24 9:51
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Setter
@Getter
public class CompressFile extends File implements Analyzed {

    private String remote;

    private int count;//压缩文件个数

    public CompressFile(String pathname) {
        super(pathname);
    }

    @Override
    public String id() {
        return null;
    }

    @Override
    public String type() {
        return null;
    }
}
