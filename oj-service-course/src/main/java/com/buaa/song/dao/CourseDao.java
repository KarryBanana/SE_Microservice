package com.buaa.song.dao;

import com.buaa.song.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

/**
 * @FileName: CourseDao
 * @Author: ProgrammerZhao
 * @Date: 2020/11/6
 * @Description:
 */
// 这里可以用 PagingAndSortingRepository
public interface CourseDao extends JpaRepository<Course,Integer> {

    // 用户查看能看到的班级
    @Query(value = "select t.* from ( " +
            "select c.id, c.name, c.order, cu.id courseId, cu.name courseName, cu.description " +
            "from class c, course cu " +
            "where c.access = 'public' and c.course_id = cu.id " +
            "UNION " +
            "select c.id, c.name,c.order, cu.id courseId, cu.name courseName, cu.description " +
            "from class c, membership m, course cu " +
            "where m.user_id = :user_id and m.class_id = c.id and c.course_id = cu.id " +
            ") t " +
            "order by t.courseId, t.order ",
            nativeQuery = true)
    List<Map<String, Object>> getUserClass(@Param("user_id") Integer userId);

    // 超级管理员获取所有课程下所有的班级
    @Query(value = "select c.id, c.name " +
            "from class c " +
            "where c.course_id = :course_id ",
    nativeQuery = true)
    List<Map<String, Object>> getCourseClass(@Param("course_id") Integer courseId);


    @Query(value = "select c.id,c.name " +
            "from class c",nativeQuery = true)
    List<Map<String,Object>> findAllCourseName();
    
}
