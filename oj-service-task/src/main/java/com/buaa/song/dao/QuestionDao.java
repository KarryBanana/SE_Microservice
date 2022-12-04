package com.buaa.song.dao;

import com.buaa.song.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * @FileName: QuestionDao
 * @author: ProgrammerZhao
 * @Date: 2021/5/22
 * @Description:
 */

public interface QuestionDao extends JpaRepository<Question,Integer> {

    @Query(value = "select q.id,q.content,u.nickname,q.create_time,tp.`order`,q.reply " +
            "from question q left join user u on q.creator_id = u.id " +
            "left join task_problem tp on q.task_id = tp.task_id and q.problem_id = tp.problem_id " +
            "where q.task_id = :id " +
            "and q.reply is not null",nativeQuery = true)
    List<Map<String,Object>> getReplyed(@Param("id") Integer taskId);

    @Query(value = "select q.id,q.content,u.nickname,q.create_time,tp.`order` " +
            "from question q left join user u on q.creator_id = u.id " +
            "left join task_problem tp on q.task_id = tp.task_id and q.problem_id = tp.problem_id " +
            "where q.task_id = :id " +
            "and q.reply is null",nativeQuery = true)
    List<Map<String,Object>> getUnreply(@Param("id") Integer taskId);
}