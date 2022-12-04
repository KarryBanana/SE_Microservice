package com.buaa.song.dao;

import com.buaa.song.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * @FileName: QuestionDao
 * @Author: ProgrammerZhao
 * @Date: 2021/4/7
 * @Description:
 */
public interface QuestionDao extends JpaRepository<Question,Integer> {
    List<Question> findByExamId(Integer examId);

    @Query(value = "select q.id,q.content,q.create_time,ep.`order`,u.nickname creator,q.reply,u2.nickname reply_person " +
            "from question q left join exam_problem ep " +
            "on q.exam_id = ep.exam_id and q.problem_id = ep.problem_id " +
            "left join user u on q.creator_id = u.id " +
            "left join user u2 on q.reply_person = u2.id",nativeQuery = true)
    List<Map<String, Object>> getQuestionsByExamId(@Param("exam_id") Integer examId);
}
