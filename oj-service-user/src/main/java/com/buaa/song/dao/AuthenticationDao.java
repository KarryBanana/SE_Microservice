package com.buaa.song.dao;


import com.buaa.song.entity.Authentication;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @FileName: Authentication
 * @Author: ProgrammerZhao
 * @Date: 2020/11/9
 * @Description:
 */
public interface AuthenticationDao extends PagingAndSortingRepository<Authentication,Integer> {

    Authentication findByStudentId(String studentId);

    Authentication findByUserId(Integer userId);

}
