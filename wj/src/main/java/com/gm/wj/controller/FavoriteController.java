package com.gm.wj.controller;

import com.gm.wj.entity.Book;
import com.gm.wj.entity.User;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.BookFavoriteService;
import com.gm.wj.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FavoriteController {

    @Autowired
    BookFavoriteService bookFavoriteService;

    @Autowired
    UserService userService;

    @PostMapping("/api/favorites/add")
    public Result addFavorite(@RequestBody Book book) {
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        bookFavoriteService.addFavorite(user, book);
        return ResultFactory.buildSuccessResult("收藏成功");
    }

    @PostMapping("/api/favorites/remove")
    public Result removeFavorite(@RequestBody Book book) {
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        bookFavoriteService.removeFavorite(user, book);
        return ResultFactory.buildSuccessResult("取消收藏成功");
    }

    @GetMapping("/api/favorites")
    public Result listFavorites() {
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        List<Book> books = bookFavoriteService.listFavorites(user);
        return ResultFactory.buildSuccessResult(books);
    }

    @GetMapping("/api/favorites/check/{bookId}")
    public Result checkFavorite(@PathVariable("bookId") int bookId) {
        if (SecurityUtils.getSubject().getPrincipal() == null) {
            return ResultFactory.buildSuccessResult(false);
        }
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildSuccessResult(false);
        }
        Book book = new Book();
        book.setId(bookId);
        boolean isFavorite = bookFavoriteService.isFavorite(user, book);
        return ResultFactory.buildSuccessResult(isFavorite);
    }
}
