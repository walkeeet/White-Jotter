package com.gm.wj.controller;

import com.gm.wj.entity.Book;
import com.gm.wj.entity.User;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.BookFavoriteService;
import com.gm.wj.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Book favorite controller.
 *
 * @author Evan
 * @date 2019/4
 */
@RestController
public class BookFavoriteController {
    @Autowired
    private BookFavoriteService bookFavoriteService;
    @Autowired
    private UserService userService;

    @PostMapping("/api/favorite/add")
    public Result addFavorite(@RequestParam int bookId) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }
        String username = (String) subject.getPrincipal();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        }
        bookFavoriteService.addBookFavorite(user.getId(), bookId);
        return ResultFactory.buildSuccessResult("收藏成功");
    }

    @PostMapping("/api/favorite/delete")
    public Result deleteFavorite(@RequestParam int bookId) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }
        String username = (String) subject.getPrincipal();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        }
        bookFavoriteService.deleteBookFavorite(user.getId(), bookId);
        return ResultFactory.buildSuccessResult("取消收藏成功");
    }

    @GetMapping("/api/favorite/list")
    public Result listFavorites() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }
        String username = (String) subject.getPrincipal();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        }
        List<Book> books = bookFavoriteService.listFavoriteBooks(user.getId());
        return ResultFactory.buildSuccessResult(books);
    }

    @GetMapping("/api/favorite/check")
    public Result checkFavorite(@RequestParam int bookId) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildSuccessResult(false);
        }
        String username = (String) subject.getPrincipal();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildSuccessResult(false);
        }
        boolean isFavorite = bookFavoriteService.isFavorite(user.getId(), bookId);
        return ResultFactory.buildSuccessResult(isFavorite);
    }
}
