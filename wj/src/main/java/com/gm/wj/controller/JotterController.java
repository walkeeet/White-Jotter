package com.gm.wj.controller;

import com.gm.wj.entity.JotterArticle;
import com.gm.wj.entity.User;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.JotterArticleService;
import com.gm.wj.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    UserService userService;

    @PostMapping("api/admin/content/article")
    public Result saveArticle(@RequestBody @Valid JotterArticle article) {
        jotterArticleService.addOrUpdate(article);
        return ResultFactory.buildSuccessResult("保存成功");
    }

    @PostMapping("api/content/article")
    public Result saveArticleByUser(@RequestBody @Valid JotterArticle article) {
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        jotterArticleService.addOrUpdateByUser(article, user);
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

    @DeleteMapping("/api/content/article/{id}")
    public Result deleteArticleByUser(@PathVariable("id") int id) {
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        JotterArticle article = jotterArticleService.findById(id);
        if (article == null) {
            return ResultFactory.buildFailResult("文章不存在");
        }
        if (article.getAuthor() == null || article.getAuthor().getId() != user.getId()) {
            return ResultFactory.buildFailResult("只能删除自己的文章");
        }
        jotterArticleService.delete(id);
        return ResultFactory.buildSuccessResult("删除成功");
    }
}
