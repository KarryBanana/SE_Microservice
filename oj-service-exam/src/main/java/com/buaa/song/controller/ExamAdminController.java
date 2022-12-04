package com.buaa.song.controller;

import com.alibaba.fastjson.JSONObject;
import com.buaa.song.dto.ExamDto;
import com.buaa.song.dto.PageAndSortDto;
import com.buaa.song.result.Result;
import com.buaa.song.service.ExamService;
import com.buaa.song.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @FileName: ExamAdminController
 * @author: ProgrammerZhao
 * @Date: 2021/5/18
 * @Description:
 */
@RestController
@RequestMapping("/admin/contest")
public class ExamAdminController {

    @Autowired
    private ExamService examService;

    private static Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        return (Integer) JwtUtil.decode(token).get("id");
    }

    @GetMapping("/list")
    public Result list(HttpServletRequest request, PageAndSortDto page){
        return examService.list(getUserIdFromRequest(request), page);
    }

    @GetMapping("/{id}/info")
    public Result examInfo(@PathVariable("id") Integer examId){
        return examService.getInfo(examId);
    }

    @PostMapping("/create")
    public Result create(HttpServletRequest request, @RequestBody ExamDto examDto){
        return examService.createExam(examDto,getUserIdFromRequest(request));
    }

    @GetMapping("/{id}/edit")
    public Result getExamEdit(@PathVariable("id") Integer examId){
        return examService.getExamEdit(examId);
    }

    @PutMapping("/edit")
    public Result editExam(HttpServletRequest request, @RequestBody ExamDto examDto){
        return examService.editExam(examDto,getUserIdFromRequest(request));
    }

    @GetMapping("/{id}/problem")
    public Result problem(@PathVariable("id") Integer examId){
        return examService.getProblems(examId);
    }

    @GetMapping("/{id}/rank")
    public Result rank(@PathVariable("id") Integer examId){
        return examService.getRank(examId);
    }

    @GetMapping("/{id}/submission")
    public Result getSubmission(HttpServletRequest request, @PathVariable("id") Integer examId) {
        return examService.getAllSubmission();
    }

    @DeleteMapping("/{id}")
    public Result deleteExam(@PathVariable("id") Integer examId) {
        return examService.deleteExam(examId);
    }

    @PostMapping("/{eid}/class/{cid}/access")
    public Result setExamAccess(@PathVariable("cid") Integer classId, @PathVariable("eid") Integer examId,
                                @RequestBody JSONObject access) {
        Integer type = (Integer) access.get("access");
        return examService.setExamAccess(classId, examId, type);
    }
}