package com.example.springbootgraduation.component;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/imserver/{username}")
@Component
public class WebSocketServer {
    public static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);


    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessionMap.put(username, session);
        log.info("有新用户加入，username={}, 当前在线人数为：{}", username, sessionMap.size());
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();
        result.set("users", array);
        for (Object key : sessionMap.keySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("username", key);
            array.add(jsonObject);
        }
        sendAllMessage(result.toString());
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        sessionMap.remove(username);
        log.info("有一连接关闭，移除username={}的用户session, 当前在线人数为：{}", username, sessionMap.size());
    }

    /**
     * 当接收到客户端发送的消息时，执行该方法
     * @param message 接收到的消息内容
     * @param session 当前会话的Session对象
     * @param username 路径参数中的用户名
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("username") String username) {
        log.info("服务端收到用户username={}的消息:{}", username, message);
        // 将接收到的消息解析为JSON对象
        JSONObject obj = JSONUtil.parseObj(message);
        // 获取目标用户名和消息内容
        String toUsername = obj.getStr("to");
        String text = obj.getStr("text");
        // 根据目标用户名获取对应的会话Session对象
        Session toSession = sessionMap.get(toUsername);

        // 如果找到了目标用户的会话Session对象
        if (toSession != null) {
            // 创建一个JSON对象，用于存储消息的发送者和内容
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("from", username);
            jsonObject.set("text", text);
            // 调用sendMessage方法将消息发送给目标用户
            sendMessage(jsonObject.toString(), toSession);
            log.info("发送给用户username={}，消息：{}", toUsername, jsonObject.toString());
        } else {
            log.info("发送失败，未找到用户username={}的session", toUsername);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 向指定会话发送消息的方法
     * @param message 消息内容
     * @param toSession 目标会话
     */
    private void sendMessage(String message, Session toSession) {
        try {
            log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
            toSession.getAsyncRemote().sendText(message);
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }

    private void sendAllMessage(String message) {
        try {
            for (Session session : sessionMap.values()) {
                log.info("服务端给客户端[{}]发送消息{}", session.getId(), message);
                session.getAsyncRemote().sendText(message);
            }
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }
}