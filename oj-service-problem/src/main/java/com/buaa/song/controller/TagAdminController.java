package com.buaa.song.controller;

import com.buaa.song.result.Result;
import com.buaa.song.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @FileName: TagController
 * @author: ProgrammerZhao
 * @Date: 2020/12/18
 * @Description:
 */

@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/admin/tag")
public class TagAdminController {

    @Autowired
    private TagService tagService;

    @PostMapping
    public Result createTag(String tagName){
        return tagService.create(tagName);
    }

    @GetMapping("/all")
    public Result getAllTags(){
        return tagService.getAll();
    }

    @PutMapping("/{id}")
    public Result updateTag(@PathVariable("id") Integer tagId, String name){
        return tagService.update(tagId, name);
    }

    @DeleteMapping("/{id}")
    public Result deleteTag(@PathVariable("id") Integer tagId){
        return tagService.delete(tagId);
    }

}