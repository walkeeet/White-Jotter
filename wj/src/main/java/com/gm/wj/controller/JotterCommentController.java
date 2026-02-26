package com.gm.wj.controller;

import com.gm.wj.entity.JotterArticle;
import com.gm.wj.entity.JotterComment;
import com.gm.wj.entity.User;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.JotterArticleService;
import com.gm.wj.service.JotterCommentService;
import com.gm.wj.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class JotterCommentController {
    @Autowired
    JotterCommentService jotterCommentService;
    @Autowired
    JotterArticleService jotterArticleService;
    @Autowired
    UserService userService;

    @GetMapping("/api/comment/{articleId}")
    public Result listComments(@PathVariable("articleId") int articleId,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        JotterArticle article = jotterArticleService.findById(articleId);
        if (article == null) {
            return ResultFactory.buildFailResult("文章不存在");
        }
        Map<String, Object> comments = jotterCommentService.listCommentsByArticle(article, page, size);
        return ResultFactory.buildSuccessResult(comments);
    }

    @GetMapping("/api/comment/count/{articleId}")
    public Result countComments(@PathVariable("articleId") int articleId) {
        JotterArticle article = jotterArticleService.findById(articleId);
        if (article == null) {
            return ResultFactory.buildFailResult("文章不存在");
        }
        int count = jotterCommentService.countByArticle(article);
        return ResultFactory.buildSuccessResult(count);
    }

    @PostMapping("/api/comment/{articleId}")
    public Result addComment(@PathVariable("articleId") int articleId,
                             @RequestBody JotterComment comment) {
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        JotterArticle article = jotterArticleService.findById(articleId);
        if (article == null) {
            return ResultFactory.buildFailResult("文章不存在");
        }
        comment.setArticle(article);
        JotterComment savedComment = jotterCommentService.addComment(comment, user);
        return ResultFactory.buildSuccessResult(savedComment);
    }

    @PostMapping("/api/comment/reply/{commentId}")
    public Result replyComment(@PathVariable("commentId") int commentId,
                               @RequestBody Map<String, Object> request) {
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        
        JotterComment parentComment = jotterCommentService.findById(commentId);
        if (parentComment == null) {
            return ResultFactory.buildFailResult("评论不存在");
        }

        String content = (String) request.get("content");
        Integer replyToUserId = (Integer) request.get("replyToUserId");
        
        JotterComment replyComment = new JotterComment();
        replyComment.setContent(content);
        replyComment.setArticle(parentComment.getArticle());
        
        User replyToUser = null;
        if (replyToUserId != null && replyToUserId > 0) {
            replyToUser = userService.findById(replyToUserId);
        }
        
        JotterComment savedReply = jotterCommentService.replyComment(commentId, replyComment, user, replyToUser);
        return ResultFactory.buildSuccessResult(savedReply);
    }

    @DeleteMapping("/api/comment/{commentId}")
    public Result deleteComment(@PathVariable("commentId") int commentId) {
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        
        JotterComment comment = jotterCommentService.findById(commentId);
        if (comment == null) {
            return ResultFactory.buildFailResult("评论不存在");
        }
        if (comment.getUser().getId() != user.getId()) {
            return ResultFactory.buildFailResult("只能删除自己的评论");
        }
        
        jotterCommentService.deleteComment(commentId, user);
        return ResultFactory.buildSuccessResult("删除成功");
    }
}
