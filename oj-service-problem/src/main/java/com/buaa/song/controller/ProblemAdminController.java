package com.buaa.song.controller;

import com.buaa.song.dto.PageAndSortDto;
import com.buaa.song.dto.ProblemDto;
import com.buaa.song.dto.ProblemUpdateDto;
import com.buaa.song.result.Result;
import com.buaa.song.service.ProblemAdminService;
import com.buaa.song.utils.JwtUtil;
import com.buaa.song.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.buaa.song.utils.RequestUtil.getUserIdFromRequest;

/**
 * @FileName: ProblemController
 * @author: ProgrammerZhao
 * @Date: 2020/11/16
 * @Description:
 */

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/admin/problem")
public class ProblemAdminController {

    @Autowired
    private ProblemAdminService problemAdminService;

    @PostMapping("/create")
    public Result createProblem(HttpServletRequest request, ProblemDto problemDto) {
        Integer userId = getUserIdFromRequest(request);
        return problemAdminService.createProblem(userId, problemDto);
    }

    @GetMapping("/list")
    public Result problemList(HttpServletRequest request, PageAndSortDto page) {
        return problemAdminService.getProblemList(getUserIdFromRequest(request), page);
    }

    @GetMapping("/pagenum")
    public Result getPageNum(HttpServletRequest request, PageAndSortDto page) {
        return problemAdminService.getPageNumber(getUserIdFromRequest(request), page);
    }

    @GetMapping("/{id}/info")
    public Result getProblemInfo(@PathVariable("id") Integer problemId) {
        return problemAdminService.getProblemInfo(problemId);
    }

    @GetMapping("/{id}/edit")
    public Result getProblemEdit(@PathVariable("id") Integer problemId) {
        return problemAdminService.getProblemEdit(problemId);
    }

    @PutMapping("/{id}/edit")
    public Result updateProblem(@PathVariable("id") Integer problemId, ProblemUpdateDto updateDto) {
        return problemAdminService.updateProblem(problemId, updateDto);
    }

    @DeleteMapping("/{id}")
    public Result deleteProblem(@PathVariable("id") Integer problemId) {
        return problemAdminService.deleteProblem(problemId);
    }

    @GetMapping("/{id}/download")
    public String downloadTestData(@PathVariable("id") Integer problemId, HttpServletResponse response) {
        return problemAdminService.download(problemId, response);
    }

    @GetMapping("/search")
    public Result search(HttpServletRequest request, @RequestParam("way") Integer searchWay,
                         @RequestParam("value") String value, PageAndSortDto page) {
        return problemAdminService.searchProblem(getUserIdFromRequest(request), searchWay, value, page);
    }

    @PostMapping("/{id}/share")
    public Result shareProblem(HttpServletRequest request, @PathVariable("id") Integer problemId,
                               List<Integer> userIds) {
        return problemAdminService.shareProblem(problemId, userIds, getUserIdFromRequest(request));
    }

    @PostMapping("/{id}/course")
    public Result addProblemToCourse(HttpServletRequest request, @PathVariable("id") Integer problemId,
                                     List<Integer> courseIds) {
        return problemAdminService.addProblemToCourse(problemId, courseIds, getUserIdFromRequest(request));
    }

    @GetMapping("/list/task")
    public Result problemListForTask(@RequestParam(name = "courseId",required = false) Integer courseId){
        return problemAdminService.problemListForTask(courseId);
    }
}