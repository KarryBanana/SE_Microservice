package com.buaa.song.service;


import com.buaa.song.dto.MessageDto;
import com.buaa.song.dto.MessageUpdateDto;
import com.buaa.song.result.Result;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {

    Result getMessageList(Integer userId);

    Result setMessageStatus(Integer userId, Integer msgId);

    Result findUnreadMsgExist(Integer userId);

    Result createMessage(Integer userId, MessageDto messageDto);

    Result insertClassMemberMessage(Integer classId, Integer msgId);

    Result updateMessage(Integer userId, Integer msgId, MessageUpdateDto messageDto);

    Result deleteMessage(Integer msgId);

    Result getSendClassList(Integer userId);

    Result getAdminClassMessage(Integer userId);
}
