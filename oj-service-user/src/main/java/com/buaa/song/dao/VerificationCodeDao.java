package com.buaa.song.dao;

import com.buaa.song.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @FileName: VerificationCodeDao
 * @Author: ProgrammerZhao
 * @Date: 2021/8/4
 * @Description:
 */
public interface VerificationCodeDao extends JpaRepository<VerificationCode,Integer> {

}
