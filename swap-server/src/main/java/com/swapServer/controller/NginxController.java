package com.swapServer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swapServer.analysis.AnalysisEngine;
import com.swapServer.analysis.file.CompressFile;
import com.swapServer.mapper.AccessLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller nginx.
 */
@Slf4j
@RestController
@RequestMapping("/nginx")
public class NginxController {

    @Resource(description = "CompressFileAnalysisEngine")
    private AnalysisEngine analysisEngine;

    private static final String PAC_TEMPLATE =
            "var proxyDomains = [\"jira.tistarlocal.com\",\"192.168.50.83\",\"192.168.50.82\",\"www.baidu.com\"];\r\n" + // ['outlook.office365.com']
                    "function FindProxyForURL(url, host) {\r\n" +
                    "  // Proxies only http(s) and ws(s)\r\n" +
                    "  if (url.substring(0, 4) != 'http' && (url.substring(0, 2) != 'ws')) {\r\n" +
                    "    return \"DIRECT\";\r\n" +
                    "  }\r\n" +
                    "  for (var d in proxyDomains) {\r\n" +
                    "    if (shExpMatch(host, proxyDomains[d])) {\r\n" +
                    "      return \"PROXY 192.168.50.160:8001\";\r\n" +
//        "      return \"PROXY %s; DIRECT\";\n" +
                    "    }\r\n" +
                    "  }\r\n" +
                    "  return \"DIRECT\";\r\n" +
                    "}\r\n";

    @Autowired
    private AccessLogMapper accessLogMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/redirect")
    public void targetDomain(HttpServletResponse response) throws IOException {
        response.sendRedirect("https://www.baidu.com");
    }

    @GetMapping()
    public ResponseEntity<String> targetDomain(
            @RequestParam(value = "domain", required = false) String domain,
            @RequestParam(value = "url", required = false) String url,
            @RequestParam(value = "token", required = false) String authToken,
            @RequestParam(value = "realIP", required = false) String realIP,
            @RequestParam(value = "method", required = false) String method,
            HttpServletRequest request) throws Exception {

        log.info("domain={}, url={}, token={}, realIP={}, method={}",
                domain, url, authToken, realIP, method);

        String remoteHost = request.getRemoteHost();
        String host = request.getHeader("Host");
        String test = request.getHeader("test");
        log.info("remoteHost:{}/host:{}/test:{}", remoteHost, host, test);

        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("target", "http://127.0.0.1:8082");

        String body = objectMapper.writeValueAsString(resultMap);

        return new ResponseEntity(body, HttpStatus.OK);
    }

    @GetMapping("/{tenantUuid:.+}")
    public ResponseEntity<String> getPac(
            @PathVariable("tenantUuid") String tenantUuid,
            HttpServletRequest request) {

        return ResponseEntity.ok(PAC_TEMPLATE);
    }

    @GetMapping("/{tenantUuid:.+}/pac")
    public ResponseEntity<Void> getPacFile(
            @PathVariable("tenantUuid") String tenantUuid,
            HttpServletRequest request, HttpServletResponse response) {

        response.setContentType("text/plain");
//        response.setContentType("application/octet-stream");
//        response.setHeader("Content-Disposition", "inline;filename=" + "\"pac.pac;\"");
        response.setHeader("Content-Disposition", "inline;filename=pac.pac");
        response.setHeader("Cache-Control", "");
        response.setHeader("X-Content-Type-Options", "");
        response.setHeader("X-Frame-Options", "");
        response.setHeader("X-XSS-Protection", "");

        try (
                OutputStream outputStream = response.getOutputStream();
                InputStream inputStream = new ByteArrayInputStream(PAC_TEMPLATE.getBytes());
        ) {
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/compress/{fileId}/{remote}")
    public ResponseEntity<Void> compressFile(@PathVariable("fileId") int fileId, @PathVariable("remote") String remote){
        String parentPath = "D:" + File.separator + "chrom" + File.separator + "zipFile" + File.separator;
        CompressFile compressFile = null;
        switch (fileId) {
            case 1:
                compressFile = new CompressFile(parentPath + "openresty-1.13.6.2-win64.zip");
                break;
            case 2:
                compressFile = new CompressFile(parentPath + "zipfile1.zip");
                break;
            case 3:
                compressFile = new CompressFile(parentPath + "UltraEdit-32.zip");
                break;
        }
        compressFile.setRemote(remote);
        analysisEngine.execute(compressFile);
        return ResponseEntity.ok().build();
    }
}
