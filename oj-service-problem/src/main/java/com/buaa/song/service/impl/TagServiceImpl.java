package com.buaa.song.service.impl;

import com.buaa.song.dao.TagDao;
import com.buaa.song.entity.Tag;
import com.buaa.song.result.Result;
import com.buaa.song.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.text.Collator;
import java.util.*;

/**
 * @FileName: TagServiceImpl
 * @author: ProgrammerZhao
 * @Date: 2020/12/18
 * @Description:
 */
@Service
@RefreshScope
public class TagServiceImpl implements TagService {

    @Autowired
    private TagDao tagDao;
    @Override
    public Result create(String tagName) {
        Tag tag = tagDao.findByName(tagName);
        if(tag == null){
            tag = new Tag();
            tag.setName(tagName);
            tagDao.save(tag);
            return Result.success("添加成功");
        }else{
            return Result.fail(400,"添加失败，该标签已存在");
        }
    }

    @Override
    public Result delete(Integer tagId) {
        tagDao.deleteById(tagId);
        return Result.success();
    }

    @Override
    public Result update(Integer tagId, String name) {
        Optional<Tag> optionalTag = tagDao.findById(tagId);
        if(optionalTag.isPresent()){
            Tag tag = optionalTag.get();
            tag.setName(name);
            tagDao.save(tag);
            return Result.success("修改成功");
        }else{
            return Result.fail(400,"修改失败，该标签不存在");
        }
    }

    @Override
    public Result getAll() {
        Iterable<Tag> all = tagDao.findAll();
        List<Tag> tags = new ArrayList<>();
        all.forEach(tag -> tags.add(tag));
        Collections.sort(tags, ((Comparator<Tag>) (tag1, tag2) -> {
            String name1 = tag1.getName();
            String name2 = tag2.getName();
            return name1.compareTo(name2);
        }).thenComparing((tag1, tag2) -> {
            Integer count1 = tag1.getCount();
            Integer count2 = tag2.getCount();
            return count1 - count2;
        }) );
        return Result.success(tags);
    }
}