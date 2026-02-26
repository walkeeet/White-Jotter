package com.gm.wj.controller;

import com.gm.wj.entity.ArticleComment;
import com.gm.wj.entity.User;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.ArticleCommentService;
import com.gm.wj.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Article comment controller.
 *
 * @author System
 * @date 2026/02/26
 */
@RestController
public class ArticleCommentController {
    @Autowired
    ArticleCommentService articleCommentService;
    @Autowired
    UserService userService;

    @GetMapping("/api/article/{articleId}/comments/{size}/{page}")
    public Result listComments(@PathVariable("articleId") int articleId, 
                               @PathVariable("size") int size, 
                               @PathVariable("page") int page) {
        return ResultFactory.buildSuccessResult(articleCommentService.listByArticle(articleId, page - 1, size));
    }

    @GetMapping("/api/article/{articleId}/commentCount")
    public Result getCommentCount(@PathVariable("articleId") int articleId) {
        return ResultFactory.buildSuccessResult(articleCommentService.countByArticle(articleId));
    }

    @PostMapping("/api/article/comment")
    public Result addComment(@RequestBody @Valid ArticleComment comment) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (username == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        User user = userService.findByUsername(username);
        comment.setUserId(user.getId());
        comment.setUsername(username);
        articleCommentService.add(comment);
        return ResultFactory.buildSuccessResult("评论成功");
    }

    @DeleteMapping("/api/article/comment/{id}")
    public Result deleteComment(@PathVariable("id") int id) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (username == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        ArticleComment comment = articleCommentService.findById(id);
        if (comment == null) {
            return ResultFactory.buildFailResult("评论不存在");
        }
        User user = userService.findByUsername(username);
        if (comment.getUserId() != user.getId()) {
            return ResultFactory.buildFailResult("无权删除此评论");
        }
        articleCommentService.delete(id);
        return ResultFactory.buildSuccessResult("删除成功");
    }

    @GetMapping("/api/user/comments/{size}/{page}")
    public Result listUserComments(@PathVariable("size") int size, @PathVariable("page") int page) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (username == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        User user = userService.findByUsername(username);
        return ResultFactory.buildSuccessResult(articleCommentService.listByUser(user.getId(), page - 1, size));
    }
}
