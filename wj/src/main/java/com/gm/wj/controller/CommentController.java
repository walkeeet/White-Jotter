package com.gm.wj.controller;

import com.gm.wj.entity.Comment;
import com.gm.wj.entity.User;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.CommentService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class CommentController {
    @Autowired
    CommentService commentService;

    @GetMapping("/api/comments/{articleId}")
    public Result listComments(@PathVariable("articleId") int articleId,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        Page<Comment> commentPage = commentService.listByArticleIdWithPage(articleId, page, size);
        return ResultFactory.buildSuccessResult(commentPage);
    }

    @PostMapping("/api/comment")
    public Result addComment(@RequestBody @Valid Comment comment) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        comment.setUserId(user.getId());
        commentService.add(comment);
        return ResultFactory.buildSuccessResult("评论成功");
    }

    @DeleteMapping("/api/comment/{id}")
    public Result deleteComment(@PathVariable("id") int id) {
        Comment comment = commentService.findById(id);
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if (comment.getUserId() == user.getId()) {
            commentService.delete(id);
            return ResultFactory.buildSuccessResult("删除成功");
        } else {
            return ResultFactory.buildFailResult("无权限删除该评论");
        }
    }
}