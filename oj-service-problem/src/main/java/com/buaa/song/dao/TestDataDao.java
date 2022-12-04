package com.buaa.song.dao;

import com.buaa.song.entity.TestData;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @FileName: TestDataDao
 * @Author: ProgrammerZhao
 * @Date: 2020/12/16
 * @Description:
 */
public interface TestDataDao extends PagingAndSortingRepository<TestData, Integer> {

    List<TestData> findAllByProblemId(Integer problemId);
}
