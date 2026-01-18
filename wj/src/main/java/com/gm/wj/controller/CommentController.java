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

import java.sql.Date;
import java.util.List;

/**
 * Comment controller.
 *
 * @author Evan
 */
@RestController
public class CommentController {
    @Autowired
    CommentService commentService;

    @GetMapping("/api/article/{id}/comments")
    public Result listComments(@PathVariable int id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<Comment> comments = commentService.listCommentsByArticleId(id, page, size);
        return ResultFactory.buildSuccessResult(comments);
    }

    @PostMapping("/api/article/{id}/comment")
    public Result addComment(@PathVariable int id, @RequestBody Comment comment) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if (user == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        comment.setUser(user);
        comment.setCreateDate(new Date(System.currentTimeMillis()));
        commentService.addComment(id, comment);
        return ResultFactory.buildSuccessResult(comment);
    }

    @PutMapping("/api/comment/{id}")
    public Result updateComment(@PathVariable int id, @RequestBody Comment comment) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Comment oldComment = commentService.getCommentById(id);
        if (oldComment == null) {
            return ResultFactory.buildFailResult("评论不存在");
        }
        if (oldComment.getUser().getId() != user.getId()) {
            return ResultFactory.buildFailResult("无权限修改该评论");
        }
        oldComment.setContent(comment.getContent());
        commentService.updateComment(oldComment);
        return ResultFactory.buildSuccessResult("评论修改成功");
    }

    @DeleteMapping("/api/comment/{id}")
    public Result deleteComment(@PathVariable int id) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Comment comment = commentService.getCommentById(id);
        if (comment == null) {
            return ResultFactory.buildFailResult("评论不存在");
        }
        if (comment.getUser().getId() != user.getId() && comment.getArticle().getAuthor().getId() != user.getId()) {
            return ResultFactory.buildFailResult("无权限删除该评论");
        }
        commentService.deleteComment(id);
        return ResultFactory.buildSuccessResult("评论删除成功");
    }

    @GetMapping("/api/article/{id}/comments/count")
    public Result countComments(@PathVariable int id) {
        long count = commentService.countCommentsByArticleId(id);
        return ResultFactory.buildSuccessResult(count);
    }

    @GetMapping("/api/comment/{id}/replies")
    public Result listReplies(@PathVariable int id, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<Comment> replies = commentService.listRepliesByCommentId(id, page, size);
        return ResultFactory.buildSuccessResult(replies);
    }
}
