package com.swapServer.demo.server;

import com.swapServer.demo.CommonTest;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

/**
 * @Data : 2020/12/25
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
public class UserServerTest extends CommonTest {

    @Test
    public void createUser() {
        String apiPath = "/admin/v1/user";
        String url = SERVER_URL + apiPath;
        HttpPost httpPost = new HttpPost(url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpEntity requestEntity = EntityBuilder.create().build();
            httpPost.setEntity(requestEntity);
            httpPost.setHeader("Authorization", defaultToken());

            HttpResponse uploadResponse = httpClient.execute(httpPost);
            StatusLine statusLine = uploadResponse.getStatusLine();

            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity responseEntity = uploadResponse.getEntity();
                log.info("返回结果为 " + EntityUtils.toString(responseEntity));
            }
        } catch (Exception e) {

        }
    }

    @Test
    public void findAllUser() {
        String apiPath = "/admin/v1/user";
        String url = SERVER_URL + apiPath;
        HttpGet httpGet = new HttpGet(url);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpEntity requestEntity = EntityBuilder.create().build();
            httpGet.setHeader("Authorization", defaultToken());

            HttpResponse uploadResponse = httpClient.execute(httpGet);
            StatusLine statusLine = uploadResponse.getStatusLine();

            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity responseEntity = uploadResponse.getEntity();
                log.info("返回结果为 " + EntityUtils.toString(responseEntity));
            }
        } catch (Exception e) {

        }
    }

}
