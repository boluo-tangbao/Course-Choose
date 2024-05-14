package com.example.demo.test;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.service.WebSocketServer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class WebSocketTest {

    //设置定时十秒一次
    @Scheduled(cron = "0/10 * * * * ?")
    @PostMapping("/send")
    public String sendMessage() throws Exception {
        Map<String,Object> map = new HashMap<>();

        // 获取当前日期和时间
        LocalDateTime nowDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateTimeFormatter.format(nowDateTime));
        map.put("server_time",dateTimeFormatter.format(nowDateTime));
        map.put("server_code","200");
        map.put("server_message","这是服务器推送到客户端的消息哦！！");
        JSONObject jsonObject =  new JSONObject(map);
        WebSocketServer.sendAllMessage(jsonObject.toString());
        return jsonObject.toString();
    }

}



