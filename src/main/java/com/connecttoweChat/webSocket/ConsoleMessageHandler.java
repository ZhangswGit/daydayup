package com.connecttoweChat.webSocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ConsoleMessageHandler extends TextWebSocketHandler {

    private static Map<String, WebSocketSession> sessionPool = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        if (sessionId == null) {
            log.error("UID is null, close the connection");
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }
        sessionPool.put(sessionId, session);
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

    public void sendMessage(String name, String longText) {
        Set<String> keys = sessionPool.keySet();
        keys.forEach(key -> {
            WebSocketSession session = sessionPool.get(key);
            TextMessage textMessage = new TextMessage(longText);
            try {
                session.sendMessage(textMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


}
