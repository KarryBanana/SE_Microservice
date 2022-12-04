package com.buaa.song.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.buaa.song.entity.Submission;
import com.buaa.song.entity.User;
import com.buaa.song.exception.SubmissionNotFindException;
import com.buaa.song.exception.UserNotFindException;
import com.buaa.song.result.Result;
import com.buaa.song.service.JudgeService;
import com.buaa.song.utils.RestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@RefreshScope
public class JudgeServiceImpl implements JudgeService {
    private static final Logger logger = LoggerFactory.getLogger(JudgeServiceImpl.class);

    private static final String TASK_QUEUE_KEY = "task:queue";
    private static final String submissionServiceUrl = "http://oj-service-submission";

    @Resource
    private RedisTemplate redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${file.test}")
    private String testFilePath;


    @Override
    public Result getJudgeTask(){
        // 测试下来感觉不加阻塞超时会更好
        Map<String, Object> task = (Map<String, Object>) redisTemplate.opsForList().rightPop(TASK_QUEUE_KEY);
        // System.out.println("After pop:"+redisTemplate.opsForList().range(TASK_QUEUE_KEY, 0, -1));

        if(task == null){ // 队列为空
            return Result.success("目前无评测任务!");
        }else { // 更新sub_id对应的评测状态为JG
            try {
                Integer submissionId = (Integer) task.get("submission_id");
                Result result = getSubmissionById(submissionId);

                Submission sub = (Submission) result.getData();
                sub.setResult("JG");
            } catch (SubmissionNotFindException e) {
                logger.error(e.getMessage());
                return Result.fail(400, e.getMessage());
            }
        }

        return Result.success(task);
    }

    @Override
    public void downloadFile(Integer problemId, HttpServletResponse response){
        String sourceDir = testFilePath + "/" + problemId;
        File dirFile = new File(sourceDir);
        File[] files = dirFile.listFiles();
        List<File> fileList = new ArrayList<>();
        for(File f : files) {
            if(f.isFile() && (!f.getName().endsWith(".tar")) ){
                System.out.println(f.getName());
                fileList.add(f);
            }
            // 这里先默认题目ID目录下的测试数据都是单个的文件
            // 即不存在目录下嵌套目录的情况
        }

        File output = new File(sourceDir, "zip-file.tar");
        try (FileOutputStream out = new FileOutputStream(output);
             TarArchiveOutputStream os = new TarArchiveOutputStream(out)) {
            for (File f : fileList) {
                TarArchiveEntry tarFile = new TarArchiveEntry(f, f.getName());
                tarFile.setSize(f.length());
                os.putArchiveEntry(tarFile);
                IOUtils.copy(new FileInputStream(f), os);
                os.closeArchiveEntry();
            }

            FileInputStream fileInputStream = new FileInputStream(output);
            InputStream fis = new BufferedInputStream(fileInputStream);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            // 清空response
            response.reset();
            // 设置response的Header
            response.setCharacterEncoding("UTF-8");
            //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
            //attachment表示以附件方式下载   inline表示在线打开   "Content-Disposition: inline; filename=文件名.mp3"
            // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
            response.addHeader("Content-Disposition", "attachment;filename=" + output.getName());
            // 告知浏览器文件的大小
            response.addHeader("Content-Length", "" + output.length());
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/x-tar");
            outputStream.write(buffer);
            outputStream.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ;
    }

    private Result getSubmissionById(Integer subId) throws SubmissionNotFindException {
        Result result = RestUtil.get(restTemplate, submissionServiceUrl + "/" + subId, Submission.class);
        if (!result.getStatus().equals(200)) {
            throw new SubmissionNotFindException(subId);
        }
        return result;
    }
}
