package com.buaa.song.dao;

import com.buaa.song.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * @FileName: IssueDao
 * @Author: ProgrammerZhao
 * @Date: 2021/4/7
 * @Description:
 */
public interface IssueDao extends JpaRepository<Issue,Integer> {
    List<Issue> findByExamId(Integer examId);

    @Query(value = "select i.id,i.title,i.content,u.nickname,ep.`order`,i.create_time " +
            "from issue i left join user u on i.creator_id = u.id " +
            "left join exam_problem ep on i.exam_id = ep.exam_id and i.problem_id = ep.problem_id " +
            "where i.exam_id = :exam_id",nativeQuery = true)
    List<Map<String,Object>> getIssuesByExamId(@Param(value = "exam_id")Integer examId);
}
