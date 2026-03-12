package com.gm.wj.controller;

import com.gm.wj.entity.Book;
import com.gm.wj.entity.BookCollection;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.BookCollectionService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Book collection controller.
 *
 * @author Evan
 * @date 2024/3
 */
@RestController
public class BookCollectionController {
    @Autowired
    BookCollectionService bookCollectionService;

    @GetMapping("/api/collection/check/{bookId}")
    public Result checkCollectionStatus(@PathVariable("bookId") int bookId) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }
        String username = subject.getPrincipal().toString();
        boolean isCollected = bookCollectionService.isBookCollected(username, bookId);
        return ResultFactory.buildSuccessResult(isCollected);
    }

    @PostMapping("/api/collection/add/{bookId}")
    public Result collectBook(@PathVariable("bookId") int bookId) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }
        String username = subject.getPrincipal().toString();
        boolean success = bookCollectionService.collectBook(username, bookId);
        if (success) {
            return ResultFactory.buildSuccessResult("收藏成功");
        } else {
            return ResultFactory.buildFailResult("已收藏过该书籍");
        }
    }

    @PostMapping("/api/collection/remove/{bookId}")
    public Result uncollectBook(@PathVariable("bookId") int bookId) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }
        String username = subject.getPrincipal().toString();
        boolean success = bookCollectionService.uncollectBook(username, bookId);
        if (success) {
            return ResultFactory.buildSuccessResult("取消收藏成功");
        } else {
            return ResultFactory.buildFailResult("取消收藏失败");
        }
    }

    @GetMapping("/api/collection/list")
    public Result listCollectedBooks() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }
        String username = subject.getPrincipal().toString();
        List<Book> books = bookCollectionService.listCollectedBooksByUsername(username);
        return ResultFactory.buildSuccessResult(books);
    }

    @GetMapping("/api/admin/collection/all")
    public Result listAllCollections() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }
        List<BookCollection> collections = bookCollectionService.listAllCollections();
        return ResultFactory.buildSuccessResult(collections);
    }
}
