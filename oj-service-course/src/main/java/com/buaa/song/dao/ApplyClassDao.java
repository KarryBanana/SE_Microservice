package com.buaa.song.dao;

import com.buaa.song.entity.ApplyClass;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * @FileName: ApplyClassDao
 * @Author: ProgrammerZhao
 * @Date: 2020/11/14
 * @Description:
 */
public interface ApplyClassDao extends PagingAndSortingRepository<ApplyClass,Integer> {

    // 身份认证部分之后完成
    @Query(value = "select a.id,u.id as uid,a.info,u.username,u.realname as realName, u.student_id as studentId " +
            "from apply_class a, `user` u " +
            "where a.user_id = u.id " +
            "and a.class_id = :cid and a.is_agree = 0 ",nativeQuery = true)
    List<Map<String,Object>> findAllUser(@Param("cid") Integer classId);
}
