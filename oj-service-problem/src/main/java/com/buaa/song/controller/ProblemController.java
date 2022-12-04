package com.buaa.song.controller;

import com.alibaba.fastjson.JSONObject;
import com.buaa.song.dto.PageAndSortDto;
import com.buaa.song.result.Result;
import com.buaa.song.service.ProblemAdminService;
import com.buaa.song.service.ProblemService;
import com.buaa.song.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;

/**
 * @FileName: ProblemController
 * @author: ProgrammerZhao
 * @Date: 2021/10/28
 * @Description:
 */

@RestController
@RequestMapping("/problem")
@CrossOrigin(origins = {"*"})
public class ProblemController {

    @Autowired
    private ProblemAdminService problemAdminService;

    @Autowired
    private ProblemService problemService;


    private static Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        if(token == null){
            return null;
        }
        return (Integer) JwtUtil.decode(token).get("id");
    }


    @GetMapping("/{id}/info")
    public Result getProblemInfo(@PathVariable("id") Integer problemId) {
        // 和管理端接口相同
        return problemAdminService.getProblemInfo(problemId);
    }

    @GetMapping("/{id}/lang")
    public Result getLangByProblemId(@PathVariable("id") Integer problemId) {
        return problemService.getLangByProblemId(problemId);
    }

    @GetMapping("/{id}/submission")
    public Result getSubmission(HttpServletRequest request, @PathVariable("id") Integer problemId){
        return null;
    }

    @GetMapping("/list")
    public Result publicProblemList(HttpServletRequest request, PageAndSortDto page) {
        return problemService.getPublicProblemList(getUserIdFromRequest(request), page);
    }

    @PostMapping("/collect")
    // 这里的jsonObject用org.json会接收不到
    public Result collectProblem(HttpServletRequest request, @RequestBody JSONObject data) {
        System.out.println(data);
        Integer problem_id = (Integer) data.get("problemId");
        return problemService.collectProblem(getUserIdFromRequest(request), problem_id);
    }

    @PostMapping("/cancelCollect")
    public Result cancelCollectProblem(HttpServletRequest request, @RequestBody JSONObject data) {
        System.out.println(data);
        Integer problem_id = (Integer) data.get("problemId");
        return problemService.cancelCollectProblem(getUserIdFromRequest(request), problem_id);
    }

    @GetMapping("/collect/list")
    public Result showCollectProblem(HttpServletRequest request, PageAndSortDto page) {
        return problemService.showCollectProblem(getUserIdFromRequest(request), page);
    }

    @GetMapping("/search")
    public Result searchProblem(HttpServletRequest request, @RequestParam Integer type, @RequestParam Object content,
                                PageAndSortDto page) {
        if (1 == type) { // 题目名称搜索
            // 这里使用赵博学长写好的 elasticSearch
            return problemService.searchProblemByTitle(getUserIdFromRequest(request), (String) content, page);
        } else if (2 == type) { // id精确搜索
            Integer problemId = Integer.parseInt(String.valueOf(content));
            return problemService.searchProblemById(getUserIdFromRequest(request), problemId);

        } else if (3 == type) { // 作者模糊搜索
            return problemService.searchProblemByAuthor(getUserIdFromRequest(request), String.valueOf(content), page);

        } else if (4 == type) { // 通过标签数组筛选
            if (content.getClass().isArray()) {
                int len = Array.getLength(content);
                String[] tags = new String[len];
                for (int i = 0; i < len; ++i) {
                    tags[i] = (String) Array.get(content, i);
                    System.out.println(tags[i]);
                }
                return Result.success("标签数组传递成功");
            }
            return Result.fail(401, "参数非数组");

        } else {
            return Result.fail(400, "不合法的搜索方式");
        }
    }

    @GetMapping("/accept/ranklist")
    public Result getAcceptRankList(HttpServletRequest request) {
        return problemService.getAcceptRankList();
    }

    @GetMapping("/question-answer")
    public Result getQuestionAnswer(HttpServletRequest request) {
        return problemService.getQuestionAnswer();
    }

    @GetMapping("/pagenum")
    public Result getPageNum(HttpServletRequest request, PageAndSortDto page) {
        // 这里使用赵博学长写好的
        return problemAdminService.getPageNumber(getUserIdFromRequest(request), page);
    }

    @GetMapping("/search_zb")
    public Result search(HttpServletRequest request, @RequestParam("way") Integer searchWay,
                         @RequestParam("value") String value, PageAndSortDto page) {
        // 这里使用赵博学长写好的
        return problemAdminService.searchProblem(getUserIdFromRequest(request), searchWay, value, page);
    }
}
