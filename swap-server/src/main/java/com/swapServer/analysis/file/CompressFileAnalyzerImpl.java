package com.swapServer.analysis.file;

import com.swapServer.analysis.Analyzed;
import com.swapServer.analysis.Analyzer;
import com.swapServer.model.response.RealTimeResponse;
import com.swapServer.service.RealTimeService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

/**
 * @Data :  2021/2/24 9:53
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
@Service("CompressFileAnalyzerImpl")
public class CompressFileAnalyzerImpl implements Analyzer {

    @Autowired
    private RealTimeService realTimeService;

    @Override
    public void analyze(Analyzed analyzed) {
        zipFile(analyzed, 0);
    }

    @Override
    public boolean support(Analyzed analyzed) {
        return analyzed instanceof CompressFile;
    }

    public void zipFile(Analyzed analyzed, int count) {
        CompressFile file = (CompressFile) analyzed;
        try (ZipFile zipFile = new ZipFile(file)) {
            Enumeration<ZipArchiveEntry> zipFileEntries = zipFile.getEntries();
            while (zipFileEntries.hasMoreElements()) {
                ZipArchiveEntry zipArchiveEntry = zipFileEntries.nextElement();
                String detailName = zipArchiveEntry.getName();
                String user = file.getRemote();

                long startTime = System.currentTimeMillis();
                sendMessage(user, ParsingStatus.start, detailName, startTime, null);
                InputStream inputStream = zipFile.getInputStream(zipArchiveEntry);
                FileType fileType = handleDetailFile(detailName, inputStream);

                sendMessage(user, ParsingStatus.stop, detailName, startTime, fileType);

                count ++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendMessage(String user, ParsingStatus parsingStatus, String detailFileName, @NotNull long startTime, FileType fileType) {

        realTimeService.sendCompress(user, RealTimeResponse.builder()
                .body(FileDetail.builder()
                        .fileName(detailFileName)
                        .parsingStatus(parsingStatus)
                        .SpendTime(ParsingStatus.stop == parsingStatus ? System.currentTimeMillis() - startTime : 0)
                        .fileType(fileType)
                        .build())
                .build());
    }

    @Async
    FileType handleDetailFile(String detailName, InputStream inputStream) throws Exception{
        StringBuilder sb = new StringBuilder();
        byte[] b = new byte[4];
        try {
            inputStream.read(b, 0, b.length);
            if (b == null || b.length < 0){
                throw new Exception("handleDetailFile exception");
            }
            for (int i = 0; i < b.length; i++) {
                // 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式
                int v = b[i] & 0xFF;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    sb.append(0);
                }
                sb.append(hv);
            }
        } catch (IOException e) {
            throw new Exception("handleDetailFile exception");
        }
        log.info("file {} is fileHeader {}", detailName, sb.toString());
        return FileType.other;
    }

    @Data
    @Builder
    static class FileDetail {
        private String fileName;
        private ParsingStatus parsingStatus;
        private Long SpendTime;
        private FileType fileType;
    }

    enum ParsingStatus{
        start, processing, stop;
    }

    enum FileType{
        other, deceive;
    }
}
