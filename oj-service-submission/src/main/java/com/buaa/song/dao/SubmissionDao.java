package com.buaa.song.dao;

import com.buaa.song.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * @FileName: SubmissionDao
 * @author: ProgrammerZhao
 * @Date: 2021/5/21
 * @Description:
 */

public interface SubmissionDao extends JpaRepository<Submission,Integer>, JpaSpecificationExecutor<Submission> {
    // 普通用户只能看自己的提交记录
    @Query(value = "select s.id, u.nickname, s.result, s.score, lang.name lang_name, s.code_length, s.time_cost, s.memory_cost, " +
            "s.create_time create_time " +
            "from submission s, user u, language lang " +
            "where lang.id = s.language_id " +
            "and u.id = :uid and u.id = s.user_id " +
            "and s.problem_id = :pid " +
            "order by s.id DESC "
    ,nativeQuery = true)
    List<Map<String, Object>> getProblemSubList(@Param("uid") Integer userId, @Param("pid") Integer problemId);

    @Query(value = "select s.id, u.nickname, s.result, s.score, lang.name lang_name, s.code_length, s.time_cost, s.memory_cost, " +
            "s.create_time create_time " +
            "from submission s, user u, language lang " +
            "where lang.id = s.language_id " +
            "and u.id = s.user_id " +
            "and s.problem_id = :pid " +
            "order by s.id DESC "
    , nativeQuery = true)
    List<Map<String, Object>> getProblemAdminSubList(@Param("pid") Integer problemId);
}