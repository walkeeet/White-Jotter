package com.gm.wj.controller;

import com.gm.wj.entity.UserBookCollect;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.UserBookCollectService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class UserBookCollectController {
    @Autowired
    UserBookCollectService userBookCollectService;

    @GetMapping("/api/collect")
    public Result getAllCollects() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("未登录");
        }
        int userId = (int) subject.getSession().getAttribute("userId");
        List<UserBookCollect> collects = userBookCollectService.getAllByUserId(userId);
        return ResultFactory.buildSuccessResult(collects);
    }

    @PostMapping("/api/collect/{bookId}")
    public Result collectBook(@PathVariable int bookId) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录再收藏");
        }
        int userId = (int) subject.getSession().getAttribute("userId");
        UserBookCollect collect = userBookCollectService.collectBook(userId, bookId);
        if (collect != null) {
            return ResultFactory.buildSuccessResult("收藏成功");
        }
        return ResultFactory.buildFailResult("收藏失败");
    }

    @DeleteMapping("/api/collect/{bookId}")
    public Result cancelCollect(@PathVariable int bookId) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("未登录");
        }
        int userId = (int) subject.getSession().getAttribute("userId");
        userBookCollectService.cancelCollect(userId, bookId);
        return ResultFactory.buildSuccessResult("取消收藏成功");
    }

    @GetMapping("/api/collect/status/{bookId}")
    public Result getCollectStatus(@PathVariable int bookId) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildSuccessResult(false);
        }
        int userId = (int) subject.getSession().getAttribute("userId");
        boolean collected = userBookCollectService.isCollected(userId, bookId);
        return ResultFactory.buildSuccessResult(collected);
    }
}
