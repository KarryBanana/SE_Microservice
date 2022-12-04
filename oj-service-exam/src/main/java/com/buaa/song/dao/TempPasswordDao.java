package com.buaa.song.dao;

import com.buaa.song.entity.TempPassword;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @FileName: TempPasswordDao
 * @Author: ProgrammerZhao
 * @Date: 2021/4/7
 * @Description:
 */
public interface TempPasswordDao extends JpaRepository<TempPassword,Integer> {

    TempPassword findByUserIdAndTempPassword(Integer userId, String tempPassword);

}
