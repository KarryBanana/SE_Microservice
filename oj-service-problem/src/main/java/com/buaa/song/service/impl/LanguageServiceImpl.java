package com.buaa.song.service.impl;

import com.buaa.song.dao.LanguageDao;
import com.buaa.song.entity.Language;
import com.buaa.song.result.Result;
import com.buaa.song.service.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

/**
 * @FileName: LanguageServiceImpl
 * @author: ProgrammerZhao
 * @Date: 2020/12/18
 * @Description:
 */
@Service
@RefreshScope
public class LanguageServiceImpl implements LanguageService {

    @Autowired
    private LanguageDao languageDao;


    @Override
    public Result getLangById(Integer lid){
        Optional<Language> optionalLang = languageDao.findById(lid);
        if(optionalLang.isPresent()){
            Language language = optionalLang.get();
            String languageName = language.getName();
            System.out.println("In languageDao langName is "+ languageName);
            return Result.success("查找支持的编程语言成功",languageName);
        }else{
            return Result.fail(400,"语言"+lid+"不存在");
        }
    }


    @Override
    public Result create(String name, String info) {
        Language lang = languageDao.findByName(name);
        if(lang == null){
            lang = new Language();
            lang.setName(name);
            lang.setInfo(info);
            languageDao.save(lang);
            return Result.success("添加成功");
        }else{
            return Result.fail(400,"语言 "+name+" 已存在");
        }
    }

    @Override
    public Result delete(Integer langId) {
        languageDao.deleteById(langId);
        return Result.success();
    }

    @Override
    public Result update(Integer langId, String name, String info) {
        Optional<Language> optionalLang = languageDao.findById(langId);
        if(optionalLang.isPresent()){
            Language language = optionalLang.get();
            language.setName(name);
            language.setInfo(info);
            languageDao.save(language);
            return Result.success("修改成功");
        }else{
            return Result.fail(400,"该语言不存在");
        }
    }

    @Override
    public Result getAll() {
        Iterable<Language> languages = languageDao.findAll();
        return Result.success(languages);
    }


}