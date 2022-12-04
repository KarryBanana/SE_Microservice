package com.buaa.song.dao;

import com.buaa.song.entity.Membership;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @FileName: MembershipDao
 * @Author: ProgrammerZhao
 * @Date: 2020/11/8
 * @Description:
 */
public interface MembershipDao extends PagingAndSortingRepository<Membership,Integer> {

    @Query(value = "select * " +
            "from membership m " +
            "where m.user_id = :user_id " +
            "and m.class_id = :class_id ",
    nativeQuery = true)
    Membership findByUserIdAndClassId(@Param("user_id") Integer userId, @Param("class_id") Integer classId);

    @Modifying
    @Transactional
    @Query(value = "delete from membership " +
            "where user_id = :user_id " +
            "and class_id = :class_id ",
    nativeQuery = true)
    void deleteByUserIdAndClassId(@Param("user_id") Integer userId, @Param("class_id") Integer classId);
}
