package com.connecttoweChat.filter;

import com.connecttoweChat.Utils.SecurityUtils;
import com.connecttoweChat.config.jwtToken.TokenProvide;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class SecurityFilter extends OncePerRequestFilter {

    private final String AUTHORIZATION_HEADER = "Authorization";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    TokenProvide tokenProvide;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = tokenProvide.isToken(request);
        if (StringUtils.isNotEmpty(token)) {
            if (!tokenProvide.verificationToken(token)) {
                response.setStatus(SC_UNAUTHORIZED);
                response.setContentType(APPLICATION_JSON_VALUE);
                return;
            }
            String currentUser = SecurityUtils.currentUser();
            log.info(currentUser);
        }
        doFilter(request, response, filterChain);
    }

    public String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return "";
        }
        for (Cookie cookie : cookies) {
            if (StringUtils.equals(cookie.getName(), name)) {
                return cookie.getValue();
            }
        }
        return "";
    }
}
