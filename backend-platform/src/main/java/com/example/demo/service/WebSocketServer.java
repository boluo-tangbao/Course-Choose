package com.example.demo.service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;


// 使用 @ServerEndpoint 注解表示此类是一个 WebSocket 端点
// 通过 value 注解，指定 websocket 的路径
@ServerEndpoint(value = "/websocket/{userId}")
@Service
public class WebSocketServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketServer.class);

    /**
     * 记录目前在线人数。
     */
    private static AtomicInteger onlineCount = new AtomicInteger(0);
    //private static int onlineCount = 0;

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, List<String>> roomMessages = new ConcurrentHashMap<>();

    private Session session;

    private String userId=" ";

    // 收到消息
    @OnMessage
    public void onMessage(String message) throws IOException{

        LOGGER.info("[websocket] 收到消息：id={}，message={}", this.session.getId(), message);

        if (message.equalsIgnoreCase("bye")) {
            // 由服务器主动关闭连接。状态码为 NORMAL_CLOSURE（正常关闭）。
            this.session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Bye"));;
            return;
        }


        this.session.getAsyncRemote().sendText("["+ Instant.now().toEpochMilli() +"] Hello " + message);
    }

    // 连接打开
    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig, @PathParam("userId") String userId){
        // 保存 session 到对象
        // 这里添加了一个 userId 的参数，用来区分不同的连接；如果不添加会默认为同一个连接
        this.session = session;
        this.userId = userId;
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            webSocketMap.put(userId, this);
            //加入set中
        } else {
            webSocketMap.put(userId, this);
            //加入set中
            addOnlineCount();
            //在线数加1
        }
        LOGGER.info("用户登录:" + userId + ",当前在线人数为:" + getOnlineCount());
        LOGGER.info("[websocket] 新的连接：id={}", this.session.getId());
        LOGGER.info("当前的登录用户："+webSocketMap.keySet());
        try {
            sendMessage("连接成功");
        } catch (IOException e) {
            LOGGER.error("用户:" + userId + ",网络异常!!!!!!");
        }
    }

    // 连接关闭
    @OnClose
    public void onClose(CloseReason closeReason){
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            //从set中删除
            subOnlineCount();
        }
        LOGGER.info("用户退出:" + userId + ",当前在线人数为:" + getOnlineCount());
        LOGGER.info("[websocket] 连接断开：id={}，reason={}", this.session.getId(),closeReason);
        LOGGER.info("当前的登录用户："+webSocketMap.keySet());
    }

    // 连接异常
    @OnError
    public void onError(Throwable throwable) throws IOException {

        LOGGER.info("[websocket] 连接异常：id={}，throwable={}", this.session.getId(), throwable.getMessage());

        // 关闭连接。状态码为 UNEXPECTED_CONDITION（意料之外的异常）
        this.session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, throwable.getMessage()));
    }


    /**
     * 向指定用户发送消息
     */
    public static void sendMessageToUser(String userId, String message) throws IOException {
        if (!StringUtils.isEmpty(message) && webSocketMap.containsKey(userId)) {
            WebSocketServer webSocketServer = webSocketMap.get(userId);
            webSocketServer.sendMessage(message);
        } else {
            LOGGER.error("用户" + userId + "不在线！");
        }
    }

    // 向指定房间号的所有用户发送消息
    public static void sendMessageToRoom(String roomId, String message) throws IOException {
        List<String> messages = roomMessages.getOrDefault(roomId, new ArrayList<>());
        messages.add(message);
        roomMessages.put(roomId, messages);

        for (WebSocketServer webSocketServer : webSocketMap.values()) {
            if (webSocketServer.userId.startsWith(roomId)) {
                webSocketServer.sendMessage(message);
            }
        }
    }

    // 获取指定房间号的消息列表
    public static List<String> getRoomMessages(String roomId) {
        return roomMessages.getOrDefault(roomId, new ArrayList<>());
    }
    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 实现服务器主动推送
     */
    public static void sendAllMessage(String message) throws IOException {
        ConcurrentHashMap.KeySetView<String, WebSocketServer> userIds = (ConcurrentHashMap.KeySetView<String, WebSocketServer>) webSocketMap.keySet();
        for (String userId : userIds) {
            WebSocketServer webSocketServer = webSocketMap.get(userId);
            webSocketServer.session.getBasicRemote().sendText(message);
            System.out.println("webSocket实现服务器主动推送成功userIds====" + userIds);
        }
    }
    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, @PathParam("userId") String userId) throws IOException {
        LOGGER.info("发送消息到:" + userId + "，报文:" + message);
        if (!StringUtils.isEmpty(message) && webSocketMap.containsKey(userId)) {
            webSocketMap.get(userId).sendMessage(message);
        } else {
            LOGGER.error("用户" + userId + ",不在线！");
        }
    }

    public static Set<String> getOnlineUsers() {
        System.out.println(webSocketMap.keySet());
        return webSocketMap.keySet();
    }


    public static synchronized int getOnlineCount() {
        return onlineCount.get();
    }

    public static synchronized void addOnlineCount() {
        onlineCount.incrementAndGet();
    }

    public static synchronized void subOnlineCount() {
        onlineCount.decrementAndGet();
    }
}


