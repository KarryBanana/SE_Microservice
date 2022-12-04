package com.buaa.song.dao;

import com.buaa.song.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @FileName: SubmissionDao
 * @Author: ProgrammerZhao
 * @Date: 2021/4/30
 * @Description:
 */
public interface SubmissionDao extends JpaRepository<Submission,Integer> {

}
