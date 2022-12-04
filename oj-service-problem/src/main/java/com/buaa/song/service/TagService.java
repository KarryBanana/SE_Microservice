package com.buaa.song.service;
;
import com.buaa.song.result.Result;
import org.springframework.stereotype.Service;

/**
 * @FileName: TagService
 * @Author: ProgrammerZhao
 * @Date: 2020/12/18
 * @Description:
 */
@Service
public interface TagService {
    Result create(String tagName);

    Result getAll();

    Result update(Integer tagId, String name);

    Result delete(Integer tagId);
}
