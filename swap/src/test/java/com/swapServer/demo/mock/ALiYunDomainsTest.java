package com.swapServer.demo.mock;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.swapServer.config.DefaultProperties;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @Data : 2020/12/25
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
public class ALiYunDomainsTest extends AbstractBeanTest {

    @Autowired
    private DefaultProperties defaultProperties;

    private static IClientProfile profile;
    private static IAcsClient client;
    private static Gson gson = new Gson();

    @BeforeEach
    void init(){
        profile = DefaultProfile.getProfile(defaultProperties.getRegionId(), defaultProperties.getAccessKeyId(), defaultProperties.getAccessKeySecret());
        client = new DefaultAcsClient(profile);
    }

    // 若报Can not find endpoint to access异常，请添加以下此行代码
    // DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Alidns", "alidns.aliyuncs.com");

    @Test
    public void DescribeDomainRecords() {
        DescribeDomainRecordsRequest request = new DescribeDomainRecordsRequest();
        request.setRegionId("cn-hangzhou");
        request.setDomainName("alinun.top");
        try {
            DescribeDomainRecordsResponse response = client.getAcsResponse(request);
            System.out.println(new Gson().toJson(response));
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
    }

    @Test
    public void AddDomainRecord(){
        AddDomainRecordRequest request = new AddDomainRecordRequest();
        request.setRegionId("cn-hangzhou");
        request.setDomainName("alinun.top");
        request.setRR("eic1");
        request.setType("A");
        request.setLine("default");
        request.setValue("121.89.200.214");

        try {
            AddDomainRecordResponse response = client.getAcsResponse(request);
            System.out.println(new Gson().toJson(response));
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
    }

    @Test
    public void deleteDomainRecord(){
        DeleteDomainRecordRequest request = new DeleteDomainRecordRequest();
        request.setRegionId("cn-hangzhou");
        request.setRecordId("20996932736742400");

        try {
            DeleteDomainRecordResponse response = client.getAcsResponse(request);
            System.out.println(new Gson().toJson(response));
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }
    }

    @Test
    public void test(){
        String request = "{\"requestId\":\"1CA85CE5-7386-4AA0-B7F9-B2D6056C11C2\",\"recordId\":\"20995828658107392\"}";
        Map map = gson.fromJson(request, Map.class);
        log.info((String) map.get("recordId"));
    }

}
