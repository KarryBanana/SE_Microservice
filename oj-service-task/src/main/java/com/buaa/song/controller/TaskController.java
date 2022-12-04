package com.buaa.song.controller;

import com.buaa.song.dto.CreateTaskDto;
import com.buaa.song.dto.IssueDto;
import com.buaa.song.dto.PageAndSortDto;
import com.buaa.song.result.Result;
import com.buaa.song.service.TaskService;
import com.buaa.song.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @FileName: TaskController
 * @author: ProgrammerZhao
 * @Date: 2021/5/14
 * @Description:
 */
@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/list")
    public Result taskList(HttpServletRequest request, Integer page, Integer limit ){
        return taskService.taskList(getUserIdFromRequest(request),page,limit);
    }

    @GetMapping("/problem")
    public Result problemList(HttpServletRequest request,Integer courseId){
        return taskService.problemList(getUserIdFromRequest(request),courseId);
    }

    @PostMapping("/create")
    public Result createTask(HttpServletRequest request, CreateTaskDto taskDto){
        return taskService.createTask(taskDto,getUserIdFromRequest(request));
    }

    @GetMapping("/{id}/info")
    public Result taskInfo(@PathVariable("id") Integer taskId){
        return taskService.info(taskId);
    }

    @GetMapping("/{id}/problem")
    public Result taskProblem(@PathVariable("id") Integer taskId,Integer order){
        return taskService.problem(taskId,order);
    }

    @GetMapping("/{id}/rank")
    public Result taskRank(@PathVariable("id") Integer taskId){
        return taskService.rank(taskId);
    }

    @GetMapping("/{id}/issue")
    public Result taskIssue(@PathVariable("id") Integer taskId){
        return taskService.getIssue(taskId);
    }

    @PostMapping("/{id}/issue")
    public Result createIssue(HttpServletRequest request, IssueDto issueDto, @PathVariable("id") Integer taskId){
        return taskService.createIssue(issueDto,taskId,getUserIdFromRequest(request));
    }

    @GetMapping("/{id}/question/reply")
    public Result getQuestion1(@PathVariable("id") Integer taskId){
        return taskService.question1(taskId);
    }

    @GetMapping("/{id}/question/unreply")
    public Result getQuestion2(@PathVariable("id") Integer taskId){
        return taskService.question2(taskId);
    }

    @PostMapping("/{id}/question")
    public Result answer(HttpServletRequest request,@PathVariable("id") Integer questionId, String reply){
        return taskService.answer(questionId,getUserIdFromRequest(request),reply);
    }

    private static Integer getUserIdFromRequest(HttpServletRequest request) {
//        String token = request.getHeader("authorization");
//        return (Integer) JwtUtil.decode(token).get("id");
        return 1;
    }
}