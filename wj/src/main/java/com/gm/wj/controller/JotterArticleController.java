package com.gm.wj.controller;

import com.gm.wj.entity.JotterArticle;
import com.gm.wj.entity.User;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.JotterArticleService;
import com.gm.wj.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;

/**
 * Jotter article controller for user operations.
 *
 * @author Assistant
 * @date 2026-02-04
 */
@RestController
public class JotterArticleController {

    @Autowired
    JotterArticleService jotterArticleService;

    @Autowired
    UserService userService;

    /**
     * Publish a new article. Requires login.
     */
    @PostMapping("/api/jotter/article")
    public Result publishArticle(@RequestBody @Valid JotterArticle article) {
        // Get current user
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            return ResultFactory.buildFailResult("请先登录");
        }

        String username = (String) subject.getPrincipal();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        }

        // Set article date if not set
        if (article.getArticleDate() == null) {
            article.setArticleDate(new Date(System.currentTimeMillis()));
        }

        jotterArticleService.addOrUpdate(article);
        return ResultFactory.buildSuccessResult("发布成功");
    }

    /**
     * Delete an article. Requires login and can only delete own article.
     */
    @DeleteMapping("/api/jotter/article/{id}")
    public Result deleteArticle(@PathVariable("id") int id) {
        // Get current user
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            return ResultFactory.buildFailResult("请先登录");
        }

        String username = (String) subject.getPrincipal();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        }

        JotterArticle article = jotterArticleService.findById(id);
        if (article == null) {
            return ResultFactory.buildFailResult("文章不存在");
        }

        // Delete article and its comments
        jotterArticleService.delete(id);

        return ResultFactory.buildSuccessResult("删除成功");
    }
}
