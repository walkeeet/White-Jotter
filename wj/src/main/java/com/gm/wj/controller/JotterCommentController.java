package com.gm.wj.controller;

import com.gm.wj.dto.CommentDTO;
import com.gm.wj.entity.JotterComment;
import com.gm.wj.entity.User;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.JotterCommentService;
import com.gm.wj.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Jotter Comment Controller.
 *
 * @author Evan
 * @date 2026/2/26
 */
@RestController
public class JotterCommentController {
    @Autowired
    JotterCommentService jotterCommentService;
    @Autowired
    UserService userService;

    /**
     * Add a comment.
     * Requires login.
     */
    @PostMapping("/api/comments")
    public Result addComment(@RequestBody @Valid JotterComment comment) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }

        User user = userService.findByUsername(subject.getPrincipal().toString());
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        }

        comment.setUserId(user.getId());
        JotterComment savedComment = jotterCommentService.addComment(comment);
        return ResultFactory.buildSuccessResult(savedComment);
    }

    /**
     * Get comments by article ID with pagination.
     */
    @GetMapping("/api/comments/{articleId}/{size}/{page}")
    public Result listComments(@PathVariable("articleId") int articleId,
                               @PathVariable("size") int size,
                               @PathVariable("page") int page) {
        Page<CommentDTO> comments = jotterCommentService.getCommentsByArticleId(articleId, page - 1, size);
        return ResultFactory.buildSuccessResult(comments);
    }

    /**
     * Delete a comment.
     * Users can only delete their own comments.
     */
    @DeleteMapping("/api/comments/{id}")
    public Result deleteComment(@PathVariable("id") int id) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }

        User user = userService.findByUsername(subject.getPrincipal().toString());
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        }

        JotterComment comment = jotterCommentService.findById(id);
        if (comment == null) {
            return ResultFactory.buildFailResult("评论不存在");
        }

        if (comment.getUserId() != user.getId()) {
            return ResultFactory.buildFailResult("只能删除自己的评论");
        }

        jotterCommentService.deleteComment(id, user.getId());
        return ResultFactory.buildSuccessResult("删除成功");
    }
}
