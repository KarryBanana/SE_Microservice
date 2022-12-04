package com.buaa.song.service;

import com.buaa.song.result.Result;


/**
 * @FileName: LanguageService
 * @Author: ProgrammerZhao
 * @Date: 2020/12/18
 * @Description:
 */
public interface LanguageService {

    Result create(String name, String info);

    Result getLangById(Integer langId);

    Result getAll();

    Result update(Integer langId, String name, String info);

    Result delete(Integer langId);
}
