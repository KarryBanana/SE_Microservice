package com.buaa.song.dao;

import com.buaa.song.entity.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * @FileName: TagDao
 * @Author: ProgrammerZhao
 * @Date: 2020/11/16
 * @Description:
 */
public interface TagDao extends PagingAndSortingRepository<Tag,Integer> {

    @Query(value = "select t.*  " +
            "from tag t,problem_tag pt  " +
            "where pt.problem_id = :pid and pt.tag_id = t.id",nativeQuery = true)
    List<Tag> findTagsByProblem(@Param("pid") Integer problemId);

    @Query(value = "select t.id  " +
            "from tag t,problem_tag pt  " +
            "where pt.problem_id = :pid and pt.tag_id = t.id",nativeQuery = true)
    List<Integer> findTagIdsByProblem(@Param("pid") Integer problemId);


    // 获取题目的标签Tags
    @Query(value = "select t.`name` tagName, t.count " +
            "from problem_tag pt, tag t " +
            "where t.id = pt.tag_id and pt.problem_id = :id " +
            "limit 1 "
            ,nativeQuery = true)
    Map<String, Object> findProblemTags(@Param("id") Integer problemId);

    Tag findByName(String name);
}
