package com.gm.wj.controller;

import com.gm.wj.entity.JotterArticle;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.JotterArticleService;
import com.gm.wj.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Jotter controller.
 *
 * @author Evan
 * @date 2020/1/14 20:33
 */
@RestController
public class JotterController {
    @Autowired
    JotterArticleService jotterArticleService;
    @Autowired
    LikeService likeService;

    @PostMapping("api/admin/content/article")
    public Result saveArticle(@RequestBody @Valid JotterArticle article) {
        jotterArticleService.addOrUpdate(article);
        return ResultFactory.buildSuccessResult("保存成功");
    }

    @GetMapping("/api/article/{size}/{page}")
    public Result listArticles(@PathVariable("size") int size, @PathVariable("page") int page) {
        return ResultFactory.buildSuccessResult(jotterArticleService.list(page - 1, size));
    }

    @GetMapping("/api/article/{id}")
    public Result getOneArticle(@PathVariable("id") int id) {
        return ResultFactory.buildSuccessResult(jotterArticleService.findById(id));
    }

    @DeleteMapping("/api/admin/content/article/{id}")
    public Result deleteArticle(@PathVariable("id") int id) {
        jotterArticleService.delete(id);
        return ResultFactory.buildSuccessResult("删除成功");
    }

    @PostMapping("/api/article/like/{id}")
    public Result toggleLike(@PathVariable("id") int id) {
        int result = likeService.toggleLike(id);
        if (result == -1) {
            return ResultFactory.buildFailResult("请先登录");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("status", result);
        data.put("count", likeService.getLikeCount(id));
        return ResultFactory.buildSuccessResult(data);
    }

    @GetMapping("/api/article/like/{id}")
    public Result getLikeInfo(@PathVariable("id") int id) {
        Map<String, Object> data = new HashMap<>();
        data.put("count", likeService.getLikeCount(id));
        data.put("userStatus", likeService.getUserLikeStatus(id));
        return ResultFactory.buildSuccessResult(data);
    }
}
