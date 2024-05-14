package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.ChatRoom;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.WebSocketServer;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/chatRoom")
public class ChatRoomController {

    // 假设聊天室数据存储在内存中的对象中
    private ConcurrentHashMap<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();


    @Resource
    private WebSocketServer webSocketServer;

    @PostMapping("/createChatRoom")
    public Result<?> createChatRoom(@RequestBody Map<String, String> requestBody) {
        String roomId = requestBody.get("roomId");

        // 检查聊天室是否已存在
        if (chatRooms.containsKey(roomId)) {
            return Result.error("聊天室已存在");
        }

        // 创建新的聊天室对象
        ChatRoom chatRoom = new ChatRoom(roomId, new ArrayList<>());
        chatRooms.put(roomId, chatRoom);

        // 截取出两个用户的ID
        String[] userIds = roomId.split("-");
        // 给两个用户发送消息
        try {
            webSocketServer.sendInfo("您已加入聊天室：" + roomId, userIds[0]);
            webSocketServer.sendInfo("您已加入聊天室：" + roomId, userIds[1]);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("发送消息失败");
        }

        // 构造返回的结果数据
        Map<String, Object> res = new HashMap<>();
        res.put("roomId", roomId); // 可根据需要添加更多数据

        // 返回成功结果，携带数据
        return Result.success(res);
    }

    @DeleteMapping("/{roomId}")
    public Result<?> deleteChatRoom(@PathVariable String roomId) {
        if (!chatRooms.containsKey(roomId)) {
            return Result.success("聊天室不存在，无需删除");
        }
        // 从 chatRooms 中移除对应的 roomId
        chatRooms.remove(roomId);
        return Result.success("聊天室删除成功");
    }

    @PostMapping("/sendMessage/{roomId}")
    public Result<?> sendMessage(@PathVariable String roomId, @RequestBody Map<String, String> requestBody) {
        String message = requestBody.get("message");

        try {
            // 调用 WebSocketServer 中的方法实现消息的处理和转发
            webSocketServer.sendMessageToRoom(roomId, message);

            // 如果需要返回一些数据给前端，可以构造一个响应对象返回
            ConcurrentHashMap<String, Object> responseData = new ConcurrentHashMap<>();
            responseData.put("roomId", roomId);
            responseData.put("message", message);
            responseData.put("messages", WebSocketServer.getRoomMessages(roomId));

            return Result.success(responseData);
        } catch (IOException e) {
            e.printStackTrace();
            // 处理消息发送失败的情况，可以返回相应的错误信息给前端
            return Result.error("消息发送失败，请稍后重试");
        }
    }


}
