package com.buaa.song.dao;

import com.buaa.song.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @FileName: UserDao
 * @Author: ProgrammerZhao
 * @Date: 2020/10/27
 * @Description:
 */
public interface UserDao extends PagingAndSortingRepository<User,Integer> {

    User findByUsername(String username);

    User save(User user);

    Page <User> findAll(Pageable pageable);

    @Query(value = "select u.* from user u " +
            "where username = :username and password = :password",nativeQuery = true)
    User findUser(@Param("username") String username,@Param("password") String password);

    @Transactional
    @Modifying
    @Query(value = "update user " +
            "set password = :password " +
            "where username = :mail ",nativeQuery = true)
    Integer updatePassword(@Param("mail") String mail,@Param("password") String password);

    @Query(value = "select username email,nickname,description,school,major,student_id,realname " +
            "from user " +
            "where id = :user_id",nativeQuery = true)
    Map<String,Object> getInfoById(@Param("user_id") Integer userId);


    @Query(value = "select count(sub.id) as subTimes, DATEDIFF(CURRENT_DATE,sub.create_time) as daysBefore " +
            "from submission sub " +
            "where sub.user_id = :user_id and DATEDIFF(CURRENT_DATE,sub.create_time) <= :days_passed " +
            "group by DATE_FORMAT(sub.create_time,\"%Y-%m-%d\") ",
    nativeQuery = true)
    List<Map<String, Object> > getAnnualSubmission(@Param("user_id") Integer userId,
                                                   @Param("days_passed") Long daysPassed);

    @Query(value = "select sub.result result, count(sub.result) number " +
            "from submission sub " +
            "where sub.user_id = :user_id " +
            "group by sub.result ",
    nativeQuery = true)
    List<Map<String, Object> > getAllSubmissionType(@Param("user_id") Integer userId);

    @Query(value = "select sub.result, p.title, p.id, sub.create_time as time " +
            "from submission sub, problem p " +
            "where sub.user_id = :user_id and sub.problem_id = p.id " +
            "order by sub.create_time DESC " +
            "limit 8",
    nativeQuery = true)
    List<Map<String, Object> > getLatestSubmission(@Param("user_id") Integer userId);


    @Query(value = "select count(*) as number " +
            "from submission sub where sub.user_id = :user_id and DATEDIFF(CURRENT_DATE,sub.create_time) = :offset and sub.result = 'AC' " +
            "UNION ALL " +
            "select count(*) as number " +
            "from submission sub where sub.user_id = :user_id and DATEDIFF(CURRENT_DATE,sub.create_time) = :offset and sub.result = 'CE' " +
            "UNION ALL " +
            "select count(*) as number " +
            "from submission sub where sub.user_id = :user_id and DATEDIFF(CURRENT_DATE,sub.create_time) = :offset and sub.result = 'WA' " +
            "UNION ALL " +
            "select count(*) as number " +
            "from submission sub where sub.user_id = :user_id and DATEDIFF(CURRENT_DATE,sub.create_time) = :offset and sub.result = 'PE' " +
            "UNION ALL " +
            "select count(*) as number " +
            "from submission sub where sub.user_id = :user_id and DATEDIFF(CURRENT_DATE,sub.create_time) = :offset and sub.result = 'TLE' " +
            "UNION ALL " +
            "select count(*) as number " +
            "from submission sub where sub.user_id = :user_id and DATEDIFF(CURRENT_DATE,sub.create_time) = :offset and sub.result = 'OE' ",
    nativeQuery = true)
    List<Map<String, Object> > getWeekSubmission(@Param("user_id") Integer userId, @Param("offset") Integer offset);

    @Query(value = "select school " +
            "from school "
    , nativeQuery = true)
    List<String> getUserSchools();
}
