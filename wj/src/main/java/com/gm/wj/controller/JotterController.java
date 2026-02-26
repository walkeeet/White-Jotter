package com.gm.wj.controller;

import com.gm.wj.dto.JotterArticleDTO;
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

    /**
     * Save article (admin only).
     */
    @PostMapping("api/admin/content/article")
    public Result saveArticle(@RequestBody @Valid JotterArticle article) {
        jotterArticleService.addOrUpdate(article);
        return ResultFactory.buildSuccessResult("保存成功");
    }

    /**
     * Publish article (requires login).
     */
    @PostMapping("/api/articles/publish")
    public Result publishArticle(@RequestBody @Valid JotterArticle article) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }

        User user = userService.findByUsername(subject.getPrincipal().toString());
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        }

        jotterArticleService.addOrUpdate(article, user.getId());
        return ResultFactory.buildSuccessResult("发布成功");
    }

    /**
     * List articles with author info and comment count.
     */
    @GetMapping("/api/article/{size}/{page}")
    public Result listArticles(@PathVariable("size") int size, @PathVariable("page") int page) {
        return ResultFactory.buildSuccessResult(jotterArticleService.listWithAuthorAndCommentCount(page - 1, size));
    }

    /**
     * Get article by ID with author info and comment count.
     */
    @GetMapping("/api/article/{id}")
    public Result getOneArticle(@PathVariable("id") int id) {
        JotterArticleDTO article = jotterArticleService.findByIdWithAuthorAndCommentCount(id);
        if (article == null) {
            return ResultFactory.buildFailResult("文章不存在");
        }
        return ResultFactory.buildSuccessResult(article);
    }

    /**
     * Delete article (admin only).
     */
    @DeleteMapping("/api/admin/content/article/{id}")
    public Result deleteArticle(@PathVariable("id") int id) {
        jotterArticleService.delete(id);
        return ResultFactory.buildSuccessResult("删除成功");
    }

    /**
     * Delete own article (requires login).
     */
    @DeleteMapping("/api/articles/{id}")
    public Result deleteOwnArticle(@PathVariable("id") int id) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }

        User user = userService.findByUsername(subject.getPrincipal().toString());
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        }

        if (!jotterArticleService.isArticleAuthor(id, user.getId())) {
            return ResultFactory.buildFailResult("只能删除自己的文章");
        }

        jotterArticleService.delete(id);
        return ResultFactory.buildSuccessResult("删除成功");
    }
}
