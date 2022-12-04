package com.buaa.song.dao;

import com.buaa.song.entity.SubmissionCode;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @FileName: SubmissionCodeDao
 * @Author: ProgrammerZhao
 * @Date: 2021/4/30
 * @Description:
 */
public interface SubmissionCodeDao extends JpaRepository<SubmissionCode,Integer> {

}
