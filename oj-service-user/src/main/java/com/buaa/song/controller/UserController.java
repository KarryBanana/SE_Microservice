package com.buaa.song.controller;

import com.alibaba.fastjson.JSONObject;
import com.buaa.song.dto.*;
import com.buaa.song.result.Result;
import com.buaa.song.service.UserService;
import com.buaa.song.utils.JwtUtil;
import com.netflix.ribbon.proxy.annotation.Http;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @FileName: UserController
 * @author: ProgrammerZhao
 * @Date: 2020/10/29
 * @Description:
 */

@RestController
@CrossOrigin(origins = {"*"})
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody UserLoginDto dto){
        return userService.login(dto);
    }

    @PostMapping("/logout")
    public Result logout(HttpServletRequest request){
        String token = request.getHeader("authorization");
        return userService.logout(token);
    }

    @PostMapping("/register")
    public Result register(@RequestBody RegisterDto dto){
        return userService.register(dto);
    }

    @GetMapping("/isUsernameExist")
    public Result isUsernameExist(@RequestParam String username){
        return userService.isUsernameExist(username);
    }

    @GetMapping("/isSuperAdmin")
    public Result isSuperAdmin(HttpServletRequest request) {
        return userService.isSuperAdmin(getUserIdFromRequest(request));
    }

    @PostMapping("/user/editVisit")
    public Result editVisit(HttpServletRequest request, @RequestBody JSONObject privacy) {
        Integer private_level = (Integer) privacy.get("privacy");
//        System.out.println("privacy level is " + privacy);
        return userService.editVisit(getUserIdFromRequest(request), private_level);
    }

    @GetMapping("/user/{id}")
    public Result findById(@PathVariable Integer id){
        return userService.findUserById(id);
    }

    @PostMapping("/user/code")
    public Result sendCode(@RequestBody String jsonString){
        String email = (String) JSONObject.parseObject(jsonString).get("email");
        return userService.sendCode(email);
    }

    @PostMapping("/user/verf")
    public Result verifcateCode(@RequestBody VerificateCodeDto dto){
        return userService.verificateCode(dto);
    }

    @PostMapping("/user/resetPwd")
    public Result resetPassword(HttpServletRequest request, @RequestBody ResetPasswordDto dto){
        String token = request.getHeader("authorization");
        return userService.resetPassword(token,dto);
    }

    @GetMapping("/user/info")
    public Result getUserInfoByToken(HttpServletRequest request){
        return userService.getInfo(getUserIdFromRequest(request));
    }

    @GetMapping("/user/{id}/info")
    public Result getUserInfoById(@PathVariable Integer id) {
        return userService.getInfo(id);
    }

    @GetMapping("/user/{id}/privacy")
    public Result getUserPrivacy(@PathVariable("id") Integer id) {
        return userService.getUserPrivacy(id);
    }

    @GetMapping("/user/edit")
    public Result getUserEdit(HttpServletRequest request){
        return userService.getInfo(getUserIdFromRequest(request));
    }

    @PutMapping("/user/edit")
    public Result editUser(HttpServletRequest request,@RequestBody EditInfoDto dto){
        return userService.editInfo(getUserIdFromRequest(request),dto);
    }

    @PostMapping("/user/updatepwd")
    public Result updatePassword(HttpServletRequest request,@RequestBody UpdatePasswordDto dto){
        return userService.updatePassword(getUserIdFromRequest(request),dto);
    }

    @GetMapping("/user/submission")
    public Result getAnnualSubmissionByToken(HttpServletRequest request) {
        return userService.getAnnualSubmission(getUserIdFromRequest(request));
    }

    @GetMapping("/user/{id}/submission")
    public Result getAnnualSubmissionById(@PathVariable Integer id) {
        return userService.getAnnualSubmission(id);
    }

    // 用户所有提交情况统计
    @GetMapping("/user/sub-type")
    public Result getAllSubmissionType(HttpServletRequest request) {
        return userService.getAllSubmissionType(getUserIdFromRequest(request));
    }

    // 本周的提交情况
    @GetMapping("/user/sub-week")
    public Result getWeekSubmission(HttpServletRequest request) {
        return userService.getWeekSubmission(getUserIdFromRequest(request));
    }

    // 最近8次提交记录
    @GetMapping("/user/sub-latest")
    public Result getLatestSubmission(HttpServletRequest request) {
        return userService.getLatestSubmission(getUserIdFromRequest(request));
    }

    // 获取学校列表
    @GetMapping("/user/schools")
    public Result getUserSchools() {
        return userService.getUserSchools();
    }



    private static Integer getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        return (Integer) JwtUtil.decode(token).get("id");
    }
}
