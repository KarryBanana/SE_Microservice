package com.buaa.song.controller;

import com.alibaba.fastjson.JSONObject;
import com.buaa.song.dto.SubmissionProblemDto;
import com.buaa.song.result.Result;
import com.buaa.song.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.buaa.song.utils.RequestUtil.getUserIdFromRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @FileName: SubmissionController
 * @author: ProgrammerZhao
 * @Date: 2021/5/21
 * @Description:
 */
@RestController
@RequestMapping("/submission")
@CrossOrigin(origins = {"*"})
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @GetMapping("/list")
    public Result list(){
        return submissionService.list();
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        return submissionService.findSubmissionById(id);
    }

    @PostMapping("/problem/{id}/submit")
    public Result submitProblemCode(HttpServletRequest request, @RequestBody SubmissionProblemDto submissionProblemDto) {
        return submissionService.submitProblem(getUserIdFromRequest(request), submissionProblemDto);
    }

    @GetMapping("/class/{cid}/problem/{pid}/list")
    @ResponseBody
    public Result ProblemSubList(HttpServletRequest request, @PathVariable("cid") Integer cid,
                                                             @PathVariable("pid") Integer pid) {
        return submissionService.problemSubList(getUserIdFromRequest(request), cid, pid);
    }

    @PutMapping("/result")
    public Result updateSubResult(@RequestBody JSONObject data) {
        return submissionService.updateSubResult(data);
    }
}