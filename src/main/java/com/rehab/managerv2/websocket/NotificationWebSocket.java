package com.rehab.managerv2.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 实时站内信推送核心枢纽
 */
@Component
@Slf4j
@ServerEndpoint("/ws/notify/{userId}")
public class NotificationWebSocket {

    // 存储当前所有在线的用户连接
    private static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        sessionMap.put(userId, session);
        log.info("用户 [{}] 上线，当前在线人数: {}", userId, sessionMap.size());
    }

    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        sessionMap.remove(userId);
        log.info("用户 [{}] 下线，当前在线人数: {}", userId, sessionMap.size());
    }

    @OnMessage
    public void onMessage(String message, @PathParam("userId") String userId) {
        log.info("收到用户 [{}] 的消息: {}", userId, message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket 发生错误", error);
    }

    /**
     * 服务端主动向指定用户发送消息
     */
    public static void sendMessage(String userId, String message) {
        Session session = sessionMap.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                log.error("向用户 [{}] 发送消息失败", userId, e);
            }
        } else {
            log.info("用户 [{}] 不在线，消息已忽略（真实环境可存入数据库离线表）", userId);
        }
    }
    
    /**
     * 全局广播消息
     */
    public static void broadcast(String message) {
        for (Session session : sessionMap.values()) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (Exception e) {
                    log.error("广播消息失败", e);
                }
            }
        }
    }
}
