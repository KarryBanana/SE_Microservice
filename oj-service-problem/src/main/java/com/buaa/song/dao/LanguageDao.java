package com.buaa.song.dao;

import com.buaa.song.entity.Language;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @FileName: LanguageDao
 * @Author: ProgrammerZhao
 * @Date: 2020/11/21
 * @Description:
 */
public interface LanguageDao extends CrudRepository<Language,Integer> {

    @Query(value = "select l.* " +
            "from language l,problem_language pl " +
            "where pl.problem_id = 1 and pl.language_id = l.id",nativeQuery = true)
    List<Language> findLanguagesByProblem(@Param("pid") Integer problemId);

    @Query(value = "select l.id " +
            "from language l,problem_language pl " +
            "where pl.problem_id = :pid and pl.language_id = l.id",nativeQuery = true)
    List<Integer> findLanguageIdsByProblem(@Param("pid") Integer problemId);

    Language findByName(String name);
}
