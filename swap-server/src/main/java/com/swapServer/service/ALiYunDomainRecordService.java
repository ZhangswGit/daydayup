package com.swapServer.service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.gson.Gson;
import com.swapServer.config.AliyunProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Service
public class ALiYunDomainRecordService {

    @Resource(name = "AliyunProperties")
    private AliyunProperties defaultProperties;

    private IAcsClient client;

    private Gson gson;

    @PostConstruct
    public void init() {
        IClientProfile profile = DefaultProfile.getProfile(
                defaultProperties.getRegionId(), defaultProperties.getAccessKeyId(), defaultProperties.getAccessKeySecret());
        client = new DefaultAcsClient(profile);
        gson = new Gson();
        log.info("create aliyun IClientProfile success! param:{},{},{}", defaultProperties.getRegionId(), defaultProperties.getAccessKeyId(), defaultProperties.getAccessKeySecret());
    }

    public String createDomainRecord(String platformDomain) {

        AddDomainRecordRequest request = new AddDomainRecordRequest();
        request.setRegionId(defaultProperties.getRegionId());
        request.setDomainName(defaultProperties.getDomainName());
        request.setRR(StringUtils.substring(platformDomain, platformDomain.lastIndexOf("/") + 1, platformDomain.indexOf(defaultProperties.getDomainName())));
        request.setType(defaultProperties.getType());
        request.setLine(defaultProperties.getLine());
        request.setValue(defaultProperties.getValue());

        try {
            AddDomainRecordResponse response = client.getAcsResponse(request);
            log.info("create aliyun dayomain Record success! {}", response);
            Map map = gson.fromJson(gson.toJson(response), Map.class);
            return (String) map.get("recordId");
        } catch (ServerException e) {
            log.info("create aliyun domain Record fail! {}", e.getMessage());
            e.printStackTrace();
            return null;
        } catch (ClientException e) {
            log.info("create aliyun domain Record fail! ErrCode:{}, ErrMsg:{}, RequestId:{}", e.getErrCode(), e.getErrMsg(), e.getRequestId());
            return null;
        }
    }

    public String deleteDomainRecord(String recordId) {
        DeleteDomainRecordRequest request = new DeleteDomainRecordRequest();
        request.setRegionId(defaultProperties.getRegionId());
        request.setRecordId(recordId);

        try {
            client.getAcsResponse(request);
            log.info("delete aliyun domain Record success! {}", recordId);
            return recordId;
        } catch (ServerException e) {
            log.info("delete aliyun domain Record {} fail!", recordId);
            e.printStackTrace();
            return null;
        } catch (ClientException e) {
            log.info("delete aliyun domain Record fail! ErrCode:{}, ErrMsg:{}, RequestId:{}", e.getErrCode(), e.getErrMsg(), e.getRequestId());
            return null;
        }
    }

    public String updateDomainRecord(String recordId, String platformDomain) {
        UpdateDomainRecordRequest request = new UpdateDomainRecordRequest();
        request.setRegionId(defaultProperties.getRegionId());
        request.setRecordId(recordId);
        request.setRR(StringUtils.substring(platformDomain, platformDomain.lastIndexOf("/") + 1, platformDomain.indexOf(defaultProperties.getDomainName())));

        try {
            UpdateDomainRecordResponse response = client.getAcsResponse(request);
            log.info("update aliyun dayomain Record success! {}", response);
            Map map = gson.fromJson(gson.toJson(response), Map.class);
            return (String) map.get("recordId");
        } catch (ServerException e) {
            log.info("update aliyun domain Record {} fail!", recordId);
            e.printStackTrace();
            return null;
        } catch (ClientException e) {
            log.info("delete aliyun domain Record fail! ErrCode:{}, ErrMsg:{}, RequestId:{}", e.getErrCode(), e.getErrMsg(), e.getRequestId());
            return null;
        }
    }
}
