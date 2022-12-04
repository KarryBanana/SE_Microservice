package com.buaa.song.service.impl;

import com.alibaba.fastjson.JSON;
import com.buaa.song.dao.ProblemDao;
import com.buaa.song.dao.TagDao;
import com.buaa.song.dto.PageAndSortDto;
import com.buaa.song.entity.Class;
import com.buaa.song.entity.Language;
import com.buaa.song.entity.Problem;
import com.buaa.song.entity.User;
import com.buaa.song.exception.OjClassNotFindException;
import com.buaa.song.exception.ProblemNotFindException;
import com.buaa.song.exception.UserNotFindException;
import com.buaa.song.result.Result;
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
import org.springframework.cache.CacheManager;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import com.buaa.song.service.ProblemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

@Service
@RefreshScope
public class ProblemServiceImpl implements ProblemService {
    // logger是记录日志吧
    private static final Logger logger = LoggerFactory.getLogger(ProblemServiceImpl.class);
    private static final String userServiceUrl = "http://oj-service-user";

    @Autowired
    private ProblemDao problemDao;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CacheManager cacheManager;


    @Override
    public Result getLangByProblemId(Integer problemId) {
        try {
            checkProblemExist(problemId);
            List<Integer> availableLanguage = problemDao.findLangByProblemId(problemId);

            return Result.success(availableLanguage);
        } catch (ProblemNotFindException e) {
            return Result.fail(400 ,e.getMessage());
        }
    }

    @Override
    public Result getPublicProblemList(Integer userId, PageAndSortDto page) {
        try {
            System.out.println("cache type is " + cacheManager.getClass());
            Result result = checkUserExist(userId);
            User user = (User) result.getData();
            List<Map<String, Object>> problems = null;
            Integer start = page.getPage();
            Integer limit = page.getLimit();

            if (user.getRole().equals(2)) {
                // 普通用户查看部分题目
                problems = problemDao.findPublicProblems(userId, (start - 1) * limit, limit);
            } else {
                // 超级管理员可以看全部题目
                problems = problemDao.findAllProblems((start - 1) * limit, limit);
            }
            // 获取题目其他信息 AC次数，提交次数

            System.out.println(problems);
            List<Map<String, Object>> new_problems = getProblemExtraInfo(problems, userId);

            return Result.success(new_problems);
        } catch (UserNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }
    }

    @Override
    public Result searchProblemByAuthor(Integer userId, String author, PageAndSortDto page) {
        try{
            checkUserExist(userId);
            Integer start = page.getPage();
            Integer limit = page.getLimit();

            System.out.println("author is " + author);
            SearchRequest searchRequest = new SearchRequest("oj5th_test_user");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            String searchField = "nickname";

            searchSourceBuilder.query(QueryBuilders.matchQuery(searchField, author));
            searchSourceBuilder.from(0);
            searchSourceBuilder.size(10000);
            String[] includeFields = new String[]{"id"};
            searchSourceBuilder.fetchSource(includeFields, null);
            searchRequest.source(searchSourceBuilder);

            try {
                SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
                SearchHits searchResult = searchResponse.getHits();
                SearchHit[] hits = searchResult.getHits();
                List<Integer> creatorIds = new LinkedList<>();
                for(SearchHit hit : hits){
                    System.out.println("我模糊搜索到了!");
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    Integer id = (Integer) sourceAsMap.get("id");
                    System.out.println(id);
                    creatorIds.add(id);
                }

                List<Map<String,Object>> problems = problemDao.findProblemsInSearch(userId, creatorIds, (start - 1) * limit,
                        limit);

                // 补充题目其他的信息
                List<Map<String, Object>> new_problems = getProblemExtraInfo(problems, userId);

                return Result.success(new_problems);
            } catch (IOException e) {
                logger.error(e.getMessage());
                return Result.fail(500,"IO异常");
            }

        } catch (UserNotFindException e) {
            logger.error(e.getMessage());
            return  Result.fail(400, e.getMessage());
        }
    }

    public Result searchProblemByTitle(Integer userId, String title, PageAndSortDto page) {
        System.out.println("copy from zb code!");
        Integer start = page.getPage();
        Integer limit = page.getLimit();

        SearchRequest searchRequest = new SearchRequest("oj5th_test_problem");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String searchField = "title";

        searchSourceBuilder.query(QueryBuilders.matchQuery(searchField, title));

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
            List<Map<String,Object>> problems = null;

            if(roleId.equals(2)){
                problems = problemDao.findMyProblemsInSearch(problemIds, userId, (start - 1) * limit, limit);
            }else {
                problems = problemDao.findAllProblemsInSearch(problemIds, (start - 1) * limit, limit);
            }
            List<Map<String, Object >> new_problems = getProblemExtraInfo(problems, userId);

            return Result.success(new_problems);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return Result.fail(500,"IO异常");
        }
    }

    // 根据标签数组获取题目
    // 错误状态码需要商讨一下
    @Override
    public Result searchProblemByTags(Integer userId, Object content) {
        if(content.getClass().isArray()) {
            int len = Array.getLength(content);
            int[] tags = new int[len];
            for(int i = 0; i<len; ++i) {
                tags[i] = Integer.parseInt((String) Array.get(content, i));
                System.out.print(tags[i] +" ");
            }
            System.out.println();
            return Result.success("tags success");
        }
        return Result.fail(401);
    }

    @Override
    public Result getAcceptRankList() {
        List<Map<String, Object>> rankList = null;
        rankList = problemDao.findAcceptRankList();
        if(rankList != null)
            return Result.success(rankList);
        return Result.fail(400, "排行榜为空");
    }

    @Override
    public Result getQuestionAnswer() {
        List<Map<String, Object>> question_answer = null;
        question_answer = problemDao.findQuestionAnswer();
        if(question_answer != null)
            return Result.success(question_answer);
        return Result.fail(400);
    }

    // 错误状态码需要商讨一下, 事务回滚还得看下
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result collectProblem(Integer userId, Integer problemId) {
        try {
            checkUserExist(userId);
            // checkProblemExist(problemId);
            List<Map<String, Object>> problems = null;

            problems = problemDao.checkProblemCollected(userId, problemId);
            if(problems.size() > 0)
                return Result.fail(401, "题目已经收藏");
            // 时间在sql语句里添加了
            Integer result = problemDao.insertIntoCollectProblem(userId, problemId);
            if (!(result > 0)) {
                return Result.fail(400, "收藏题目失败");
            }
            return Result.success("收藏成功");
        }catch (UserNotFindException e) {
            logger.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(400, e.getMessage());
        }
    }

    // 取消收藏 事务回滚还得看下
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result cancelCollectProblem(Integer userId, Integer problemId) {
        try {
            checkUserExist(userId);
            checkProblemExist(problemId);
            List<Map<String, Object>> problems = null;
            problems = problemDao.checkProblemCollected(userId, problemId);
            if(problems.size() > 0) {
                Integer result = problemDao.deleteCollectProblem(userId, problemId);
                if (!(result > 0)) {
                    return Result.fail(400, "取消收藏题目失败");
                }
                return Result.success("取消收藏成功");
            } else {
                return Result.fail(401, "题目未收藏");
            }
        }catch (UserNotFindException | ProblemNotFindException e) {
            logger.error(e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.fail(400, e.getMessage());
        }
    }

    @Override
    public Result showCollectProblem(Integer userId, PageAndSortDto page) {
        try {
            checkUserExist(userId);

            Integer start = page.getPage();
            Integer limit = page.getLimit();

            List<Map<String, Object>> collect_problems = null;
            collect_problems = problemDao.findCollectProblems(userId, (start - 1) * limit, limit);

            List<Map<String, Object>> new_collect_problems = getProblemExtraInfo(collect_problems, userId);

            return Result.success(new_collect_problems);
        }catch (UserNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }
    }

    /*
    未完待续  感觉mysql硬查速度有点慢
     */
    @Override
    public Result searchProblemById(Integer userId, Integer problemId) {
        try {
            checkUserExist(userId);
            checkProblemExist(problemId);
            List<Map<String, Object>> search_problems = null;

            search_problems = problemDao.searchProblemById(userId, problemId);

            List<Map<String, Object>> new_search_problems = getProblemExtraInfo(search_problems, userId);

            return Result.success(new_search_problems);
        }catch (UserNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }catch (ProblemNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }
    }

    // 获取题目的额外信息 ac次数，总提交量啥的
    public List<Map<String, Object>> getProblemExtraInfo(List<Map<String, Object>> problems, Integer userId) {
        List<Map<String, Object>> problemsWithExtraInfo = new LinkedList<>();  // 新建一个list

        for (Map<String, Object> problem : problems) {
//                for(Map.Entry<String, Object> entry : problems.get(i).entrySet()) {
//                    System.out.print(" key = " + entry.getKey() + " value = " + entry.getValue());
//                }
//                System.out.println();


            Map<String, Object> extraInfo = new HashMap<>(problem);
            int problemId = (int) problem.get("id");
            List<Map<String, Object>> acceptSubmission = problemDao.findAcceptSubmission(userId, problemId);
            if (acceptSubmission.size() > 0)
                extraInfo.put("status", 0);

            List<Map<String, Object>> attemptSubmission = problemDao.findAttemptSubmission(userId, problemId);
            if (attemptSubmission.size() > 0)  // 有提交记录
                extraInfo.put("status", 1);
            else  // 没有提交记录
                extraInfo.put("status", 2);

            Map<String, Object> problemAcSubNum = problemDao.findProblemAcSubNum(problemId);

            extraInfo.put("acNum", problemAcSubNum.get("ac_num"));
            extraInfo.put("acUserNum", problemAcSubNum.get("ac_user_num"));
            extraInfo.put("subNum", problemAcSubNum.get("sub_num"));
            extraInfo.put("subUserNum", problemAcSubNum.get("sub_user_num"));

            // 题目的标签数组暂时只返回一个标签, {tagName: xxx, count: 1}
            Map<String, Object> problemTags = tagDao.findProblemTags(problemId);
            extraInfo.put("tags", problemTags);

            problemsWithExtraInfo.add(extraInfo);
        }
        return problemsWithExtraInfo;
    }


    private Result checkUserExist(Integer userId) throws UserNotFindException {
        Result result = RestUtil.get(restTemplate, userServiceUrl + "/user/" + userId, User.class);
        if (!result.getStatus().equals(200)) {
            throw new UserNotFindException(userId);
        }
        return result;
    }

    private  Result checkProblemExist(Integer problemId) throws ProblemNotFindException {
        try{
            Optional<Problem> optionalProblem = problemDao.findById(problemId);
            if (!optionalProblem.isPresent()) {
                throw new ProblemNotFindException(problemId);
            }
        }catch (ProblemNotFindException e) {
            logger.error(e.getMessage());
            return Result.fail(400, e.getMessage());
        }
        return Result.success("problem exists");
    }

}
