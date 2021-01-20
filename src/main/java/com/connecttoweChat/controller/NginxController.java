package com.connecttoweChat.controller;

import com.connecttoweChat.mapper.AccessLogMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller nginx.
 */
@Slf4j
@RestController
@RequestMapping
public class NginxController {

    @Autowired
    private AccessLogMapper accessLogMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/")
    public void targetDomain(HttpServletResponse response) throws IOException {
        response.sendRedirect("https://www.baidu.com");
    }

    @GetMapping("/nginx")
    public ResponseEntity<String> targetDomain(
            @RequestParam(value = "domain") String domain,
            @RequestParam(value = "url") String url,
            @RequestParam(value = "token", required = false) String authToken,
            @RequestParam(value = "realIP") String realIP,
            @RequestParam(value = "method") String method,
            HttpServletRequest request) throws Exception {

        log.info("domain={}, url={}, token={}, realIP={}, method={}",
                domain, url, authToken, realIP, method);

        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("target", "http://127.0.0.1:8082");

        String body = objectMapper.writeValueAsString(resultMap);

        return new ResponseEntity(body, HttpStatus.OK);
    }
}
