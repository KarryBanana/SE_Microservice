package com.buaa.song.dao;

import com.buaa.song.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @FileName: IssueDao
 * @Author: ProgrammerZhao
 * @Date: 2021/5/22
 * @Description:
 */
public interface IssueDao extends JpaRepository<Issue,Integer> {
}
