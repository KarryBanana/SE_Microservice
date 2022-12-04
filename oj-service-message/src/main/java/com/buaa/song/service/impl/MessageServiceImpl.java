package com.buaa.song.service.impl;

import com.buaa.song.dao.MessageDao;
import com.buaa.song.dto.MessageDto;
import com.buaa.song.dto.MessageUpdateDto;
import com.buaa.song.entity.Message;
import com.buaa.song.entity.User;
import com.buaa.song.exception.MessageNotFindException;
import com.buaa.song.exception.MessageTypeException;
import com.buaa.song.exception.ParseToDateException;
import com.buaa.song.exception.UserNotFindException;
import com.buaa.song.result.Result;
import com.buaa.song.service.MessageService;
import com.buaa.song.utils.RestUtil;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RefreshScope
public class MessageServiceImpl implements MessageService {
    // logger是记录日志吧
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    private static final String userServiceUrl = "http://oj-service-user";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MessageDao messageDao;


    @Override
    public Result getMessageList(Integer userId) {
        try {
            checkUserExist(userId);
            List<Map<String, Object>> msgList = messageDao.getUserMessage(userId);

            return Result.success(msgList);

        } catch (UserNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result setMessageStatus(Integer userId, Integer msgId) {
        try{
            checkUserExist(userId);
            checkMessageExist(msgId);
            Integer result = messageDao.setMessageRead(userId, msgId);
            if( !(result > 0) ) {
                return Result.fail(400, "设置消息已读出错");
            }
            return Result.success("消息已读");
        } catch (UserNotFindException | MessageNotFindException e) {
            logger.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(400, e.getMessage());
        }
    }

    @Override
    public Result findUnreadMsgExist(Integer userId) {
        try {
            checkUserExist(userId);

            Map<String, Object> res = messageDao.findUnreadMsg(userId);
            BigInteger unreadMsgNum = (BigInteger) res.get("num");

            return Result.success(unreadMsgNum.compareTo(BigInteger.valueOf(1)) == 0); // 和1相等返回0，小于1返回-1，因此与0比较

        } catch (UserNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result createMessage(Integer userId, MessageDto messageDto) {
        try {
            checkUserExist(userId);
            Message msg = messageDto.transferToMsg();  // 可能抛出消息类型异常、日期转换异常
            Message newMsg = messageDao.save(msg);

            Integer classId = newMsg.getClassId();
            Integer msgId = newMsg.getId();

            return insertClassMemberMessage(classId, msgId);

        } catch (UserNotFindException e) {
            logger.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(400, e.getMessage());
        } catch (MessageTypeException e) {
            logger.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(400, "消息类型错误");
        } catch (ParseToDateException e) {
            logger.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(400, "日期格式错误");
        }
    }


    // 为某班级所有成员添加对应的user-message表记录
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result insertClassMemberMessage(Integer classId, Integer msgId) {
        try {
            // 可能要加上check classId
            checkMessageExist(msgId);

            Integer result = messageDao.NotifyClassMember(classId, msgId);
            // 存储过程的返回值为0
            if(result != 0) {
                return Result.fail(400, "消息发送至班级成员失败!");
            }

            return Result.success("消息发布成功,已发送至班级成员");

        } catch (MessageNotFindException e) {
            logger.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(400, e.getMessage());
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateMessage(Integer userId, Integer msgId, MessageUpdateDto messageDto){
        try {
            checkUserExist(userId);
            Result result = checkMessageExist(msgId);

            Message msg = (Message) result.getData();

            messageDto.updateMsg(msg);
            messageDao.save(msg);

            return Result.success("消息修改成功");

        } catch (UserNotFindException | MessageNotFindException e) {
            logger.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(400, e.getMessage());
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteMessage(Integer msgId){
        try {
            checkMessageExist(msgId);
            messageDao.deleteById(msgId);
            Integer result = messageDao.deleteUserMessage(msgId);
            if(! (result > 0) ) {
                return Result.fail(400, "删除用户消息记录失败!");
            }

            return Result.success("删除消息成功");
        } catch (MessageNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, "消息ID" + msgId + "不存在");
        }
    }

    @Override
    public Result getSendClassList(Integer userId) {
        try {
            Result result = checkUserExist(userId);
            User user = (User) result.getData();

            List<Map<String, Object>> classList = null;

            if(user.getRole().equals(2)) {
                classList = messageDao.getAdminClass(userId);
            } else if(user.getRole().equals(3)) {
                classList = messageDao.getAllClass();
            } else {
                return Result.fail(400, "用户权限错误");
            }

            return Result.success(classList);
        } catch (UserNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, "用户"+userId+"不存在");
        }
    }

    @Override
    public Result getAdminClassMessage(Integer userId) {
        try {
            Result result = checkUserExist(userId);
            User user = (User) result.getData();

            List<Map<String, Object> > messageList = null;

            if(user.getRole().equals(2)) {
                messageList = messageDao.getAdminMsg(userId);
            } else if(user.getRole().equals(3)) {
                messageList = messageDao.getAllMsg();
            } else {
                return Result.fail(400, "用户权限错误");
            }

            return Result.success(messageList);
        } catch (UserNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, "用户"+userId+"不存在");
        }
    }

    private Result checkUserExist(Integer userId) throws UserNotFindException {
        Result result = RestUtil.get(restTemplate, userServiceUrl + "/user/" + userId, User.class);
        if (!result.getStatus().equals(200)) {
            throw new UserNotFindException(userId);
        }
        return result;
    }

    private Result checkMessageExist(Integer msgId) throws MessageNotFindException {
        Optional<Message> msg = messageDao.findById(msgId);
        if(msg.isPresent()) {
            return Result.success(msg.get());
        } else {
            throw new MessageNotFindException(msgId);
        }
    }
}
