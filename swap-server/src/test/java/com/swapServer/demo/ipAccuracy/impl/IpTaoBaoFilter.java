package com.swapServer.demo.ipAccuracy.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.swapServer.demo.Utils;
import com.swapServer.demo.ipAccuracy.IpAccuracyChain;
import com.swapServer.demo.ipAccuracy.IpAccuracyFilter;
import com.swapServer.demo.ipAccuracy.Site;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * @Data :  2021/3/19 17:45
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
public class IpTaoBaoFilter implements IpAccuracyFilter {

    private String urlName = "http://ip.taobao.com/outGetIpInfo";

    @Override
    public String name() {
        return urlName;
    }

    @Override
    public void doFilter(String goalIp, IpAccuracyChain ipAccuracyChain) {

        Map param = new HashMap<String, String>();
        param.put("ip", goalIp);
        param.put("accessKey", "alibaba-inc");

        Document doc;
        try {
            doc = Jsoup.connect(urlName)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36")
                    .referrer("http://ip.taobao.com/ipSearch")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .data(param)
                    .get();

            Element child = doc.child(0).child(1).child(0);

            String text = child.text();

                    JsonObject json = Utils.jsonObject.getAsJsonObject(text);
            JsonObject data = json.getAsJsonObject("data");
            String county = data.get("county").getAsString();

//            Element child = ip_all.child(0).child(1);
            String country = county;
            String province = county;
            String city = county;

            Site build = Site.builder()
                    .country(country)
                    .province(province)
                    .city(city)
                    .build();

            ipAccuracyChain.setSite(build);

            log.info("网址: {} 获取 ip :{} 信息{}", name(), goalIp, build);
        }catch (Exception e) {
            log.info("网址: {} 获取 ip :{} 信息失败", name(), goalIp);
        }

        ipAccuracyChain.accuracy(goalIp);
    }
}
