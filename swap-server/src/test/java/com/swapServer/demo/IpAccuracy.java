package com.swapServer.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Data :  2021/3/16 9:26
 * @Author : zhangsw
 * @Descripe : 本地ip库准确率测试，由于网络抓取数据，元素获取代码不可作为参考
 */
@Slf4j
public class IpAccuracy {

    private static DbSearcher dbSearcher = null;

    private static DbConfig dbConfig = null;

    AtomicLong success = new AtomicLong(0l);//记录成功

    AtomicLong fail = new AtomicLong(0l);//记录失败

    static ExecutorService executorService;

    static {
        //加载本地ip资源库
        try {
            long start = System.currentTimeMillis();
            Resource resource = new ClassPathResource("config/resource/ip2region.db");
            dbConfig = new DbConfig();
            dbSearcher = new DbSearcher(dbConfig, resource.getFile().getPath());

            //创建线程池
                int corePoolSize = Math.max(Runtime.getRuntime().availableProcessors(), 2);
            executorService = new ThreadPoolExecutor(corePoolSize, corePoolSize * 2, 60L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(10240), new ThreadPoolExecutor.DiscardOldestPolicy());
            log.info("init IP DB spend time:{}, corePoolSize:{}", System.currentTimeMillis() - start, corePoolSize);
        } catch (Exception e) {
            log.error("Failed to load COUNTRY GeoIP db: {}", e.getMessage());
        }
    }

    /**
     * 测试本地IP库的准确率
     * 对比IP库: http://ip.bczs.net
     */
    @Test
    public void ipContrast() {
        long start = System.currentTimeMillis();
        String domain = "ip.bczs.net";
        Document doc;
        try {
            doc = Jsoup.connect(String.format("%s://%s%s", "http", domain, "/countrylist"))
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36")
                    .referrer("http://ip.bczs.net/")
                    .header("Upgrade-Insecure-Requests", "1")
                    .get();
            // 从document中获取title值
            String title = doc.title();
            log.info("title = {}", title);
            Element result = doc.getElementById("result");
            //所有二级页链接
//            Elements allElements = result.child(0).child(0).child(1).children();
//            for (Element element : allElements) {
//                openSecondary(domain, element);
//            }
            //获取中国的ip段
            Element element = result.child(0).child(0).child(1).child(1);
            openSecondary(domain, element);

        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("spend times {}, count :{}, success:{}, fail:{}, failureRate:{}", System.currentTimeMillis() - start, success.longValue() + fail.longValue(), success.longValue(), fail.longValue(), (float) fail.longValue() / (success.longValue() + fail.longValue()));
    }

    /**
     * 打开二级页
     *
     * @param domain
     * @param element
     */
    private void openSecondary(String domain, Element element) {
        try {
            String uri = element.child(3).child(0).attr("href").trim();
            log.info("current uri:{}", uri);
            Document doc2;
            doc2 = Jsoup.connect(String.format("%s://%s%s", "http", domain, uri))
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36")
                    .referrer("http://ip.bczs.net/")
                    .header("Upgrade-Insecure-Requests", "1")
                    .get();
            Element result2 = doc2.getElementById("result");
            Elements allElements2 = result2.child(0).child(0).child(1).children();
            //所有详情页链接
            for (Element e2 : allElements2) {
                long start = System.currentTimeMillis();
                Task task = createTask(domain, e2);
                try{
                    Future<Long> future = executorService.submit(task);
                    while (!future.isDone()) {
                    }
                    log.info("spend times : {}", future.get() - start);
                }catch (Exception e) {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * 创建一个分析进程
     * @param domain
     * @param element
     * @return
     */
    private Task createTask(String domain, Element element) {
        try {
            String startIp = element.child(0).child(0).attr("href").trim();
            String endIp = element.child(1).text();
            Document document;
            document = Jsoup.connect(String.format("%s://%s%s", "http", domain, startIp))
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36")
                    .referrer("http://ip.bczs.net/")
                    .header("Upgrade-Insecure-Requests", "1")
                    .get();
            Element child = document.getElementById("result").child(0).child(0);

            String country = child.child(2).text();
            String text = child.text();

            Task task = new Task(startIp.substring(1), endIp, country, text.substring(text.indexOf("参考数据：")));
            return task;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * 数据分析进程
     */
    class Task implements Callable<Long> {
        private long start;//开始ip

        private long end;//结束ip

        private String country;

        private String province;//省

        private String city;//市

//        private String text;

        public Task(String startIp, String endIp, String country, String text) throws UnknownHostException {

            this.country = country;
            String before = text.substring(text.indexOf("：") + 1, text.indexOf("IP数据："));
            String[] details1 = before.split(" ");

            String after = text.substring(text.indexOf("IP数据："));
            String detail = after.substring(after.indexOf(" ") + 1);
            String[] details = detail.split(" ");
            Iterator<String> iterator = Arrays.asList(details).iterator();
            if (iterator.hasNext()) {
                this.country = iterator.next();
            }
            if (iterator.hasNext()) {
                String next = iterator.next();
                if (StringUtils.equals(next, "中国")) {
                    if (!StringUtils.equals(details1[1], "中国")){
                        this.province = details1[1];
                    }
                } else {
                    this.province = next;
                }
            }
            if (iterator.hasNext()) {
                this.city = iterator.next();
            }

            String ip = text.substring(text.indexOf("：") + 1, text.indexOf(" "));
            String[] ips = ip.split("-");
            if (getIP(InetAddress.getByName(ips[0].trim())) > getIP(InetAddress.getByName(startIp))) {
                start = getIP(InetAddress.getByName(ips[0].trim()));
            } else {
                start = getIP(InetAddress.getByName(startIp));
            }
            if (getIP(InetAddress.getByName(ips[1].trim())) < getIP(InetAddress.getByName(endIp))) {
                end = getIP(InetAddress.getByName(ips[1].trim()));
            } else {
                end = getIP(InetAddress.getByName(endIp));
            }
        }

        @Override
        public Long call() throws IOException {

            log.info("start check startIp:{} --> endIp:{}", toIP(start), toIP(end));
            for (long ip = start; ip <= end; ip += 0xff) {
                String hostAddress = toIP(ip).getHostAddress();
                DataBlock dataBlock = dbSearcher.btreeSearch(hostAddress);
                String[] split = StringUtils.split(dataBlock.getRegion(), "|");
                if (!"中国".equals(split[0]) && !"中国".equals(country)){
                    //都不是中国，过滤
                } else if ("中国".equals(split[0]) && country.contains("中国")) {
                    //匹配省
                    if ((StringUtils.isBlank(province) || province.contains(split[2]))
                            && (StringUtils.isBlank(city) || city.contains(split[3]) ||  "0".equals(split[3]))
                    ) {
                        //省市都为空，市为空 匹配成功
                        success.getAndAdd(1);
                    } else {
                        fail.getAndAdd(1);
                        log.info("check currentIp :{} city fail [province:{} city:{}] -> [{}]", hostAddress, province, city, split);
                    }
                } else {
                    fail.getAndAdd(1);
                    log.info("check currentIp :{} county fail county:[{}] -> [{}]", hostAddress, country, split);
                }
//                if (country.contains(split[0]) || (!"0".equals(split[0]) && text.contains(split[0])) || (!"0".equals(split[3]) && text.contains(split[3])) || (!"0".equals(split[4]) && text.contains(split[4]))) {
//                    success.getAndAdd(1);
//                } else {
//                    fail.getAndAdd(1);
//                    log.info("check currentIp :{} fail {} -> {}", hostAddress, text, split);
//                }
            }
            return System.currentTimeMillis();
        }
    }

    public InetAddress toIP(long ip) throws UnknownHostException {
        byte[] b = new byte[4];
        int i = (int) ip;//低３２位
        b[0] = (byte) ((i >> 24) & 0x000000ff);
        b[1] = (byte) ((i >> 16) & 0x000000ff);
        b[2] = (byte) ((i >> 8) & 0x000000ff);
        b[3] = (byte) ((i >> 0) & 0x000000ff);
        return InetAddress.getByAddress(b);
    }

    public long getIP(InetAddress ip) {
        byte[] b = ip.getAddress();
        long l = b[0] << 24L & 0xff000000L |
                b[1] << 16L & 0xff0000L |
                b[2] << 8L & 0xff00L |
                b[3] << 0L & 0xffL;
        return l;
    }

    @Test
    public void floatTest() {
        log.info((float) 870048 / (float) (870048 + 10414882) + "");
    }
}
