package com.buaa.song.dao;

import com.buaa.song.entity.ClassProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClassProblemDao extends JpaRepository<ClassProblem, Integer> {

    @Query(value = "select * " +
            "from class_problem cp " +
            "where cp.class_id = :cid " +
            "and cp.problem_id = :pid",
    nativeQuery = true)
    ClassProblem findClassProblem(@Param("cid") Integer classId, @Param("pid") Integer problemId);
}
