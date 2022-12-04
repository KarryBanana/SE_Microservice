package com.buaa.song.controller;

import com.alibaba.fastjson.JSONObject;
import com.buaa.song.dto.DealApplyDto;
import com.buaa.song.dto.PageAndSortDto;
import com.buaa.song.entity.Class;
import com.buaa.song.result.Result;
import com.buaa.song.service.ClassAdminService;
import com.buaa.song.service.ClassService;
import com.buaa.song.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @FileName: ClassController
 * @author: ProgrammerZhao
 * @Date: 2021/2/19
 * @Description:
 */
@RestController
@RequestMapping("/admin/class")
@CrossOrigin
public class ClassAdminController {

    @Autowired
    private ClassService classService;

    @Autowired
    private ClassAdminService classAdminService;

    @GetMapping("/index")
    public Result index(HttpServletRequest request){
        Integer id = getUserIdFromRequest(request);
        return classAdminService.findClassByUserId(id);
    }

    @GetMapping("/{id}/info")
    public Result info(@PathVariable("id") Integer classId){
        return classAdminService.getAdminClassInfo(classId);
    }

    @PutMapping("/{id}/info")
    public Result updateInfo(@PathVariable("id") Integer classId, @RequestBody Class clazz){
        return classAdminService.updateClassInfo(classId,clazz);
    }

    @PostMapping("/add")
    public Result addClass(HttpServletRequest request, @RequestBody Class c) {
        return classAdminService.addClass(getUserIdFromRequest(request), c);
    }

    @DeleteMapping("/{id}")
    public Result deleteClass(HttpServletRequest request, @PathVariable("id") Integer classId) {
        return classAdminService.deleteClass(getUserIdFromRequest(request), classId);
    }


    @PostMapping("{cid}/problem/{pid}/access")
    public Result setProblemAccess(@PathVariable("cid") Integer classId, @PathVariable("pid") Integer problemId,
                                   @RequestBody JSONObject data) {
        Integer access = (Integer) data.get("access");
        return classAdminService.setProblemAccess(classId, problemId, access);
    }

    @PostMapping("{cid}/user/{id}/access")
    public Result setUserAccess(@PathVariable("cid") Integer classId, @PathVariable("id") Integer userId,
                                @RequestBody JSONObject data ) {
        Integer access = (Integer) data.get("access");
        return classAdminService.setUserAccess(classId, userId, access);
    }

    @GetMapping("/{id}/member")
    public Result getClassMember(@PathVariable("id") Integer classId){
        return classAdminService.getClassMember(classId);
    }

    @GetMapping("/{id}/user/{uid}/info")
    public Result getUserInfo(@PathVariable("id") Integer cid, @PathVariable("uid") Integer uid){
        return classAdminService.getUserInfo(cid,uid);
    }

    @PostMapping("/{id}/user")
    public Result addUser(@PathVariable("id") Integer classId, @RequestParam String way, @RequestParam String content){
        return classAdminService.addUser(classId, way, content);
    }

    @GetMapping("/{id}/user/apply")
    public Result getApplyUserList(@PathVariable("id") Integer classId){
        return classAdminService.getApplyUsers(classId);
    }

    @PostMapping("/user/apply")
    public Result dealApplyUser(HttpServletRequest request, @RequestBody DealApplyDto dealApplyDto){
        Integer dealPerson = getUserIdFromRequest(request);

        Integer applyId = dealApplyDto.getApplyId();
        Integer isAgree = dealApplyDto.getIsAgree();

        return classAdminService.dealApplyUser(applyId,isAgree,dealPerson);
    }

    @PutMapping("/{id}/user/{uid}/admin")
    public Result setUserToAdmin(@PathVariable("id") Integer cid, @PathVariable("uid") Integer uid){
        return classAdminService.setUserToAdmin(cid,uid);
    }

    @PutMapping("/{id}/user/{uid}/member")
    public Result setUserToMember(@PathVariable("id") Integer cid, @PathVariable("uid") Integer uid){
        return classAdminService.setUserToMember(cid,uid);
    }

    @DeleteMapping("/{id}/user/{uid}")
    public Result deleteUserFromClass(@PathVariable("id") Integer cid, @PathVariable("uid") Integer uid){
        return classAdminService.deleteUser(cid,uid);
    }

    @PostMapping("/{id}/user/excel")
    public Result addUserFromExcel(@RequestParam MultipartFile file, @PathVariable("id") Integer classId, @RequestParam String suffix,
                                   @RequestParam Integer pattern, @RequestParam(required = false) String password){
        return classAdminService.addUserFromExcel(file, classId, suffix, pattern, password);
    }

    @GetMapping("/{id}/user/excel/download")
    public String downloadExcel(@PathVariable("id") Integer classId, @RequestParam("file") String fileName, HttpServletResponse response){
        return classAdminService.downloadExcelFile(fileName,response);
    }

    @GetMapping("/{id}/problem")
    public Result problemList(@PathVariable("id") Integer classId, PageAndSortDto page){
        return classAdminService.getAdminProblemList(classId, page);
    }

    @GetMapping("/{id}/contest")
    public Result examList(@PathVariable("id") Integer classId, PageAndSortDto page) {
        return classAdminService.getAdminClassExams(classId, page);
    }

    private static Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        return (Integer) JwtUtil.decode(token).get("id");
    }

}