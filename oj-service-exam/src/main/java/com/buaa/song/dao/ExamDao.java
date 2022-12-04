package com.buaa.song.dao;

import com.buaa.song.entity.Exam;
import org.checkerframework.framework.qual.EnsuresQualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * @FileName: ExamDao
 * @Author: ProgrammerZhao
 * @Date: 2021/4/7
 * @Description:
 */
public interface ExamDao extends JpaRepository<Exam, Integer> {

    @Query(value = "select id,title,description,start_time,end_time,access " +
            "from exam " +
            "where id = :id", nativeQuery = true)
    Map<String, Object> getExamInfo(@Param("id") Integer id);

    @Query(value = "select p.id,ep.order,p.title,p.content,p.time_limit,p.memory_limit, " +
            "count(s.id) sub_num, " +
            "count(if(s.result='AC',s.id,null)) ac_num, " +
            "count(distinct s.user_id) sub_user_num, " +
            "count(distinct if(s.result='AC',s.user_id,null)) ac_user_num " +
            "from exam_problem ep join problem p on ep.problem_id = p.id " +
            "left join submission s on p.id = s.problem_id and s.exam_id = ep.exam_id " +
            "where ep.exam_id = :id " +
            "group by p.id " +
            "order by ep.order", nativeQuery = true)
    List<Map<String, Object>> getProblemList(@Param("id") Integer examId);

    @Query(value = "select ep.problem_id,ep.`order`, " +
            "count(distinct if(s.exam_id = :exam_id,s.user_id,null)) sub_user_num, " +
            "count(distinct if(s.exam_id = :exam_id and s.result='AC',s.user_id,null)) ac_user_num " +
            "from exam_problem ep left join submission s on ep.problem_id = s.problem_id " +
            "where ep.exam_id = :exam_id " +
            "group by ep.problem_id " +
            "order by ep.`order` asc", nativeQuery = true)
    List<Map<String, Object>> getProblemRecord(@Param("exam_id") Integer examId);

    @Query(value = "select u.id,u.username,a.student_id, " +
            "if(a.real_name!=null,a.real_name,u.nickname) 'name' " +
            "from exam e join membership m on e.class_id = m.class_id " +
            "join user u on m.user_id = u.id " +
            "left join authentication a on u.id = a.user_id " +
            "where e.id = :exam_id " +
            "order by u.id", nativeQuery = true)
    List<Map<String, Object>> getExamUserInfo(@Param("exam_id") Integer examId);

    @Query(value = "select s.user_id,s.problem_id,s.result,s.score*ep.score score," +
            "timestampdiff(second,e.start_time,s.create_time) 'time' " +
            "from exam e join exam_problem ep on e.id = ep.exam_id " +
            "join submission s on ep.exam_id = s.exam_id " +
            "and ep.problem_id = s.problem_id " +
            "where e.id = :exam_id " +
            "order by s.user_id asc,ep.`order` asc,score desc,s.create_time asc", nativeQuery = true)
    List<Map<String, Object>> getExamSubInfo(@Param("exam_id") Integer examId);

    @Query(value = "select e.problem_id,e.`order`,s.user_id " +
            "from (select s.problem_id,ep.`order`,min(s.create_time) earliest_ac " +
            "from exam_problem ep join submission s " +
            "on ep.exam_id = s.exam_id and ep.problem_id = s.problem_id " +
            "where ep.exam_id = :exam_id " +
            "and s.result = 'AC' " +
            "group by s.problem_id) e,submission s " +
            "where s.exam_id = :exam_id " +
            "and s.result = 'AC' " +
            "and e.problem_id = s.problem_id " +
            "and e.earliest_ac = s.create_time", nativeQuery = true)
    List<Map<String, Object>>  getEarliestAcOfProblem(@Param("exam_id") Integer examId);

    @Query(value = "select s.id,s.result,s.score,s.time_cost,s.memory_cost,s.create_time sub_time, " +
            "s.code_length len,l.`name` 'language',ep.`order` problem " +
            "from submission s " +
            "left join exam_problem ep on s.problem_id = ep.problem_id and s.exam_id = ep.exam_id " +
            "left join `language` l on s.`language_id` = l.id " +
            "where s.exam_id = :exam_id " +
            "and s.user_id = :user_id " +
            "order by s.create_time", nativeQuery = true)
    List<Map<String, Object>> getSubmission(@Param("exam_id") Integer examId, @Param("user_id") Integer userId);

    @Query(value = "select ct.id examId,ct.title examName,ct.start_time startTime,ct.end_time endTime, " +
            "c.id classId,c.name className, " +
            "CASE " +
            "    WHEN ct.start_time <= now() and now() <= ct.end_time THEN 1 " +
            "    WHEN now() < ct.start_time THEN 0 " +
            "    WHEN now() > ct.end_time THEN -1 " +
            "END `status`, " +
            "count(distinct(cp.problem_id)) problemNum " +
            "from exam ct, class c, exam_problem cp, membership m " +
            "where m.user_id = :user_id " +
            "and (m.type='creator' or m.type='admin') " +
            "and m.class_id = ct.class_id " +
            "and ct.class_id = c.id " +
            "and ct.id = cp.exam_id " +
            "group by ct.id " +
            "order by `status` DESC, endTime DESC " +
            "limit :offset, :limit", nativeQuery = true)
    List<Map<String, Object>> getMyExams(@Param("user_id") Integer userId, @Param("offset") Integer offset,
                                         @Param("limit") Integer limit);

    @Query(value = "select count(DISTINCT(ct.id)) num " +
            "from exam ct, membership m " +
            "where ct.access = 'public' " +
            "or " +
            "( :user_id = m.user_id and m.class_id = ct.class_id " +
            "and " +
            "CASE " +
            "   WHEN m.type = 'member' THEN " +
            "     ct.access != 'private' " +
            "   ELSE " +
            "     ct.access " +
            "END )"
    , nativeQuery = true)
    Integer getMyExamsPageNum(@Param("user_id") Integer userId);

    @Query(value = "select ct.id examId, ct.title examName, c.name className,ct.start_time startTime, ct.end_time endTime, " +
            "CASE " +
            "  WHEN ct.start_time <= now() and now() <= ct.end_time THEN 1 " +
            "  WHEN now() < ct.start_time THEN 0 " +
            "  WHEN now() > ct.end_time THEN -1 " +
            "END `status`, " +
            "count(distinct(cp.problem_id)) problemNum " +
            "from class c, exam ct, exam_problem cp " +
            "where c.id = ct.class_id and ct.id = cp.exam_id " +
            "group by ct.id " +
            "order by `status` DESC, endTime DESC " +
            "limit :offset,:limit", nativeQuery = true)
    List<Map<String, Object>> getAllExams(@Param("offset") Integer offset, @Param("limit") Integer limit);

    @Query(value = "select count(distinct(ct.id)) num from exam ct"
    , nativeQuery = true)
    Integer getAllExamsPageNum();

    @Query(value = "select t.*from ( " +
            "select ct.id examId, ct.title examName, c.name className,ct.start_time startTime, ct.end_time endTime, " +
            "CASE " +
            "   WHEN ct.start_time <= now() and now() <= ct.end_time THEN 1 " +
            "   WHEN now() < ct.start_time THEN 0 " +
            "   WHEN now() > ct.end_time THEN -1 " +
            "END stat, " +
            "count(distinct(cp.problem_id)) problemNum " +
            "from class c, exam ct, exam_problem cp " +
            "where c.id = :class_id and c.id = ct.class_id " +
            "and cp.exam_id = ct.id " +
            "and ct.access = 'public' " +
            "group by ct.id " +
            "UNION " +
            "select ct.id examId, ct.title examName, c.name className,ct.start_time startTime, ct.end_time endTime, " +
            "CASE " +
            "   WHEN ct.start_time <= now() and now() <= ct.end_time THEN 1 " +
            "   WHEN now() < ct.start_time THEN 0 " +
            "   WHEN now() > ct.end_time THEN -1 " +
            "END stat," +
            "count(distinct(cp.problem_id)) problemNum " +
            "from class c, exam ct, membership m, exam_problem cp " +
            "where c.id = :class_id and c.id = ct.class_id " +
            "and cp.exam_id = ct.id " +
            "and m.class_id = :class_id and m.user_id = :user_id " +
            "and CASE " +
            "   WHEN m.type = 'member' THEN " +
            "   ct.access != 'private' " +
            "   ELSE " +
            "   ct.access = 'private' or ct.access = 'protect' or ct.access = 'public' " +
            "END " +
            "group by ct.id ) t " +
            "order by t.endTime desc " +
            "limit :offset, :limit",
    nativeQuery = true)
    List<Map<String, Object>> getClassExam(@Param("user_id") Integer userId, @Param("class_id") Integer classId,
                                            @Param("offset") Integer offset, @Param("limit") Integer limit);

    @Transactional
    @Modifying
    @Query(value = "insert into exam_problem " +
            "values (null,:eid,:pid,:order,:uid,:score, now(), now() )", nativeQuery = true)
    int saveExamProblem(@Param("eid") Integer examId, @Param("pid") Integer problemId, @Param("order") Integer order,
                        @Param("uid") Integer userId, @Param("score") Integer score);

    @Query(value = "select p.id,p.title,ep.`order`,ep.score " +
            "from exam_problem ep,problem p " +
            "where ep.exam_id = :exam_id " +
            "and ep.problem_id = p.id " +
            "order by ep.`order`",nativeQuery = true)
    List<Map<String,Object>> getExamProblems(@Param("exam_id") Integer examId);

    @Transactional
    @Modifying
    @Query(value = "delete from exam_problem " +
            "where exam_id = :eid and problem_id = :pid",nativeQuery = true)
    void deleteExamProblem(@Param("eid") Integer examId, @Param("pid") Integer problemId);

    @Query(value = "select count(*) " +
            "from membership m " +
            "where m.user_id = :uid " +
            "and m.class_id = :cid",nativeQuery = true)
    BigInteger hasPermission(@Param("uid") Integer userId, @Param("cid") Integer classId);

    @Query(value = "select id from user where id = :uid",nativeQuery = true)
    Map<String,Object> test(@Param("uid") Integer id);
}
