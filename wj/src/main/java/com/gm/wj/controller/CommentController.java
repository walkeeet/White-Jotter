package com.gm.wj.controller;

import com.gm.wj.dto.CommentDTO;
import com.gm.wj.entity.Comment;
import com.gm.wj.entity.Note;
import com.gm.wj.entity.User;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.CommentService;
import com.gm.wj.service.NoteService;
import com.gm.wj.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private NoteService noteService;

    @Autowired
    private UserService userService;

    @PostMapping("/api/comment")
    public Result saveComment(@RequestBody @Valid Comment comment) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        User user = userService.findByUsername(username);
        comment.setUser(user);

        Note note = noteService.findEntityById(comment.getNote().getId());
        if (note == null) {
            return ResultFactory.buildFailResult("笔记不存在");
        }
        comment.setNote(note);

        if (comment.getParent() != null && comment.getParent().getId() != 0) {
            Comment parentComment = commentService.findEntityById(comment.getParent().getId());
            if (parentComment == null) {
                return ResultFactory.buildFailResult("父评论不存在");
            }
            comment.setParent(parentComment);
        }

        commentService.addOrUpdate(comment);
        return ResultFactory.buildSuccessResult("评论成功");
    }

    @GetMapping("/api/comment/note/{noteId}/{size}/{page}")
    public Result listCommentsByNote(@PathVariable("noteId") int noteId, @PathVariable("size") int size, @PathVariable("page") int page) {
        return ResultFactory.buildSuccessResult(commentService.listByNote(noteId, page - 1, size));
    }

    @GetMapping("/api/comment/{id}")
    public Result getOneComment(@PathVariable("id") int id) {
        CommentDTO comment = commentService.findById(id);
        if (comment == null) {
            return ResultFactory.buildFailResult("评论不存在");
        }
        return ResultFactory.buildSuccessResult(comment);
    }

    @DeleteMapping("/api/comment/{id}")
    public Result deleteComment(@PathVariable("id") int id) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        User user = userService.findByUsername(username);
        CommentDTO comment = commentService.findById(id);
        if (comment == null) {
            return ResultFactory.buildFailResult("评论不存在");
        }
        if (comment.getUserId() != user.getId()) {
            return ResultFactory.buildFailResult("无权删除此评论");
        }
        commentService.delete(id, user.getId());
        return ResultFactory.buildSuccessResult("删除成功");
    }
}
