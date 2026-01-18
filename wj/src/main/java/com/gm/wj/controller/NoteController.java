package com.gm.wj.controller;

import com.gm.wj.dto.NoteDTO;
import com.gm.wj.entity.Note;
import com.gm.wj.entity.User;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.NoteService;
import com.gm.wj.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class NoteController {
    @Autowired
    private NoteService noteService;

    @Autowired
    private UserService userService;

    @PostMapping("/api/note")
    public Result saveNote(@RequestBody @Valid Note note) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        User user = userService.findByUsername(username);
        note.setUser(user);
        noteService.addOrUpdate(note);
        return ResultFactory.buildSuccessResult("保存成功");
    }

    @PutMapping("/api/note")
    public Result updateNote(@RequestBody @Valid Note note) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        User user = userService.findByUsername(username);
        Note existingNote = noteService.findEntityById(note.getId());
        if (existingNote == null) {
            return ResultFactory.buildFailResult("笔记不存在");
        }
        if (existingNote.getUser().getId() != user.getId()) {
            return ResultFactory.buildFailResult("无权修改此笔记");
        }
        note.setUser(user);
        noteService.addOrUpdate(note);
        return ResultFactory.buildSuccessResult("修改成功");
    }

    @GetMapping("/api/note/{size}/{page}")
    public Result listNotes(@PathVariable("size") int size, @PathVariable("page") int page) {
        return ResultFactory.buildSuccessResult(noteService.list(page - 1, size));
    }

    @GetMapping("/api/note/user/{size}/{page}")
    public Result listUserNotes(@PathVariable("size") int size, @PathVariable("page") int page) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        User user = userService.findByUsername(username);
        return ResultFactory.buildSuccessResult(noteService.listByUser(user.getId(), page - 1, size));
    }

    @GetMapping("/api/note/{id}")
    public Result getOneNote(@PathVariable("id") int id) {
        NoteDTO note = noteService.findById(id);
        if (note == null) {
            return ResultFactory.buildFailResult("笔记不存在");
        }
        return ResultFactory.buildSuccessResult(note);
    }

    @DeleteMapping("/api/note/{id}")
    public Result deleteNote(@PathVariable("id") int id) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        User user = userService.findByUsername(username);
        NoteDTO note = noteService.findById(id);
        if (note == null) {
            return ResultFactory.buildFailResult("笔记不存在");
        }
        if (note.getUserId() != user.getId()) {
            return ResultFactory.buildFailResult("无权删除此笔记");
        }
        noteService.delete(id);
        return ResultFactory.buildSuccessResult("删除成功");
    }
}
