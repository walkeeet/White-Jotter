package com.gm.wj.controller;

import com.gm.wj.entity.User;
import com.gm.wj.entity.UserNote;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.UserNoteService;
import com.gm.wj.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * User note controller.
 *
 * @author System
 * @date 2026/02/26
 */
@RestController
public class UserNoteController {
    @Autowired
    UserNoteService userNoteService;
    @Autowired
    UserService userService;

    @GetMapping("/api/user/notes/{size}/{page}")
    public Result listUserNotes(@PathVariable("size") int size, @PathVariable("page") int page) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (username == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        User user = userService.findByUsername(username);
        return ResultFactory.buildSuccessResult(userNoteService.listByUser(user.getId(), page - 1, size));
    }

    @GetMapping("/api/user/note/{id}")
    public Result getNote(@PathVariable("id") int id) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (username == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        UserNote note = userNoteService.findById(id);
        if (note == null) {
            return ResultFactory.buildFailResult("笔记不存在");
        }
        User user = userService.findByUsername(username);
        if (note.getUserId() != user.getId()) {
            return ResultFactory.buildFailResult("无权访问此笔记");
        }
        return ResultFactory.buildSuccessResult(note);
    }

    @PostMapping("/api/user/note")
    public Result saveNote(@RequestBody @Valid UserNote note) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (username == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        User user = userService.findByUsername(username);
        note.setUserId(user.getId());
        note.setUsername(username);
        userNoteService.addOrUpdate(note);
        return ResultFactory.buildSuccessResult("保存成功");
    }

    @DeleteMapping("/api/user/note/{id}")
    public Result deleteNote(@PathVariable("id") int id) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (username == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        UserNote note = userNoteService.findById(id);
        if (note == null) {
            return ResultFactory.buildFailResult("笔记不存在");
        }
        User user = userService.findByUsername(username);
        if (note.getUserId() != user.getId()) {
            return ResultFactory.buildFailResult("无权删除此笔记");
        }
        userNoteService.delete(id);
        return ResultFactory.buildSuccessResult("删除成功");
    }

    @GetMapping("/api/user/note/count")
    public Result getNoteCount() {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (username == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        User user = userService.findByUsername(username);
        return ResultFactory.buildSuccessResult(userNoteService.countByUser(user.getId()));
    }
}
