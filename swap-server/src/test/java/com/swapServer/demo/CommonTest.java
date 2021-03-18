package com.swapServer.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swapServer.analysis.Analyzed;
import com.swapServer.analysis.file.CompressFile;
import com.swapServer.analysis.file.CompressFileAnalyzerImpl;
import com.swapServer.constants.AuthConstant;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbMakerConfigException;
import org.lionsoul.ip2region.DbSearcher;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
public class CommonTest {
    protected static final String FILE_BASH_PATH = "D:" + File.separator + "chrom" + File.separator;

    protected static final ObjectMapper objectMapper = new ObjectMapper();

    protected final String AUTHORIZATION_HEADER = "Authorization";

    public static final String SERVER_URL = "http://192.168.50.121:8082";

    private static String PRIVATE_KEY =
            "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCtpaRsXJJZ5maF\n" +
                    "GCr2zIwcSqbYagRhtAa7xCKhqgaF3SuB9UhCT7GML32UwGrS1ZhukuIpJ3idIQPe\n" +
                    "vMdFlgyoJj4a4ORg4KEQLGeLPYgWjzEdKyuntwnTBU8wwYHDhzmcns7TnOtONEZM\n" +
                    "BJ14R8ZWk8uLKbJZ3KIOOYWcFaYzEQ4TPGTm4iE61zNFM+wOWLL7l9C7od95xtnH\n" +
                    "FsHShmr6yjC5eZq53xkUUp+hI6FjrHL1BZRro1TVr5TePgWvV7Z5RIJ6DVmwv25I\n" +
                    "NolaZ23BNw+0j3H4mKZFEFrni+Cvw3JYXBk0gh6YgpTAi3nMzgk2Er6ke/4hCuRU\n" +
                    "Ck3aD08FAgEDAoIBAHPDwvLoYZFERFi6x08zCBLcbzrxWEEiryfYFxZxWa6Tclaj\n" +
                    "hYGKdl10/mMq8eHjuvRh7Btvpb4WApR92i5kCHAZfryV7ZXrFgrIRQd+Wrm0y2jH\n" +
                    "cm/PW+IDiiCBAS0E0RMUieJonN7NhDKtvlAv2Y8NMlzGduaTFrQmWRK5GXdfmpoX\n" +
                    "PA+C9rjTTnnVq7D56zZuFimKs8v7tGwseaTEXPCfE3SIQH0VVZUxKPSJCHBwel5Q\n" +
                    "NcIkoRHou9NJ9G3hsVVeBCkpaHYaaw5mVtIaomGYgLR/VFymziPFuc1wLPTLMY6s\n" +
                    "/8Tv40IgIx4czU81kFDy+ppIZYviMe3MrvgHv3MCgYEA3UUkpQtJgE+Wx08z9DN1\n" +
                    "Q36jblOQgtflIQJ1rP2ni9JDod84n5BlAr+Cl/QSChvFjGwAoOISNEAhv2I+GnzO\n" +
                    "M3zGJ3xYbtKgDKjzoOh+RK0h/4pQDpnLHdRRor/ewaSStOG+ORF3iwWR0Tc6e0Kf\n" +
                    "OxP8dayX4kxyZlp086leOWMCgYEAyOb05cRULtYDdj8/dlFsjqtPQSnBTu/oKiJe\n" +
                    "Xl24U7+X8at0mm3aFfUGSj5ECp8+LvuMouZaLvqwu78Q/I22+PxxVL/2ziz3wQmp\n" +
                    "ZjbBNYGwTxuAZNPTUzsWIXOE25v/8yCaYHuivq/AjtwlypNedaip0RB3g4LFMxTi\n" +
                    "U7yldncCgYEAk4Ntw1zbqt+52jTNTXejglRs9De1rI/uFgGjyKkaXTbXwT97FQru\n" +
                    "AdUBuqK2sWfZCEgAa0FheCrBKkF+vFM0IlMuxP2QSeHACHCia0Wpgx4Wqlw1XxEy\n" +
                    "E+LhFyqUgRhhzevUJgulB1kL4M98UixqJ2KoTnMP7DL27ub4onDpe5cCgYEAhe9N\n" +
                    "7oLiyeQCTtTU+YudtHI01hvWNJ/wHBbplD564n+6oRz4ZvPmuU4EMX7YBxTUH1Jd\n" +
                    "we7myfx10n9gqF55+1L2OH/53silK1vGRCSAzlZ1ihJVmI03jNIOwPet571VTMBm\n" +
                    "6v0XKcqAXz1uhwzpo8XGi2BPrQHYzLiW4n3DpE8CgYEAsU+2SnUj1+qu6mgvK5E6\n" +
                    "Prem0yz5Yi/VAxOBOfyrb7UIuN6WPkO1zUSgDYBHkxmRuluw7PwohzfhqgFVSddp\n" +
                    "Tnpv6Ky0Y58eeFx9ECeMsZqfYgvz/RZpIsoTphvujKpXe7Zv27IMIxIfTckGRMt4\n" +
                    "uj6l18eUdGFWqKRR+99zhsE=";

    public String defaultToken() {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(PRIVATE_KEY);
        Key key = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
        String authorities = Arrays.asList(new GrantedAuthority[]{new SimpleGrantedAuthority(AuthConstant.ADMIN)}).stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        Date nowDate = new Date();
        long now = nowDate.getTime();
        Date validity = new Date(now + 300 * 1000l);
        return Jwts.builder()
                .setSubject("sysAdmin")
                .claim("AUTH", authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setIssuedAt(nowDate)
                .setExpiration(validity)
                .compact();
    }

    @Test
    void currentTimeMillis() {
        log.info(System.currentTimeMillis() + "");
    }

    @Test
    void testInstant() {
        Instant now = Instant.now();
        log.info(now + "");
        Instant instant = Instant.now().minus(5, ChronoUnit.MINUTES);
        log.info(instant + "");
    }

    @Test
    void sortTest() {
        List<Integer> list = Arrays.asList(1, 3, 89, 6, 7, 65, 4, 2);
        list.stream().sorted(((o1, o2) -> {
            if (o1 < o2) {
                return -1;
            } else return 1;
        })).forEach(System.out::println);
    }

    @Test
    void notNull() {
        List<Integer> list = new ArrayList<>();
        log.info(ObjectUtils.isEmpty(list) + "");
    }

    @Test
    void sortMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("1", "1");
        map.put("2", "2");
        map.forEach((k, v) -> {
            log.info(k + " - " + v);
        });
    }

    @Test
    void sub() {
        String platformDomain = "https://11a12fdb-8717-4821-b687-56632587dfa7-email-isolation.tistarsec.com";
        String substring = StringUtils.substring(platformDomain, platformDomain.lastIndexOf("/") + 1, platformDomain.indexOf(".tistarsec.com"));
        log.info(substring);
        AsyncResult<String> ars = new AsyncResult<>(substring);
    }

    @Test
    void testLoad() {
        try {
            Resource resource = new ClassPathResource("config/resource/auto-config-url.txt");
            InputStream is = resource.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder("");
            String data = "";
            while ((data = br.readLine()) != null) {
                System.out.println(data);
                sb.append(data);
            }
            String.format(sb.toString(), "http://127.0.0.1");
            br.close();
            isr.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoadIso() {
        try {
            URL url = this.getClass().getClassLoader().getResource("config/resource/pac-app.app");
            File file = new File(url.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void builder(String[] args) {
        String a = null;
        switch (a){

        }
        A zs = A.builder().name("zs").age(20).build();
        log.info(zs.toString());
        String ss = " aaaa\"%s\"";
        log.info(String.format(ss, 123));
        log.info(ss);
    }

    public static String sleepTest() {
        try {
            Thread.sleep(10000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "ABC";
    }

    static class A {
        private String name;
        private int age;

        public A() {
        }

        public A(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public A build() {
            return new A(this.name, this.age);
        }

        public static A builder() {
            return new A();
        }

        public A name(String name) {
            this.name = name;
            return this;
        }

        public A age(int age) {
            this.age = age;
            return this;
        }
    }

    @Test
    public void parse() {
        Instant nowTime = Instant.parse("2020-12-31T12:30:30Z");
        log.info(nowTime + "");
    }

    @Test
    public void urlTest() {
        String host = "@qq";
        String domain = String.format("http://%s", host);
        try {
            new URL(domain);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ryhFileCopy() {
        String filePath = "D:" + File.separator + "chrom" + File.separator + "ryh" + File.separator + "cflr_load_perform_ryh_09_03-golden.csv";
        File file = new File(filePath);

        for (int i = 4; i < 304; i++) {
            try (InputStream inputStream = new FileInputStream(file);) {

                String outFilePath = "D:" + File.separator + "chrom" + File.separator + "ryh" + File.separator + "cflr_load_perform_ryh_09_" + (i < 10 ? ("0" + i) : i) + "-golden.csv";
                File outFile = new File(outFilePath);
                outFile.createNewFile();
                try (OutputStream outputStream = new FileOutputStream(outFile)) {
                    IOUtils.copy(inputStream, outputStream);
                }
            } catch (Exception e) {

            }
        }

        log.info("完成！");
    }

    @Test
    public void analyse(){
        Analyzed compressFile = new CompressFile(FILE_BASH_PATH + "zipFile.zip");
        CompressFileAnalyzerImpl compressFileAnalyzer = new CompressFileAnalyzerImpl();
        compressFileAnalyzer.analyze(compressFile);
    }

    @Test
    public void mavenTest() {
    }

    @Test
    public void binaryTest() {
        String c = "sad";
        char[] chars = c.toCharArray();
        for (int i = 0; i < chars.length ; i++) {
            Integer.toBinaryString(chars[1]);
        }
    }

    @Test
    public void openCV() {
        String fileName = "D:" + File.separator + "chrom" + File.separator + "1.jpg";
        Mat src = Imgcodecs.imread(fileName);

        Mat gold = src.clone();

        Imgcodecs.imwrite(String.format("D:" + File.separator + "chrom" + File.separator + "tmp"+ File.separator + "%d.jpg", System.currentTimeMillis()), gold);
    }

    @Test
    public void javaPath(){
        System.out.println(System.getProperty("java.library.path"));
    }
}
