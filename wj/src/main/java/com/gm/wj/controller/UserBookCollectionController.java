package com.gm.wj.controller;

import com.gm.wj.entity.UserBookCollection;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.UserBookCollectionService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserBookCollectionController {

    @Autowired
    private UserBookCollectionService userBookCollectionService;

    @PostMapping("/collection/add")
    public Result addCollection(@RequestBody CollectionRequest request) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }

        String username = subject.getPrincipal().toString();
        int userId = Integer.parseInt(username.split("_")[0]);

        try {
            userBookCollectionService.addCollection(userId, request.getBookId());
            return ResultFactory.buildSuccessResult("收藏成功");
        } catch (Exception e) {
            return ResultFactory.buildFailResult("收藏失败：" + e.getMessage());
        }
    }

    @PostMapping("/collection/remove")
    public Result removeCollection(@RequestBody CollectionRequest request) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }

        String username = subject.getPrincipal().toString();
        int userId = Integer.parseInt(username.split("_")[0]);

        try {
            userBookCollectionService.removeCollection(userId, request.getBookId());
            return ResultFactory.buildSuccessResult("取消收藏成功");
        } catch (Exception e) {
            return ResultFactory.buildFailResult("取消收藏失败：" + e.getMessage());
        }
    }

    @GetMapping("/collection/my")
    public Result getMyCollections() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }

        String username = subject.getPrincipal().toString();
        int userId = Integer.parseInt(username.split("_")[0]);

        List<UserBookCollection> collections = userBookCollectionService.getCollectionsByUserId(userId);
        return ResultFactory.buildSuccessResult(collections);
    }

    @GetMapping("/collection/check/{bookId}")
    public Result checkCollection(@PathVariable int bookId) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildSuccessResult(false);
        }

        String username = subject.getPrincipal().toString();
        int userId = Integer.parseInt(username.split("_")[0]);

        boolean isCollected = userBookCollectionService.isCollected(userId, bookId);
        return ResultFactory.buildSuccessResult(isCollected);
    }

    private static class CollectionRequest {
        private int bookId;

        public int getBookId() {
            return bookId;
        }

        public void setBookId(int bookId) {
            this.bookId = bookId;
        }
    }
}
