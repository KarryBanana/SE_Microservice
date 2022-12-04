package com.buaa.song.service;

import com.buaa.song.dto.PageAndSortDto;
import com.buaa.song.entity.Class;
import com.buaa.song.result.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface ClassAdminService {
    Result findClassByUserId(Integer id);

    Result addClass(Integer userId, Class c);

    Result deleteClass(Integer userId, Integer classId);

    Result findClassById(Integer classId);

    Result setProblemAccess(Integer classId, Integer problemId, Integer access);

    Result setUserAccess(Integer classId, Integer userId, Integer access);

    Result getAdminClassInfo(Integer classId);

    Result updateClassInfo(Integer classId, Class clazz);

    Result getAdminProblemList(Integer classId, PageAndSortDto page);

    Result getAdminClassExams(Integer classId, PageAndSortDto page);

    Result getClassMember(Integer classId);

    Result getUserInfo(Integer cid, Integer uid);

    Result setUserToAdmin(Integer cid, Integer uid);

    Result setUserToMember(Integer cid, Integer uid);

    Result deleteUser(Integer cid, Integer uid);

    Result addUserFromExcel(MultipartFile file, Integer classId, String suffix, Integer pattern, String password);

    Result addUser(Integer classId, String way, String content);

    Result getApplyUsers(Integer classId);

    Result dealApplyUser(Integer applyId, Integer isAgree, Integer dealPerson);

    String downloadExcelFile(String fileName, HttpServletResponse response);
}
