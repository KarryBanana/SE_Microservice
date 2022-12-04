package com.buaa.song.service;

import com.buaa.song.dto.CreateTaskDto;
import com.buaa.song.dto.IssueDto;
import com.buaa.song.result.Result;

/**
 * @FileName: TaskService
 * @Author: ProgrammerZhao
 * @Date: 2021/5/14
 * @Description:
 */
public interface TaskService {
    Result taskList(Integer userId, Integer page, Integer limit);

    Result problemList(Integer userId, Integer courseId);

    Result createTask(CreateTaskDto taskDto, Integer userId);

    Result info(Integer taskId);

    Result problem(Integer taskId, Integer order);

    Result rank(Integer taskId);

    Result getIssue(Integer taskId);

    Result createIssue(IssueDto issueDto, Integer taskId, Integer userId);

    Result question1(Integer taskId);

    Result question2(Integer taskId);

    Result answer(Integer questionId,Integer userId,String reply);
}
