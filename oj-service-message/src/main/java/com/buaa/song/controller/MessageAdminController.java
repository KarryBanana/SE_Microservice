package com.buaa.song.controller;

import com.buaa.song.dto.MessageDto;
import com.buaa.song.dto.MessageUpdateDto;
import com.buaa.song.result.Result;
import com.buaa.song.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import static com.buaa.song.utils.RequestUtil.getUserIdFromRequest;

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/admin/msg")
public class MessageAdminController {

    @Autowired
    private MessageService messageService;


    @PostMapping("/send")
    public Result createMessage(HttpServletRequest request, @RequestBody MessageDto messageDto) {
        return messageService.createMessage(getUserIdFromRequest(request), messageDto);
    }

    @PutMapping("/{id}")
    public Result updateMessage(HttpServletRequest request, @PathVariable("id") Integer msgId,
                                @RequestBody MessageUpdateDto updateMsgDto) {
        return messageService.updateMessage(getUserIdFromRequest(request), msgId, updateMsgDto);
    }

    @DeleteMapping("/{id}")
    public Result deleteMessage(@PathVariable("id") Integer msgId){
        return messageService.deleteMessage(msgId);
    }

    // 获取可发消息的班级（即管理的班级）
    @GetMapping("/classlist")
    public Result getSendClassList(HttpServletRequest request) {
        return messageService.getSendClassList(getUserIdFromRequest(request));
    }

    @GetMapping("/classmsg")
    public Result getAdminClassMessage(HttpServletRequest request) {
        return messageService.getAdminClassMessage(getUserIdFromRequest(request));
    }
}
