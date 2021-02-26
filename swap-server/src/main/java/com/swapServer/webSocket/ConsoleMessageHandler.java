package com.swapServer.webSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swapServer.model.response.RealTimeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ConsoleMessageHandler extends TextWebSocketHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static Map<String, WebSocketSession> sessionPool = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        String remote = session.getRemoteAddress().getHostName();
        if (sessionId == null) {
            log.error("UID is null, close the connection");
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }
        log.info("sessionId:{},remote:{}", sessionId, remote);
        sessionPool.put(remote, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        if (sessionId != null) {
            sessionPool.remove(sessionId.toString());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.debug("Handle message:[{}]", message.getPayload());
        //PongMessage pongMessage = new PongMessage();
        session.sendMessage(message);
        String sessionId = session.getId();
        log.debug("Send heartbeat message to sessionId:[{}]", sessionId);
    }

    public static void sendMessage(String remote, RealTimeResponse realTimeResponse) {
        WebSocketSession session = sessionPool.get(remote);
        try {
            String longText = objectMapper.writeValueAsString(realTimeResponse);
            TextMessage textMessage = new TextMessage(longText);
            session.sendMessage(textMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
