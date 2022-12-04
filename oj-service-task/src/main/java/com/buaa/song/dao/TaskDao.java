package com.buaa.song.dao;

import com.buaa.song.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @FileName: TaskDao
 * @Author: ProgrammerZhao
 * @Date: 2021/5/14
 * @Description:
 */
public interface TaskDao extends JpaRepository<Task, Integer> {

    @Query(value = "select t.id,t.title,c.id course_id," +
            "c.name course_name,t.start_time,t.end_time " +
            "from membership m,course c,task t " +
            "where m.user_id = :user_id " +
            "and (m.type = 'creator' or m.type = 'admin') " +
            "and m.course_id = c.id " +
            "and c.id = t.course_id " +
            "order by t.end_time desc " +
            "limit :page,:limit", nativeQuery = true)
    List<Map<String, Object>> findMyTask(@Param("user_id") Integer userId,
                                         @Param("page") Integer page,
                                         @Param("limit") Integer limit);

    @Query(value = "select t.id,t.title,c.id course_id," +
            "c.name course_name,t.start_time,t.end_time " +
            "from course c,task t " +
            "where t.course_id = c.id " +
            "order by t.end_time desc " +
            "limit :page,:limit", nativeQuery = true)
    List<Map<String, Object>> findAllTask(@Param("page") Integer page,
                                          @Param("limit") Integer limit);

    @Transactional
    @Modifying
    @Query(value = "insert into task_problem " +
            "values (null,:pid,:tid,:order,:score,:uid)", nativeQuery = true)
    int saveTaskProblem(@Param("tid") Integer taskId, @Param("uid") Integer userId, @Param("pid") Integer problemId,
                         @Param("order") Integer order, @Param("score") Double score);

    @Query(value = "select id,title,description,start_time,end_time " +
            "from task " +
            "where id = :id",nativeQuery = true)
    List<Map<String,Object>> getTaskInfo(@Param("id") Integer taskId);

    @Query(value = "select p.id,p.title,p.content,p.time_limit,p.memory_limit, " +
            "count(s.id) sub_num, " +
            "count(if(s.result='AC',s.id,null)) ac_num, " +
            "count(distinct s.user_id) sub_user_num, " +
            "count(distinct if(s.result='AC',s.user_id,null)) ac_user_num   " +
            "from task_problem tp join problem p on tp.problem_id = p.id   " +
            "left join submission s on p.id = s.problem_id and s.task_id = tp.task_id " +
            "where tp.task_id = :tid  " +
            "and tp.`order` = :order " +
            "group by p.id",nativeQuery = true)
    Map<String,Object> getTaskProblem(@Param("tid") Integer taskId, @Param("order") Integer order);

    @Query(value = "select tp.problem_id,tp.`order`, " +
            "count(distinct s.user_id) sub_user_num, " +
            "count(distinct if(s.result='AC',s.user_id,null)) ac_user_num " +
            "from task_problem tp left join submission s " +
            "on tp.problem_id = s.problem_id and tp.task_id = s.task_id " +
            "where tp.task_id = :taskId " +
            "group by tp.problem_id " +
            "order by tp.`order` asc", nativeQuery = true)
    List<Map<String, Object>> getProblemRecord(@Param("task_id") Integer taskId);

    @Query(value = "select u.id,u.username,a.student_id, " +
            "if(a.real_name!=null,a.real_name,u.nickname) 'name' " +
            "from task t join membership m on t.course_id = m.course_id " +
            "join user u on m.user_id = u.id " +
            "left join authentication a on u.id = a.user_id " +
            "where t.id = :task_id " +
            "order by u.id", nativeQuery = true)
    List<Map<String, Object>> getTaskUserInfo(@Param("task_id") Integer taskId);

    @Query(value = "select s.user_id,s.problem_id,s.result,s.score*tp.score score," +
            "timestampdiff(second,t.start_time,s.create_time) 'time' " +
            "from task t join task_problem tp on t.id = tp.task_id " +
            "join submission s on tp.task_id = s.task_id " +
            "and tp.problem_id = s.problem_id " +
            "where t.id = :task_id " +
            "order by s.user_id asc,tp.`order` asc,score desc,s.create_time asc", nativeQuery = true)
    List<Map<String, Object>> getTaskSubInfo(@Param("task_id") Integer taskId);

    @Query(value = "select t.problem_id,t.`order`,s.user_id " +
            "from (select s.problem_id,tp.`order`,min(s.create_time) earliest_ac " +
            "from task_problem tp join submission s " +
            "on tp.task_id = s.task_id and tp.problem_id = s.problem_id " +
            "where tp.task_id = :task_id " +
            "and s.result = 'AC' " +
            "group by s.problem_id) t,submission s " +
            "where s.task_id = :task_id " +
            "and s.result = 'AC' " +
            "and t.problem_id = s.problem_id " +
            "and t.earliest_ac = s.create_time", nativeQuery = true)
    List<Map<String, Object>> getEarliestAcOfProblem(@Param("task_id") Integer taskId);

    @Query(value = "select i.id,i.title,i.content,i.create_time,u.nickname,tp.`order` " +
            "from issue i left join user u on i.creator_id = u.id " +
            "left join task_problem tp on i.task_id = tp.task_id and i.problem_id = tp.problem_id " +
            "where i.task_id = :tid",nativeQuery = true)
    List<Map<String,Object>> getTaskIssue(@Param("tid") Integer taskId);

    @Query(value = "select problem_id " +
            "from task_problem " +
            "where task_id = :tid and `order` = :order",nativeQuery = true)
    Integer findProblemIdByOrder(@Param("tid") Integer taskId, @Param("order") Integer order);
}
