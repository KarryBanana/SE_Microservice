package com.buaa.song.dao;

import com.buaa.song.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


public interface MessageDao extends JpaRepository<Message, Integer> {

    @Transactional
    @Modifying
    @Query(value = "update user_message um " +
            "set um.read = 1 " +
            "where um.user_id = :user_id and um.message_id = :msg_id",
    nativeQuery = true)
    Integer setMessageRead(@Param("user_id") Integer userId, @Param("msg_id") Integer msgId);

    @Query(value = "select ifnull( " +
            "(select 1 from user_message um where um.user_id = :user_id and um.`read` = 0 limit 1) , 0) num "
    , nativeQuery = true)
    Map<String, Object> findUnreadMsg(@Param("user_id") Integer userId);

    @Transactional
    @Modifying
    @Query(value = "DELETE from user_message where message_id = :msg_id ",
    nativeQuery = true)
    Integer deleteUserMessage(@Param("msg_id") Integer msgId);

    @Query(value = "select m.id, c.name, m.title, m.content, m.timing `time`, um.`read` " +
            "from user_message um, message m, class c " +
            "where um.user_id = :user_id and um.message_id = m.id " +
            "and m.timing <= now() " +
            "and m.class_id != 0 and m.class_id =  c.id " +
            "order by um.read ASC, c.id DESC, m.timing DESC ",
    nativeQuery = true)
    List<Map<String, Object>> getUserMessage(@Param("user_id") Integer userId);


    @Transactional
    @Modifying
    @Query(value = "call NotifyClassMember(:classId, :msgId) ",
    nativeQuery = true)
    Integer NotifyClassMember(@Param("classId") Integer classId, @Param("msgId") Integer msgId);


    @Query(value = "select c.id, c.name, u.realname creator, count(m.user_id) stuNum " +
            "from class c, membership m, user u " +
            "where m.user_id = :user_id and m.type = 'admin' " +
            "and m.class_id = c.id and c.creator = u.id " +
            "group by m.class_id"
    ,nativeQuery = true)
    List<Map<String, Object>> getAdminClass(@Param("user_id") Integer userId);

    @Query(value = "select c.id, c.name, u.realname creator, count(m.user_id) stuNum " +
            "from class c, user u, membership m " +
            "where c.creator = u.id and c.id = m.class_id " +
            "group by m.class_id "
    ,nativeQuery = true)
    List<Map<String, Object>> getAllClass();


    @Query(value = "select m.id, c.`name`, m.title, m.content, m.timing as `time` " +
            "from membership ms, message m, class c " +
            "where ms.user_id = :user_id and ms.type = 'admin' and ms.class_id = m.class_id " +
            "and m.class_id = c.id " +
            "order by c.id DESC, time DESC"
    , nativeQuery = true)
    List<Map<String, Object> > getAdminMsg(@Param("user_id") Integer userId);

    @Query(value = "select m.id, c.`name`, m.title, m.content, m.timing as `time` " +
            "from message m, class c " +
            "where m.class_id = c.id " +
            "order by c.id DESC, time DESC "
    , nativeQuery = true)
    List<Map<String ,Object> > getAllMsg();
}
