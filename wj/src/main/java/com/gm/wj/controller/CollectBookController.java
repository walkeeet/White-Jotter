package com.gm.wj.controller;

import com.gm.wj.entity.CollectBook;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.CollectBookService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CollectBookController {
    @Autowired
    CollectBookService collectBookService;

    @PostMapping("/api/collect")
    public Result collectBook(@RequestParam("bookId") int bookId) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (username == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        
        if (collectBookService.isCollected(username, bookId)) {
            return ResultFactory.buildFailResult("该书籍已收藏");
        }
        
        CollectBook collectBook = collectBookService.addCollectBook(username, bookId);
        if (collectBook != null) {
            return ResultFactory.buildSuccessResult("收藏成功");
        } else {
            return ResultFactory.buildFailResult("收藏失败");
        }
    }

    @PostMapping("/api/uncollect")
    public Result uncollectBook(@RequestParam("bookId") int bookId) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (username == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        
        collectBookService.deleteCollectBook(username, bookId);
        return ResultFactory.buildSuccessResult("取消收藏成功");
    }

    @GetMapping("/api/collect/status")
    public Result getCollectStatus(@RequestParam("bookId") int bookId) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (username == null) {
            return ResultFactory.buildSuccessResult(false);
        }
        
        boolean isCollected = collectBookService.isCollected(username, bookId);
        return ResultFactory.buildSuccessResult(isCollected);
    }

    @GetMapping("/api/collect/list")
    public Result listCollectBooks() {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (username == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        
        return ResultFactory.buildSuccessResult(collectBookService.listByUser(username));
    }
}
