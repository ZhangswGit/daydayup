package com.swapServer.config;

import com.swapServer.webSocket.ConsoleMessageHandler;
import com.swapServer.webSocket.ConsoleMessageInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Slf4j
@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
    public static final String WS_PATH_CONSOLE_MESSAGE = "/ws/console/v1/messages";

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ConsoleMessageHandler(), WS_PATH_CONSOLE_MESSAGE)
                .setAllowedOrigins("*")
                .addInterceptors(new ConsoleMessageInterceptor());
    }
}
