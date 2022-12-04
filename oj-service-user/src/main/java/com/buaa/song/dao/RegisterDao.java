package com.buaa.song.dao;

import com.buaa.song.entity.Register;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * @FileName: RegisterDao
 * @Author: ProgrammerZhao
 * @Date: 2020/10/29
 * @Description:
 */
public interface RegisterDao extends CrudRepository<Register,Integer> {

    Register save(Register registerUser);

    List<Register> findByStatus(Integer status);
}
