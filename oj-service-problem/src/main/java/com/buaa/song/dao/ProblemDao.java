package com.buaa.song.dao;

import com.buaa.song.entity.Problem;
import org.elasticsearch.index.query.ScriptQueryBuilder;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * @FileName: ProblemDao
 * @Author: ProgrammerZhao
 * @Date: 2020/11/16
 * @Description:
 */

@CacheConfig(cacheNames = "problemList")
public interface ProblemDao extends JpaRepository<Problem, Integer> {

    @Transactional
    @Modifying
    @Query(value = "insert into problem_tag(problem_id, tag_id, create_time, update_time) "+
            "values(:pid,:tid, now(), now())", nativeQuery = true)
    void saveProblemTag(@Param("pid") Integer problemId, @Param("tid") Integer tagId);

    @Transactional
    @Modifying
    @Query(value = "insert into problem_language values (null,:pid,:lid)", nativeQuery = true)
    void saveProblemLanguage(@Param("pid") Integer problemId, @Param("lid") Integer languageId);

    @Transactional
    @Modifying
    @Query(value = "delete from problem_tag where problem_id = :pid and tag_id = :tid", nativeQuery = true)
    void deleteProblemTag(@Param("pid") Integer problemId, @Param("tid") Integer tagId);

    @Transactional
    @Modifying
    @Query(value = "delete from problem_language where problem_id = :pid and language_id = :lid", nativeQuery = true)
    void deleteProblemLanguage(@Param("pid") Integer problemId, @Param("lid") Integer languageId);

    @Query(value = "select p.id " +
            "from problem p,class_problem cp,membership m " +
            "where p.creator_id = :userId " +
            "or (m.user_id = :userId " +
            "and (m.type ='creator' or m.type='admin') " +
            "and m.class_id = cp.class_id " +
            "and cp.problem_id = p.id) " +
            "group by p.id " +
            "limit :offset,:limit", nativeQuery = true)
    List<Integer> findMyProblemIds(@Param("userId") Integer userId, @Param("offset") Integer offset,
                                             @Param("limit") Integer limit);

    @Query(value = "select id from problem limit :offset,:limit", nativeQuery = true)
    List<Integer> findAllProblemIds(@Param("offset") Integer offset, @Param("limit") Integer limit);

    @Query(value = "select count(distinct p.id) " +
            "from problem p,course c,class_problem cp,membership m " +
            "where p.access = 'public' " +
            "       or p.creator_id = :userId " +
            "       or (:userId = m.user_id " +
            "           and m.class_id = c.id " +
            "           and c.id = cp.class_id " +
            "           and cp.problem_id = p.id)", nativeQuery = true)
    Integer getProblemCount(@Param("userId") Integer userId);

    @Query(value = "select count(p.id) from problem p", nativeQuery = true)
    Integer getProblemCount();

    @Query(value = "select p.title,p.content,u.nickname,p.time_limit,p.memory_limit, DATE_FORMAT(p.update_time,'%Y-%m-%d %H:%i:%s') update_time, " +
            "count(s.id) sub_num,count(distinct s.user_id) sub_user_num, " +
            "count(if(s.result = 'AC',s.id,null)) ac_num,count(distinct if(s.result = 'AC',s.user_id,null)) ac_user_num " +
            "from problem p left join user u on p.creator_id = u.id " +
            "left join submission s on p.id = s.problem_id " +
            "where p.id = :id", nativeQuery = true)
    Map<String, Object> getProblemInfo(@Param("id") Integer problemId);

    @Query(value = "select count(s.id) sub_num,count(distinct s.user_id) sub_user_num, " +
            "count(if(s.result = 'AC',s.id,null)) ac_num,count(distinct if(s.result = 'AC',s.user_id,null)) ac_user_num " +
            "from problem p, submission s " +
            "where p.id = :id and p.id = s.problem_id "
    , nativeQuery = true)
    Map<String, Object> findProblemAcSubNum(@Param("id") Integer problemId);

    @Query(value = "select p.id,p.title,p.difficulity,u.nickname, " +
            "count(s.id) sub_num,count(distinct s.user_id) sub_user_num, " +
            "count(if(s.result = 'AC',s.id,null)) ac_num," +
            "count(distinct if(s.result = 'AC',s.user_id,null)) ac_user_num " +
            "from problem p left join user u on p.creator_id = u.id " +
            "left join submission s on p.id = s.problem_id " +
            "where p.id = :pid " +
            "group by p.id", nativeQuery = true)
    Map<String, Object> searchById(@Param("pid") Integer problemId);

    @Query(value = "select count(*) " +
            "from class_problem cp,membership m " +
            "where cp.problem_id = :pid " +
            "and cp.class_id = m.class_id " +
            "and m.user_id = :uid", nativeQuery = true)
    BigInteger hasProblemPermission(@Param("pid") Integer problemId, @Param("uid") Integer userId);

    @Transactional
    @Modifying
    @Query(value = "insert into problem_share " +
            "values (null,:pid,:uid,:create_user,now())", nativeQuery = true)
    Integer insertIntoProblemShare(@Param("pid") Integer problemId, @Param("uid") Integer userId,
                                   @Param("create_user") Integer createUser);

    @Transactional
    @Modifying
    @Query(value = "insert into course_problem " +
            "values (null,:cid,:pid,:create_user,now())", nativeQuery = true)
    Integer insertIntoCourseProblem(@Param("pid") Integer problemId, @Param("cid") Integer courseId,
                                    @Param("create_user") Integer createUser);

    @Query(value = "select p.id, p.title, p.difficulty, c.name as name, u.nickname as creator " +
            "from problem p, class_problem cp, class c, user u " +
            "where p.id in :ids and p.id = cp.problem_id and cp.class_id = c.id and p.creator_id = u.id " +
            "order by c.name, p.id ASC " +
            "limit :offset, :limit"
            , nativeQuery = true)
    List<Map<String, Object>> findAllProblemsInSearch(@Param("ids") List<Integer> problemIds, @Param("offset") Integer offset,
                                                      @Param("limit") Integer limit);

    @Query(value = "select t.* from ( " +
            "select p.id,p.title, p.difficulty, c.name as name, u.nickname as creator " +
            "from problem p, class_problem cp, class c, user u " +
            "where p.id in :ids and p.id = cp.problem_id and cp.class_id = c.id and p.creator_id = u.id " +
            "and (cp.access = 'public' or p.creator_id = :user_id ) " +
            "UNION " +
            "select p.id,p.title, p.difficulty, c.name as name, u.nickname as creator " +
            "from problem p,class_problem cp,class c, membership m, user u " +
            "where p.id in :ids and p.creator_id = u.id " +
            "and m.user_id = :user_id and m.class_id = c.id and c.id = cp.class_id and cp.problem_id = p.id " +
            "and CASE " +
            "  WHEN m.type = 'member' THEN " +
            "   cp.access != 'private' " +
            "  ELSE " +
            "   cp.access " +
            "END ) t " +
            "order by t.name, t.id ASC " +
            "limit :offset, :limit"
            , nativeQuery = true)
    List<Map<String, Object>> findMyProblemsInSearch(@Param("ids") List<Integer> problemIds, @Param("user_id") Integer userId,
                                                     @Param("offset") Integer offset, @Param("limit") Integer limit);


    // 用户查询题库下题目： 公开public题目 + 自己创建的题目 + 班级内的题目
    @Cacheable
    @Query(value = "select t.* from " +
            "(select p.id, p.title, p.difficulty, c.name as name, u.nickname as creator " +
            "from problem p, class_problem cp, class c, user u " +
            "where p.id = cp.problem_id and cp.class_id = c.id and cp.access = 'public' and p.creator_id = u.id " +
            "UNION " +
            "select p.id, p.title, p.difficulty, c.name as name, u.nickname as creator " +
            "from problem p, class_problem cp, class c, user u " +
            "where p.id = cp.problem_id and cp.class_id = c.id and p.creator_id = :user_id and p.creator_id = u.id " +
            "UNION " +
            "select p.id, p.title, p.difficulty, c.name as name, u.nickname as creator " +
            "from problem p, class_problem cp, class c, membership m, user u " +
            "where m.user_id = :user_id and m.class_id = cp.class_id and p.id = cp.problem_id and cp.class_id = c.id and p.creator_id = u.id " +
            "and CASE " +
            "WHEN m.type = 'member' " +
            "THEN cp.access != 'private' " +
            "ELSE " +
            "cp.access " +
            "END) t " +
            "order by t.name, t.id ASC "+
            "limit :offset, :limit"
            , nativeQuery = true)
    List<Map<String, Object>> findPublicProblems(@Param("user_id") Integer userId, @Param("offset") Integer offset,
                                                 @Param("limit") Integer limit);


    // 类似于赵博学长的findMyProblemsInSearch
    // 通过问题的创建者id进行搜索
    @Query(value = "select t.* from " +
            "(select p.id, p.title, p.difficulty, c.name as name, u.nickname " +
            "from problem p, class_problem cp, class c, user u " +
            "where p.creator_id in :ids and p.id = cp.problem_id and cp.class_id = c.id and cp.access = 'public' and p.creator_id = u.id " +
            "UNION " +
            "select p.id, p.title, p.difficulty, c.name as name, u.nickname " +
            "from problem p, class_problem cp, class c, user u " +
            "where p.creator_id in :ids and p.id = cp.problem_id and cp.class_id = c.id and p.creator_id = :user_id and p.creator_id = u.id " +
            "UNION " +
            "select p.id, p.title, p.difficulty, c.name as name, u.nickname " +
            "from problem p, class_problem cp, class c, membership m, user u " +
            "where p.creator_id in :ids and m.user_id = :user_id and m.class_id = cp.class_id and p.id = cp.problem_id and cp.class_id = c.id and p.creator_id = u.id " +
            "and CASE " +
            "   WHEN m.type = 'member' " +
            "   THEN cp.access != 'private' " +
            "   ELSE " +
            "   cp.access " +
            "END) t " +
            "order by t.name, t.id ASC " +
            "limit :offset , :limit "
    , nativeQuery = true)
    List<Map<String, Object>> findProblemsInSearch(@Param("user_id") Integer userId, @Param("ids") List<Integer> creatorIds,
                                                   @Param("offset") Integer offset,
                                                   @Param("limit") Integer limit);


    // 查看用户是否ac题目
    @Query(value = "select * " +
            "from submission sub " +
            "where sub.problem_id = :problem_id " +
            "and sub.user_id = :user_id " +
            "and sub.result = 'AC' " +
            "limit 1"
    , nativeQuery = true)
    List<Map<String, Object>> findAcceptSubmission(@Param("user_id") Integer userId, @Param("problem_id") Integer problem_id);

    // 查看用户是否做过题目
    @Query(value = "select * " +
            "from submission sub " +
            "where sub.problem_id = :problem_id " +
            "and sub.user_id = :user_id " +
            "limit 1"
            , nativeQuery = true)
    List<Map<String, Object>> findAttemptSubmission(@Param("user_id") Integer userId, @Param("problem_id") Integer problem_id);


    // 通过题目id精确查找题目
    @Query(value = "select t.* from ( " +
            "select p.id,p.title, p.difficulty, c.name as name, u.nickname as creator " +
            "from problem p, class_problem cp, class c, user u " +
            "where p.id = :problemId and p.id = cp.problem_id and cp.class_id = c.id and p.creator_id = u.id " +
            "and (cp.access = 'public' or p.creator_id = :userId ) " +
            "UNION " +
            "select p.id,p.title, p.difficulty, c.name as name, u.nickname as creator " +
            "from problem p,class_problem cp,class c, membership m, user u " +
            "where p.id = :problemId and p.creator_id = u.id  " +
            "and m.user_id = :userId and m.class_id = c.id and c.id = cp.class_id and cp.problem_id = p.id " +
            "and CASE " +
            "  WHEN m.type = 'member' THEN " +
            "   cp.access != 'private' " +
            "  ELSE " +
            "   cp.access " +
            "END ) t " +
            "order by t.name, t.id ASC "
    ,nativeQuery = true)
    List<Map<String, Object>> searchProblemById(@Param("userId") Integer userId, @Param("problemId") Integer problemId);


    // 查询用户收藏的题目
    @Query(value = "select p.id, p.title, c.create_time time " +
            "from problem p, collection c " +
            "where c.user_id = :user_id and c.problem_id = p.id " +
            "limit :offset, :limit"
            , nativeQuery = true)
    List<Map<String, Object>> findCollectProblems(@Param("user_id") Integer userId, @Param("offset") Integer offset,
                                                  @Param("limit") Integer limit);

    // 用户收藏题目
    @Transactional
    @Modifying
    @Query(value = "insert into collection " +
            "values(NULL, :uid, :pid, now(), now())"
            ,nativeQuery = true)
    Integer insertIntoCollectProblem(@Param("uid") Integer userId, @Param("pid") Integer problemId);

    // 检查用户是否收藏过题目
    @Query(value = "select * " +
            "from collection c " +
            "where c.user_id = :user_id and c.problem_id = :problem_id"
            , nativeQuery = true)
    List<Map<String, Object>> checkProblemCollected(@Param("user_id") Integer userId, @Param("problem_id") Integer problemId);

    // 用户取消收藏
    @Transactional
    @Modifying
    @Query(value = "delete from collection " +
            "where user_id = :user_id and problem_id = :problem_id"
            , nativeQuery = true)
    Integer deleteCollectProblem(@Param("user_id") Integer userId, @Param("problem_id") Integer problemId);

    // 返回ac排行榜前10名
    @Query(value = "select sub.user_id as studentId, u.nickname as username, count(distinct(sub.problem_id)) as acceptNum " +
            "from (select * from submission sub where sub.create_time >= DATE_SUB(now(),INTERVAL 1 MONTH)) sub, user u " +
            "where sub.result = 'AC' and sub.user_id = u.id " +
            "group by sub.user_id " +
            "order by acceptNum DESC " +
            "limit 0, 10",
    nativeQuery = true)
    List<Map<String, Object>> findAcceptRankList();

    // 返回Q&A列表
    @Query(value = "select qa.question as question, qa.answer as answer " +
            "from question_answer qa " +
            "order by qa.id",
    nativeQuery = true)
    List<Map<String, Object>> findQuestionAnswer();

    @Query(value = "select p.id,p.title,p.difficulity " +
            "from problem p",nativeQuery = true)
    List<Map<String, Object>> findAllProblemsInTask();

    @Query(value = "select p.id,p.title,p.difficulity " +
            "from course_problem cp,problem p " +
            "where cp.course_id = :cid " +
            "and cp.problem_id = p.id",nativeQuery = true)
    List<Map<String, Object>> findProblemsInTask(@Param("cid") Integer courseId);

    @Query(value = "select p.id,p.title,p.difficulty,u.id creatorId, " +
            "u.nickname creatorName " +
            "from problem p left join user u on p.creator_id=u.id " +
            "where p.id=:id",nativeQuery = true)
    Map<String,Object> findProblemById(@Param("id") Integer problemId);


    @Query(value = "select pl.language_id lid " +
            "from problem_language pl " +
            "where pl.problem_id = :pid ", nativeQuery = true)
    List<Integer> findLangByProblemId(@Param("pid") Integer problemId);

    @Query(value = "select count(distinct s.user_id) subUserNum, " +
            "count(distinct if(s.result='AC',s.user_id,null)) acUserNum " +
            "from submission s " +
            "where s.problem_id = :id ",nativeQuery = true)
    Map<String,Object> getUserNum(@Param("id") Integer problemId);

    @Cacheable
    @Query(value = "select p.id, p.title, p.difficulty, c.name as name, u.nickname as creator " +
            "from problem p, class_problem cp, class c, user u " +
            "where p.id = cp.problem_id and cp.class_id = c.id and p.creator_id = u.id " +
            "order by name, p.id " +
            "limit :offset,:limit ", nativeQuery = true)
    List<Map<String, Object>> findAllProblems(@Param("offset") Integer offset, @Param("limit") Integer limit);
}
