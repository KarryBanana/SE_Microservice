package com.buaa.song.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.buaa.song.dao.LanguageDao;
import com.buaa.song.dao.ProblemDao;
import com.buaa.song.dao.TagDao;
import com.buaa.song.dao.TestDataDao;
import com.buaa.song.dto.PageAndSortDto;
import com.buaa.song.dto.ProblemDto;
import com.buaa.song.dto.ProblemUpdateDto;
import com.buaa.song.dto.ProblemUpdateDto.UpdateTestData;
import com.buaa.song.dto.TestDataDto;
import com.buaa.song.entity.*;
import com.buaa.song.exception.*;
import com.buaa.song.result.Result;
import com.buaa.song.service.ProblemAdminService;
import com.buaa.song.utils.FileUtil;
import com.buaa.song.utils.JsonUtil;
import com.buaa.song.utils.RestUtil;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @FileName: ProblemServiceImpl
 * @author: ProgrammerZhao
 * @Date: 2020/11/16
 * @Description:
 */

@Service
@RefreshScope
public class ProblemAdminServiceImpl implements ProblemAdminService {

    private static final Logger logger = LoggerFactory.getLogger(ProblemAdminServiceImpl.class);
    private static final String userServiceUrl = "http://oj-service-user";

    @Autowired
    private ProblemDao problemDao;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private LanguageDao languageDao;
    @Autowired
    private TestDataDao testDataDao;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${file.test}")
    private String testFilePath;


    // 以下为管理端接口
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result createProblem(Integer userId, ProblemDto problemDto) {
        String dir = null;
        try {
            checkUserExist(userId);
            //将dto转为实体并保存到数据库
            Problem problem = problemDto.transferToProblem();
            problem.setCreatorId(userId);
            Problem savedProblem = problemDao.save(problem);
            Integer problemId = savedProblem.getId();

            // 保存题目的tags和languages
            saveTagAndLanguage(problemDto, problemId);

            //保存测试数据
            dir = testFilePath + "/" + problemId;
            saveTestDatas(problemDto, problemId, dir);

            // 保存特判代码文件
            if(problemDto.getIsSpecialJudge().equals(1)) {
                MultipartFile specialJudgeCodeFile = problemDto.getSpecialJudge().getSpecialJudgeCode();
                FileUtil.createFileByMultipartFile(specialJudgeCodeFile, dir);
            }

            saveSettingToJsonFile(savedProblem.getSetting(), dir);

            return Result.success("创建题目成功");
        } catch (CreateDirException e) {
            logger.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(500);
        } catch (IOException e) {
            logger.error("IO异常");
            FileUtil.delete(dir);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(500);
        } catch (UserNotFindException | TagNotFindException | LanguageNotFindException e) {
            logger.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(400, e.getMessage());
        }
    }
    private void saveTagAndLanguage(ProblemDto problemDto, Integer problemId) throws TagNotFindException, LanguageNotFindException{
        //保存题目和标签的关系
        for (Integer tagId : problemDto.getTags()) {
            Optional<Tag> optionalTag = tagDao.findById(tagId);
            if (optionalTag.isPresent()) {
                problemDao.saveProblemTag(problemId, tagId);
            } else {
                throw new TagNotFindException(tagId);
            }
        }
        //保存题目与语言的关系
        for (Integer languageId : problemDto.getLanguages()) {
            Optional<Language> optionalLanguage = languageDao.findById(languageId);
            if (optionalLanguage.isPresent()) {
                problemDao.saveProblemLanguage(problemId, languageId);
            } else {
                throw new LanguageNotFindException(languageId);
            }
        }
    }

    private void saveTestDatas(ProblemDto problemDto, Integer problemId, String dir) throws CreateDirException, IOException{
        boolean b = FileUtil.createDir(dir);
        if (!b) {
            throw new CreateDirException(dir);
        }
        List<TestDataDto> testDatas = problemDto.getTestdatas();
        for (TestDataDto testData : testDatas) {
            MultipartFile input = testData.getInput();
            MultipartFile output = testData.getOutput();
            TestData data = new TestData(input.getOriginalFilename(), output.getOriginalFilename(), testData.getWeight(), problemId);
            testDataDao.save(data);
            FileUtil.createFileByMultipartFile(input, dir);
            FileUtil.createFileByMultipartFile(output, dir);
        }
    }

    private void saveSettingToJsonFile(String setting, String dir) throws IOException{
        try {
            JSONObject jsonObject = JSONObject.parseObject(setting);
            String formatSetting = JSON.toJSONString(jsonObject);
            // System.out.println(formatSetting);
            File jsonFile = new File(dir + "/setting.json");
            Writer writer = new OutputStreamWriter(new FileOutputStream(jsonFile), "UTF-8");
            writer.write(formatSetting);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.error("保存题目设置IO异常");
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public Result getProblemList(Integer userId, PageAndSortDto page) {
        try {
            Result result = checkUserExist(userId);
            User user = (User) result.getData();
            List<Integer> problemIds = null;
            Integer start = page.getPage();
            Integer limit = page.getLimit();
            if (user.getRole().equals(2)) {
                //普通管理员只能查看部分题目
                problemIds = problemDao.findMyProblemIds(userId, (start - 1) * limit, limit);
            } else {
                //超级管理员可以查看全部题目
                problemIds = problemDao.findAllProblemIds((start - 1) * limit, limit);
            }
            List<Map<String,Object>> problems = new LinkedList<>();
            for(Integer problemId : problemIds){
                Map<String, Object> problem = new HashMap<>(problemDao.findProblemById(problemId));
                problem.putAll(problemDao.getUserNum(problemId));
                problems.add(problem);
            }
            return Result.success(problems);
        } catch (UserNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }
    }

    @Override
    public Result getPageNumber(Integer userId, PageAndSortDto page) {
        try {
            Result result = checkUserExist(userId);
            User user = (User) result.getData();
            Integer limit = page.getLimit();
            Integer count;
            if (user.getRole().equals(2)) {
                count = problemDao.getProblemCount(userId);
            } else {
                count = problemDao.getProblemCount();
            }
            Integer pageNumber;
            if (count % limit == 0) {
                pageNumber = count / limit;
            } else {
                pageNumber = count / limit + 1;
            }
            return Result.success(pageNumber);
        } catch (UserNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }
    }

    @Override
    public Result getProblemInfo(Integer problemId) {
        Optional<Problem> optionalProblem = problemDao.findById(problemId);
        try {
            if (!optionalProblem.isPresent()) {
                throw new ProblemNotFindException(problemId);
            }
            Map<String, Object> info = problemDao.getProblemInfo(problemId);
            return Result.success(info);
        } catch (ProblemNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }
    }

    @Override
    public Result searchProblem(Integer userId, Integer searchWay, String searchText, PageAndSortDto page) {
        System.out.println("i m in zb code!");
        Integer start = page.getPage();
        Integer limit = page.getLimit();

        SearchRequest searchRequest = new SearchRequest("oj5th_test_problem");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String searchField = null;
        if (searchWay.equals(1)) {
            //按照标题搜索
            searchField = "title";
        } else if (searchWay.equals(2)) {
            //按照内容搜索
            searchField = "content";
        }
        searchSourceBuilder.query(QueryBuilders.matchQuery(searchField, searchText));
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(10000);
        String[] includeFields = new String[]{"id"};
        searchSourceBuilder.fetchSource(includeFields, null);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits searchResult = searchResponse.getHits();
            SearchHit[] hits = searchResult.getHits();
            List<Integer> problemIds = new LinkedList<>();
            for(SearchHit hit : hits){
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Integer id = (Integer) sourceAsMap.get("id");
                System.out.println(id);
                problemIds.add(id);
            }
            Result result = RestUtil.get(restTemplate, userServiceUrl + "/user/" + userId, User.class);
            User user = (User) result.getData();
            Integer roleId = user.getRole();
            if(roleId.equals(2)){
                List<Map<String,Object>> problems = problemDao.findMyProblemsInSearch(problemIds,userId, (start - 1) * limit,
                        limit);
                return Result.success(problems);
            }else if (roleId.equals(3)){
                List<Map<String,Object>> problems = problemDao.findAllProblemsInSearch(problemIds, (start - 1) * limit,
                        limit);
                return Result.success(problems);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            return Result.fail(500,"IO异常");
        }
        return null;
    }

    @Override
    public Result getProblemEdit(Integer problemId) {
        Optional<Problem> optionalProblem = problemDao.findById(problemId);
        try {
            if (optionalProblem.isPresent()) {
                Map<String, Object> result = new HashMap<>();
                Problem problem = optionalProblem.get();
                result.put("problem", problem);
                String settingStr = problem.getSetting();
                Object setting = JsonUtil.getObjectFromJson(settingStr);
                result.put("setting", setting);
                List<Tag> tags = tagDao.findTagsByProblem(problemId);
                result.put("tags", tags);
                List<Language> languages = languageDao.findLanguagesByProblem(problemId);
                result.put("languages", languages);
                List<TestData> testData = testDataDao.findAllByProblemId(problemId);
                result.put("testdata", testData);
                return Result.success(result);
            } else {
                throw new ProblemNotFindException(problemId);
            }
        } catch (ProblemNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateProblem(Integer problemId, ProblemUpdateDto updateDto) {
        Optional<Problem> optionalProblem = problemDao.findById(problemId);
        try {
            if (optionalProblem.isPresent()) {
                //更新题目信息
                Problem problem = optionalProblem.get();
                updateDto.updateProblem(problem);
                problemDao.save(problem);
                //更新标签信息
                List<Integer> oldTags = tagDao.findTagIdsByProblem(problemId);
                List<Integer> newTags = updateDto.getTags();

                // 删除旧的当中不在新的里面的
                List<Integer> oldTagsNotInNewTags = new ArrayList<>(oldTags);
                oldTagsNotInNewTags.removeAll(newTags);
                for(Integer tag : oldTagsNotInNewTags){
                    problemDao.deleteProblemTag(problemId, tag);
                }

                // 添加新的当中不在旧的里面的
                List<Integer> newTagsNotInOldTags = new ArrayList<>(newTags);
                newTagsNotInOldTags.removeAll(oldTags);
                for(Integer tag : newTagsNotInOldTags){
                    Optional<Tag> optionalTag = tagDao.findById(tag);
                    if(optionalTag.isPresent()) {
                        problemDao.saveProblemTag(problemId, tag);
                    } else {
                        throw new TagNotFindException(tag);
                    }
                }

                // 更新语言信息
                List<Integer> oldLanguages = languageDao.findLanguageIdsByProblem(problemId);
                List<Integer> newLanguages = updateDto.getLanguages();

                List<Integer> oldLangNotInNewLang = new ArrayList<>(oldLanguages);
                oldLangNotInNewLang.removeAll(newLanguages);
                for(Integer lang : oldLangNotInNewLang) {
                    problemDao.deleteProblemLanguage(problemId, lang);
                }

                List<Integer> newLangNotInOldLang = new ArrayList<>(newLanguages);
                newLangNotInOldLang.removeAll(oldLanguages);
                for (Integer language : newLangNotInOldLang) {
                    Optional<Language> optionalLanguage = languageDao.findById(language);
                    if (optionalLanguage.isPresent()) {
                        problemDao.saveProblemLanguage(problemId, language);
                    } else {
                        throw new LanguageNotFindException(language);
                    }
                }

                // 更新测试数据
                for (UpdateTestData testdata : updateDto.getUpdateTestData()) {
                    Integer testdataId = testdata.getId();
                    Optional<TestData> optionalTestData = testDataDao.findById(testdataId);
                    if (optionalTestData.isPresent()) {
                        TestData testData = optionalTestData.get();
                        testData.setWeight(testdata.getWeight());
                        testDataDao.save(testData);
                    }
                }
                // 删除测试数据
                for (Integer testId : updateDto.getDeleteTestData()) {
                    Optional<TestData> optionalTest = testDataDao.findById(testId);
                    if (optionalTest.isPresent()) {
                        TestData testData = optionalTest.get();
                        String input = testData.getInput();
                        FileUtil.delete(testFilePath + "/" + problemId + "/" + input);
                        String output = testData.getOutput();
                        FileUtil.delete(testFilePath + "/" + problemId + "/" + output);
                        testDataDao.deleteById(testId);
                    }
                }
                //添加测试数据
                String dir = testFilePath + "/" + problemId;
                for (TestDataDto testData : updateDto.getTestdatas()) {
                    MultipartFile input = testData.getInput();
                    MultipartFile output = testData.getOutput();
                    TestData data = new TestData(input.getOriginalFilename(), output.getOriginalFilename(), testData.getWeight(), problemId);
                    testDataDao.save(data);
                    FileUtil.createFileByMultipartFile(input, dir);
                    FileUtil.createFileByMultipartFile(output, dir);
                }
                //同步评测数据到评测机

                return Result.success("修改成功");
            } else {
                throw new ProblemNotFindException(problemId);
            }
        } catch (ProblemNotFindException | TagNotFindException | LanguageNotFindException e) {
            logger.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(400, e.getMessage());
        }
        catch (IOException e) {
            logger.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(500, "IO错误");
        }
    }

    @Override
    public Result deleteProblem(Integer problemId) {
        Optional<Problem> optionalProblem = problemDao.findById(problemId);
        try {
            if (optionalProblem.isPresent()) {
                problemDao.deleteById(problemId);
                FileUtil.delete(testFilePath + "/" + problemId);
                return Result.success("删除成功");
            } else {
                throw new ProblemNotFindException(problemId);
            }
        } catch (ProblemNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }
    }

    @Override
    public String download(Integer problemId, HttpServletResponse response) {
        String dir = testFilePath + "/" + problemId;
        response.setContentType("application/zip");
        String fileName = problemId + ".zip";
        byte[] fileNameByte = fileName.getBytes(StandardCharsets.UTF_8);
        String fileNameEncode = new String(fileNameByte, 0, fileNameByte.length, StandardCharsets.ISO_8859_1);
        response.addHeader("Content-Disposition", "attachment;fileName=" + fileNameEncode);
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            FileUtil.getZip(dir, out);
            return "success";
        } catch (IOException e) {
            logger.error("IO错误");
            return "fail";
        }
    }

    @Override
    public Result shareProblem(Integer problemId, List<Integer> userIds, Integer createUser) {
        for (Integer userId : userIds) {
            Integer result = problemDao.insertIntoProblemShare(problemId, userId, createUser);
            if (!(result > 0)) {
                return Result.fail(400, "分享题目失败");
            }
        }
        return Result.success("分享题目成功");
    }

    @Override
    public Result problemListForTask(Integer courseId) {
        if(courseId == null){
            List<Map<String,Object>> problems = problemDao.findAllProblemsInTask();
            return Result.success(problems);
        }else {
            List<Map<String,Object>> problems = problemDao.findProblemsInTask(courseId);
            return Result.success(problems);
        }
    }

    @Override
    public Result addProblemToCourse(Integer problemId, List<Integer> courseIds, Integer createUser) {
        for (Integer courseId : courseIds) {
            Integer result = problemDao.insertIntoCourseProblem(problemId, courseId, createUser);
            if (!(result > 0)) {
                return Result.fail(400, "加入课程失败");
            }
        }
        return Result.success("加入课程成功");
    }

    private Result checkUserExist(Integer userId) throws UserNotFindException {
        Result result = RestUtil.get(restTemplate, userServiceUrl + "/user/" + userId + "/", User.class);
        if (!result.getStatus().equals(200)) {
            throw new UserNotFindException(userId);
        }
        return result;
    }

}
