package com.gm.wj.controller;

import com.gm.wj.entity.JotterArticle;
import com.gm.wj.entity.User;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.CommentService;
import com.gm.wj.service.JotterArticleService;
import com.gm.wj.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;


/**
 * Jotter Article Controller with user authentication.
 */
@RestController
public class JotterArticleController {
    @Autowired
    JotterArticleService jotterArticleService;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    private User getCurrentUser() {
        Subject subject = SecurityUtils.getSubject();
        String username = (String) subject.getPrincipal();
        return userService.findByUsername(username);
    }

    @PostMapping("/api/article")
    public Result publishArticle(@RequestBody @Valid JotterArticle article) {
        User currentUser = getCurrentUser();
        article.setAuthor(currentUser);
        article.setArticleDate(new Date(System.currentTimeMillis()));
        article.setCommentCount(0);
        jotterArticleService.addOrUpdate(article);
        return ResultFactory.buildSuccessResult("发布成功");
    }

    @PutMapping("/api/article/{id}")
    public Result updateArticle(@PathVariable("id") int id, @RequestBody @Valid JotterArticle article) {
        User currentUser = getCurrentUser();
        JotterArticle existingArticle = jotterArticleService.findById(id);

        if (existingArticle == null) {
            return ResultFactory.buildFailResult("文章不存在");
        }

        if (existingArticle.getAuthor().getId() != currentUser.getId()) {
            return ResultFactory.buildFailResult("无权限修改该文章");
        }

        article.setId(id);
        article.setAuthor(currentUser);
        jotterArticleService.addOrUpdate(article);
        return ResultFactory.buildSuccessResult("更新成功");
    }

    @DeleteMapping("/api/article/{id}")
    public Result deleteArticle(@PathVariable("id") int id) {
        User currentUser = getCurrentUser();
        JotterArticle article = jotterArticleService.findById(id);

        if (article == null) {
            return ResultFactory.buildFailResult("文章不存在");
        }

        if (article.getAuthor().getId() != currentUser.getId()) {
            return ResultFactory.buildFailResult("无权限删除该文章");
        }

        commentService.deleteArticleComments(id);
        jotterArticleService.delete(id);
        return ResultFactory.buildSuccessResult("删除成功");
    }

}

