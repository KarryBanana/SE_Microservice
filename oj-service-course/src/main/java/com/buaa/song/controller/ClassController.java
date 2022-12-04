package com.buaa.song.controller;

import com.buaa.song.dto.ApplyClassDto;
import com.buaa.song.dto.PageAndSortDto;
import com.buaa.song.result.Result;
import com.buaa.song.service.ClassService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import static com.buaa.song.utils.RequestUtil.getUserIdFromRequest;

/**
 * @FileName: ClassController
 * @author: ProgrammerZhao
 * @Date: 2021/10/29
 * @Description:
 */
@RestController
@RequestMapping("/class")
@CrossOrigin
public class ClassController {

    @Autowired
    private ClassService classService;

    @GetMapping("/{id}/info")
    public Result getClassInfo(@PathVariable Integer id) {
        return classService.getClassInfo(id);
    }

    @GetMapping("/{id}")
    public Result getClassById(@PathVariable Integer id){
        return classService.findClassById(id);
    }

    @GetMapping("/list")
    public Result getClassList(HttpServletRequest request) {
        return classService.getClassList(getUserIdFromRequest(request));
    }

    @GetMapping("/{id}/course")
    public Result getClassCourse(@PathVariable("id") Integer classId) {
        return classService.getClassCourse(classId);
    }

    @GetMapping("/{id}/member")
    public Result getClassMember(@PathVariable Integer id) {
        return classService.getClassMember(id);
    }

    @GetMapping("/{id}/problem")
    public Result getClassProblem(HttpServletRequest request, @PathVariable Integer id, PageAndSortDto page) {
        return classService.getClassProblemList(getUserIdFromRequest(request), id, page);
    }

    @GetMapping("/{id}/problem/pagenum")
    public Result getClassProblemPageNum(HttpServletRequest request, @PathVariable Integer id, @RequestParam Integer limit) {
        return classService.getClassProblemPageNum(getUserIdFromRequest(request), id, limit);
    }

    @GetMapping("/{id}/contest")
    public Result getClassExam(HttpServletRequest request, @PathVariable("id") Integer classId, PageAndSortDto page) {
        return classService.getClassExam(getUserIdFromRequest(request), classId, page);
    }

    @GetMapping("/{id}/contest/pagenum")
    public Result getClassExamPageNum(HttpServletRequest request, @PathVariable Integer id, @RequestParam Integer limit) {
        return classService.getClassExamPageNum(getUserIdFromRequest(request), id, limit);
    }

    @PostMapping("/apply")
    public Result applyClass(HttpServletRequest request, @RequestBody ApplyClassDto applyClassDto) {
        return classService.applyClass(getUserIdFromRequest(request), applyClassDto);
    }

    @GetMapping("/{id}/user/{uid}/isClassMember")
    public Result isClassMember(@PathVariable("id") Integer classId, @PathVariable("uid") Integer userId) {
        return classService.isClassMember(userId, classId);
    }

    /*
    赵博学长代码
    @GetMapping("/{id}/info")
    public Result info(@PathVariable("id") Integer classId) {
        return classService.getClassInfo(classId);
    }

    @GetMapping("/{id}/member")
    public Result classMember(@PathVariable("id") Integer classId, @RequestBody PageAndSortDto page) {
        return classService.getClassUser(classId, page);
    }

    @GetMapping("/{id}/problem")
    public Result classProblem(HttpServletRequest request, @PathVariable("id") Integer classId,
                               @RequestBody PageAndSortDto page) {
        Integer userId = getUserIdFromRequest(request);
        return classService.problemList(classId,userId,page);
    }

    @GetMapping("/{id}/contest")
    public Result classContest(@PathVariable("id") Integer classId,@RequestBody PageAndSortDto page){
        return classService.getClassContest(classId,page);
    }
    */
}