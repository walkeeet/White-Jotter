package com.gm.wj.controller;

import com.gm.wj.entity.JotterComment;
import com.gm.wj.entity.User;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.JotterCommentService;
import com.gm.wj.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class JotterCommentController {
    @Autowired
    JotterCommentService jotterCommentService;
    @Autowired
    UserService userService;

    @GetMapping("/api/comment/{articleId}/{size}/{page}")
    public Result listComments(@PathVariable("articleId") int articleId, @PathVariable("size") int size, @PathVariable("page") int page) {
        return ResultFactory.buildSuccessResult(jotterCommentService.listCommentsByArticleId(articleId, page - 1, size));
    }

    @GetMapping("/api/comment/count/{articleId}")
    public Result countComments(@PathVariable("articleId") int articleId) {
        return ResultFactory.buildSuccessResult(jotterCommentService.countCommentsByArticleId(articleId));
    }

    @PostMapping("/api/comment")
    public Result addComment(@RequestBody @Valid JotterComment comment) {
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        comment.setUserId(user.getId());
        jotterCommentService.addComment(comment);
        return ResultFactory.buildSuccessResult("评论成功");
    }

    @DeleteMapping("/api/comment/{id}")
    public Result deleteComment(@PathVariable("id") int id) {
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        jotterCommentService.deleteComment(id, user.getId());
        return ResultFactory.buildSuccessResult("删除成功");
    }
}
