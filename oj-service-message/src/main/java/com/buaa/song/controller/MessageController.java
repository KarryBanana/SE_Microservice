package com.buaa.song.controller;


import com.alibaba.fastjson.JSONObject;
import com.buaa.song.result.Result;
import com.buaa.song.service.MessageService;
import com.buaa.song.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/msg")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/list")
    public Result getMessageList(HttpServletRequest request) {
        return messageService.getMessageList(getUserIdFromRequest(request));
    }

    @PostMapping("/read")
    public Result setMessageStatus(HttpServletRequest request, @RequestBody JSONObject data) {
        Integer msgId = (Integer) data.get("msgId");
        return messageService.setMessageStatus(getUserIdFromRequest(request), msgId);
    }

    @GetMapping("/if-unread")
    public Result findUnreadMsgExist(HttpServletRequest request) {
        return messageService.findUnreadMsgExist(getUserIdFromRequest(request));
    }

    private static Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        return (Integer) JwtUtil.decode(token).get("id");
    }
}
