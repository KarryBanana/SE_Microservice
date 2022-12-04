package com.buaa.song.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.buaa.song.dto.ExamQuestionDto;
import com.buaa.song.dto.ExamSubmitCodeDto;
import com.buaa.song.dto.PageAndSortDto;
import com.buaa.song.result.Result;
import com.buaa.song.service.ExamService;
import com.buaa.song.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;


/**
 * @FileName: ExamController
 * @author: ProgrammerZhao
 * @Date: 2021/4/7
 * @Description:
 */
@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/contest")
public class ExamController {

    @Autowired
    private ExamService examService;

    private static Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        if (token == null)
            return null;
        try {
            JwtUtil.isVerify(token);
        } catch (JWTVerificationException e) {
            return null;
        }
        return (Integer) JwtUtil.decode(token).get("id");
    }

    @GetMapping("/{id}/info")
    public Result getExamInfo(@PathVariable("id") Integer examId) {
        return examService.getInfo(examId);
    }

    @GetMapping("/{id}/problem")
    public Result getProblems(@PathVariable("id") Integer examId) {
        return examService.getProblems(examId);
    }

    @GetMapping("/{id}/issue")
    public Result getIssue(@PathVariable("id") Integer examId) {
        return examService.getIssues(examId);
    }

    @GetMapping("/{id}/question")
    public Result getQuestion(@PathVariable("id") Integer examId) {
        return examService.getQuestion(examId);
    }

    @GetMapping("/{id}/rank")
    public Result getRank(@PathVariable("id") Integer examId) {
        return examService.getRank(examId);
    }

    @GetMapping("/{id}/submission")
    public Result getSubmission(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") Integer examId) throws IOException {
        Integer userId = getUserIdFromRequest(request);
        return examService.getSubmission(examId, userId);
    }

    // 学生端比赛列表需要修改
    @GetMapping("/list")
    public Result getExamList(HttpServletRequest request, PageAndSortDto page) {
        return examService.list(getUserIdFromRequest(request), page);
    }

    @GetMapping("/list/pagenum")
    public Result getExamListPageNum(HttpServletRequest request, Integer limit) {
        return examService.getListPageNum(getUserIdFromRequest(request), limit);
    }

    @GetMapping("/{id}/search")
    public Result getClassExams(HttpServletRequest request, @PathVariable("id") Integer classId, PageAndSortDto page) {
        return examService.getClassExam(getUserIdFromRequest(request), classId, page);
    }

    @GetMapping("/server-time")
    public Result getServerTime() {
        return Result.success(new Date(System.currentTimeMillis()));
    }

    @PostMapping("/{id}/question")
    public Result askQuestion(HttpServletRequest request, HttpServletResponse response,
                              @PathVariable("id") Integer examId,
                              @RequestBody ExamQuestionDto dto) throws IOException {
        Integer userId = getUserIdFromRequest(request);
        if (userId == null){
            response.sendError(401);
            return Result.fail(401,"未登录或登录过期");
        }
        Integer problemId = dto.getProblemId();
        String content = dto.getContent();
        return examService.askQuestion(content, examId, problemId, userId);
    }

    @PostMapping("/{id}/submit")
    public Result submitCode(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") Integer examId, @RequestBody
            ExamSubmitCodeDto dto) throws IOException {
        Integer userId = getUserIdFromRequest(request);
        if (userId == null){
            response.sendError(401);
            return Result.fail(401,"未登录或登录过期");
        }
        Integer problem = dto.getProblemId();
        Integer language = dto.getLanguage();
        String code = dto.getCode();
        return examService.submit(userId, examId, problem, language, code);
    }

    @GetMapping("/submission/{id}/code")
    public Result getSubmissionCode(@PathVariable("id") Integer id) {
        return examService.getCode(id);
    }

    @GetMapping("/test")
    public void test(){
        examService.test();
    }
}
