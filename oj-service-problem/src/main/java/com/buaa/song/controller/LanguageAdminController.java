package com.buaa.song.controller;


import com.buaa.song.result.Result;
import com.buaa.song.service.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @FileName: LanguageController
 * @author: ProgrammerZhao
 * @Date: 2020/12/18
 * @Description:
 */

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/admin/lang")
public class LanguageAdminController {

    @Autowired
    private LanguageService languageService;

    @PostMapping
    public Result create(String name, String info){
        return languageService.create(name, info);
    }

    @GetMapping("/{id}")
    public Result getLangById(@PathVariable Integer id){
        return languageService.getLangById(id);
    }


    @GetMapping("/all")
    public Result getAllLangs(){
        return languageService.getAll();
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable("id") Integer lid, String name, String info){
        return languageService.update(lid,name,info);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") Integer lid){
        return languageService.delete(lid);
    }
}