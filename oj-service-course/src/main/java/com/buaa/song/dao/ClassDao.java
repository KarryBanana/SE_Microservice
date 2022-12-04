package com.buaa.song.dao;

import com.buaa.song.entity.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * @FileName: ClassDao
 * @Author: ProgrammerZhao
 * @Date: 2020/11/7
 * @Description:
 */
public interface ClassDao extends JpaRepository<Class, Integer> {

    // 获取班级ID对应的课程信息
    @Query(value = "select cu.id, cu.name " +
            "from course cu, class c " +
            "where c.id = :class_id and c.course_id = cu.id "
    , nativeQuery = true)
    List<Map<String, Object> > getCourseByClassId(@Param("class_id") Integer classId);


    // 获取用户班级列表
    @Query(value = "select c.id, c.`name` " +
            "from class c, membership m " +
            "where m.user_id = :user_id and m.class_id = c.id " +
            "order by c.id ASC "
            , nativeQuery = true)
    List<Map<String, Object> > getUserClassList(@Param("user_id") Integer userId);

    // 获取班级下的学生
    @Query(value = "select u.id userId, u.realname name , u.student_id studentId, case m.type " +
            "when 'creator' then 2 " +
            "when 'admin' then 1 " +
            "when 'member' then 0 " +
            "end type " +
            "from user u, membership m " +
            "where m.class_id = :class_id " +
            "and m.user_id = u.id " +
            "order by type DESC",
            nativeQuery = true)
    List<Map<String, Object>> getClassStudent(@Param("class_id") Integer classId);

    // 用户查询班级下题目
    @Query(value = "select t.* from " +
            "( select p.id, p.title, p.difficulty, u.nickname as creator " +
            "from problem p, class_problem cp, membership m, user u " +
            "where m.user_id = :user_id and m.class_id = :class_id and m.class_id = cp.class_id and cp.problem_id = p.id " +
            "and p.creator_id = u.id " +
            "and CASE " +
            "   WHEN m.type = 'member' " +
            "      THEN cp.access != 'private' " +
            "   ELSE " +
            "       cp.access " +
            "END " +
            "UNION " +
            "select p.id, p.title, p.difficulty, u.nickname as creator " +
            "from problem p, class_problem cp, user u " +
            "where cp.problem_id = p.id and cp.class_id = :class_id and cp.access = 'public' and p.creator_id = u.id ) t " +
            "order by t.id ASC " +
            "limit :offset, :limit"
            , nativeQuery = true)
    List<Map<String, Object>> findClassProblems(@Param("user_id") Integer userId, @Param("class_id") Integer classId,
                                                @Param("offset") Integer offset, @Param("limit") Integer limit);

    @Query(value = "select count(distinct(cp.problem_id)) num " +
            "from class_problem cp, membership m " +
            "where " +
            "(cp.class_id = :class_id and cp.access = 'public') " +
            "or " +
            "( :user_id = m.user_id and :class_id = m.class_id " +
            "and m.class_id = cp.class_id " +
            "and CASE " +
            "   WHEN m.type = 'member' " +
            "     THEN cp.access != 'private' " +
            "   ELSE " +
            "     cp.access " +
            "END) "
    , nativeQuery = true)
    Integer getUserClassProblemCount(@Param("user_id") Integer userId, @Param("class_id") Integer classId);

    @Query(value = "select count( DISTINCT(cp.problem_id) ) " +
            "from class_problem cp " +
            "where cp.class_id = :class_id "
    , nativeQuery = true)
    Integer getAllClassProblemCount(@Param("class_id") Integer classId);

    // 针对管理员，查询该班级下的所有题目
    @Query(value = "select p.id, p.title, p.difficulty, u.nickname as creator, cp.access " +
            "from problem p, class_problem cp, user u " +
            "WHERE cp.class_id = :class_id and cp.problem_id = p.id and p.creator_id = u.id " +
            "limit :offset, :limit"
            , nativeQuery = true)
    List<Map<String, Object>> findClassAllProblems(@Param("class_id") Integer classId,
                                                   @Param("offset") Integer offset, @Param("limit") Integer limit);

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

    @Query(value = "select count(distinct(e.id)) num " +
            "from exam e, membership m " +
            "where " +
            "(e.class_id = :class_id and e.access = 'public') " +
            "or " +
            "(" +
            ":user_id = m.user_id and :class_id = m.class_id " +
            "and m.class_id = e.class_id " +
            "and CASE " +
            "WHEN m.type = 'member' " +
            "   THEN e.access != 'private' " +
            "ELSE " +
            "   e.access " +
            "END)"
    , nativeQuery = true)
    Integer getUserClassExamCount(@Param("user_id") Integer userId, @Param("class_id") Integer classId);

    @Query(value = "select count(e.id) num " +
            "from exam e " +
            "where :class_id = e.class_id"
    , nativeQuery = true)
    Integer getAllClassExamCount(@Param("class_id") Integer classId);


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

    // 获取题目的AC、AC User、Sub、Sub User数量
    @Query(value = "select count(s.id) sub_num,count(distinct s.user_id) sub_user_num, " +
            "count(if(s.result = 'AC',s.id,null)) ac_num,count(distinct if(s.result = 'AC',s.user_id,null)) ac_user_num " +
            "from problem p, submission s " +
            "where p.id = :id and p.id = s.problem_id "
            , nativeQuery = true)
    Map<String, Object> findProblemAcSubNum(@Param("id") Integer problemId);

    // 获取题目的标签Tags
    @Query(value = "select t.`name` tagName, t.count " +
            "from problem_tag pt, tag t " +
            "where t.id = pt.tag_id and pt.problem_id = :id " +
            "limit 1 "
            ,nativeQuery = true)
    Map<String, Object> findProblemTags(@Param("id") Integer problemId);

    @Query(value = "select c.id,c.`name`,u.id creatorId,u.nickname creatorName " +
            "from membership m1,class c,user u " +
            "where m1.user_id = :id " +
            "and (m1.type='creator' or m1.type='admin') " +
            "and m1.class_id = c.id " +
            "and c.creator = u.id", nativeQuery = true)
    List<Map<String,Object>> findMyClass(@Param("id") Integer id);

    @Query(value = "select c.id,c.`name`  " +
            "from class c " +
            "order by c.id ASC "
            , nativeQuery = true)
    List<Map<String,Object>> findAllClass();

    @Query(value = "select c.id, c.name, c.description info, c.`order`, c.access, cu.`name` course " +
            "from class c, course cu " +
            "where c.course_id = cu.id " +
            "and c.id = :class_id "
    ,nativeQuery = true)
    Map<String, Object> getAdminClassInfo(@Param("class_id") Integer classId);

    @Query(value = "select ct.id examId, ct.title examName, c.name className,ct.start_time startTime, ct.end_time endTime,\n" +
            "CASE " +
            "   WHEN ct.start_time <= now() and now() <= ct.end_time THEN 1 " +
            "   WHEN now() < ct.start_time THEN 0 " +
            "   WHEN now() > ct.end_time THEN -1 " +
            "END stat, " +
            "count(distinct(cp.problem_id)) problemNum " +
            "from class c, exam ct, exam_problem cp " +
            "where c.id = :class_id and c.id = ct.class_id and ct.id = cp.exam_id " +
            "group by ct.id " +
            "order by stat, endTime DESC " +
            "limit :offset, :limit"
            ,nativeQuery = true)
    List<Map<String, Object>> getAdminClassExams(@Param("class_id") Integer classId,
                                                 @Param("offset") Integer offset,
                                                 @Param("limit") Integer limit);

    @Query(value = "select u.username,u.description,u.nickname,u.school,u.college," +
            "r.chinese_name,a.student_id,a.real_name,m.type,m.create_time " +
            "from class c join membership m on c.id = m.class_id " +
            "join user u on m.user_id = u.id " +
            "join role r on u.role_id = r.id " +
            "left join authentication a on u.id = a.user_id " +
            "where c.id = :cid and u.id = :uid", nativeQuery = true)
    List<Map<String, Object>> getUserInfoOfClass(@Param("cid") Integer cid, @Param("uid") Integer uid);

    // ckr没采用zb学长这种写法
    @Query(value = "select p.id,p.title,count(s.id) subNum,count(if(s.result='AC',s.id,null)) acNum, " +
            "count(distinct s.user_id) subUserNum,count(distinct if(s.result='AC',s.user_id,null)) acUserNum, " +
            "count(if(s.result='AC' and s.user_id = :user_id,s.user_id,null)) isAC, " +
            "count(if(s.user_id = :user_id,s.user_id,null)) isSub " +
            "from class_problem cp join problem p on cp.problem_id = p.id " +
            "left join submission s on p.id = s.problem_id " +
            "where cp.class_id = :cid group by p.id limit :start,:limit", nativeQuery = true)
    List<Map<String, Object>> findProblemList(@Param("cid") Integer classId,
                                              @Param("user_id") Integer userId,
                                              @Param("start") Integer start,
                                              @Param("limit") Integer limit);

    // ckr没采用zb学长这种写法
    @Query(value = "select id contestId,title,start_time startTime,end_time endTime " +
            "from exam " +
            "where class_id = :class_id " +
            "limit :start,:limit",nativeQuery = true)
    List<Map<String,Object>> getClassContest(@Param("class_id") Integer classId,
                                             @Param("start") Integer start,
                                             @Param("limit") Integer limit);

    @Query(value = "select count(*) from membership where class_id = :id",nativeQuery = true)
    BigInteger getClassMemberNum(@Param("id") Integer classId);
}
