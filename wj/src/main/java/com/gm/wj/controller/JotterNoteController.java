package com.gm.wj.controller;

import com.gm.wj.entity.JotterNote;
import com.gm.wj.entity.User;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.JotterNoteService;
import com.gm.wj.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class JotterNoteController {
    @Autowired
    JotterNoteService jotterNoteService;
    @Autowired
    UserService userService;

    @GetMapping("/api/note/{size}/{page}")
    public Result listNotes(@PathVariable("size") int size, @PathVariable("page") int page) {
        return ResultFactory.buildSuccessResult(jotterNoteService.list(page - 1, size));
    }

    @GetMapping("/api/note/{id}")
    public Result getOneNote(@PathVariable("id") int id) {
        return ResultFactory.buildSuccessResult(jotterNoteService.findById(id));
    }

    @GetMapping("/api/user/note/{size}/{page}")
    public Result listUserNotes(@PathVariable("size") int size, @PathVariable("page") int page) {
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        return ResultFactory.buildSuccessResult(jotterNoteService.listByUserId(user.getId(), page - 1, size));
    }

    @PostMapping("/api/note")
    public Result saveNote(@RequestBody @Valid JotterNote note) {
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        note.setUserId(user.getId());
        jotterNoteService.addOrUpdate(note);
        return ResultFactory.buildSuccessResult("保存成功");
    }

    @PutMapping("/api/note")
    public Result updateNote(@RequestBody @Valid JotterNote note) {
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        if (!jotterNoteService.isOwner(note.getId(), user.getId())) {
            return ResultFactory.buildFailResult("无权限修改");
        }
        note.setUserId(user.getId());
        jotterNoteService.addOrUpdate(note);
        return ResultFactory.buildSuccessResult("修改成功");
    }

    @DeleteMapping("/api/note/{id}")
    public Result deleteNote(@PathVariable("id") int id) {
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        if (!jotterNoteService.isOwner(id, user.getId())) {
            return ResultFactory.buildFailResult("无权限删除");
        }
        jotterNoteService.delete(id, user.getId());
        return ResultFactory.buildSuccessResult("删除成功");
    }
}
