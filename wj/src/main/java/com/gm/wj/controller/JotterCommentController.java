package com.gm.wj.controller;

import com.gm.wj.entity.JotterComment;
import com.gm.wj.entity.User;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.JotterCommentService;
import com.gm.wj.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Jotter comment controller.
 *
 * @author Assistant
 * @date 2026-02-04
 */
@RestController
public class JotterCommentController {

    @Autowired
    JotterCommentService jotterCommentService;

    @Autowired
    UserService userService;

    /**
     * Add a new comment or reply.
     * Requires login.
     */
    @PostMapping("/api/comment")
    public Result addComment(@RequestBody @Valid JotterComment comment) {
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

        comment.setUserId(user.getId());
        JotterComment savedComment = jotterCommentService.addComment(comment);
        return ResultFactory.buildSuccessResult(savedComment);
    }

    /**
     * List comments for an article with pagination.
     */
    @GetMapping("/api/comment/{articleId}/{size}/{page}")
    public Result listComments(@PathVariable("articleId") Integer articleId,
                               @PathVariable("size") int size,
                               @PathVariable("page") int page) {
        return ResultFactory.buildSuccessResult(jotterCommentService.listCommentsByArticle(articleId, page - 1, size));
    }

    /**
     * Get comment count for an article.
     */
    @GetMapping("/api/comment/count/{articleId}")
    public Result getCommentCount(@PathVariable("articleId") Integer articleId) {
        return ResultFactory.buildSuccessResult(jotterCommentService.countCommentsByArticle(articleId));
    }

    /**
     * Delete a comment.
     * Requires login and can only delete own comment.
     */
    @DeleteMapping("/api/comment/{id}")
    public Result deleteComment(@PathVariable("id") int id) {
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

        JotterComment comment = jotterCommentService.findById(id);
        if (comment == null) {
            return ResultFactory.buildFailResult("评论不存在");
        }

        // Check if user owns this comment
        if (comment.getUserId() != user.getId()) {
            return ResultFactory.buildFailResult("只能删除自己的评论");
        }

        jotterCommentService.deleteComment(id);
        return ResultFactory.buildSuccessResult("删除成功");
    }
}
