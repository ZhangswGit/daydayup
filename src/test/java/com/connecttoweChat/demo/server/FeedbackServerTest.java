package com.connecttoweChat.demo.server;

import com.connecttoweChat.demo.CommonTest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

@Slf4j
public class FeedbackServerTest extends CommonTest {

    @Test
    public void createFeedback() throws IOException {
        String filePath = FILE_BASH_PATH + "license.txt";

        FileBody fileBody = new FileBody(new File(filePath));

        String apiPath = "/user/v1/feedback/create";

        String url = SERVER_URL + apiPath;
        HttpPost httpPost = new HttpPost(url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpEntity build = MultipartEntityBuilder.create()
                    .addPart("files", fileBody)
                    .addTextBody("userId", "1", ContentType.TEXT_PLAIN.withCharset("utf-8"))
                    .addTextBody("title", "标题", ContentType.TEXT_PLAIN.withCharset("utf-8"))
                    .addTextBody("description", "内容", ContentType.TEXT_PLAIN.withCharset("utf-8"))
                    .build();
            httpPost.setEntity(build);
            httpPost.setHeader("Authorization", defaultToken());
            //不能添加 Content-Type 自动添加
//            httpPost.setHeader("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary9laGoSkgC32H6Kn1");

            HttpResponse uploadResponse = httpClient.execute(httpPost);
            StatusLine statusLine = uploadResponse.getStatusLine();

            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = uploadResponse.getEntity();
                log.info("返回结果为 " + EntityUtils.toString(entity));
            }
        } catch (Exception ex) {
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }
    }

    @Test
    public void updateFeedback() throws IOException {

        String apiPath = "/user/v1/feedback/1";

        String url = SERVER_URL + apiPath;
        HttpPut httpPut = new HttpPut(url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            httpPut.setHeader("Authorization", defaultToken());
            HttpResponse uploadResponse = httpClient.execute(httpPut);
            StatusLine statusLine = uploadResponse.getStatusLine();

            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = uploadResponse.getEntity();
                log.info("返回结果为 " + EntityUtils.toString(entity));
            }
        } catch (Exception ex) {
        } finally {
            if (httpPut != null) {
                httpPut.releaseConnection();
            }
        }
    }

    @Test
    public void findAllFeedback() throws IOException {
        String apiPath = "/user/v1/feedback";

        String url = SERVER_URL + apiPath;
        HttpGet httpGet = new HttpGet(url);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            httpGet.setHeader("Authorization", defaultToken());
            HttpResponse uploadResponse = httpClient.execute(httpGet);
            StatusLine statusLine = uploadResponse.getStatusLine();

            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = uploadResponse.getEntity();
                log.info("返回结果为 " + EntityUtils.toString(entity));
            }
        } catch (Exception e) {

        }
    }
}
