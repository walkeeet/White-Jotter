package com.gm.wj.controller;

import com.gm.wj.entity.BookCollection;
import com.gm.wj.entity.User;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.BookCollectionService;
import com.gm.wj.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Book collection controller.
 *
 * @author Evan
 * @date 2024/03
 */
@RestController
public class BookCollectionController {
    @Autowired
    private BookCollectionService bookCollectionService;

    @Autowired
    private UserService userService;

    /**
     * Add a book to collection.
     *
     * @param bookId book id
     * @return Result
     */
    @PostMapping("/api/collection/add")
    public Result addCollection(@RequestParam("bookId") int bookId) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (username == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        }
        BookCollection collection = bookCollectionService.addCollection(user.getId(), bookId);
        if (collection != null) {
            return ResultFactory.buildSuccessResult("收藏成功");
        } else {
            return ResultFactory.buildFailResult("已收藏过该书籍");
        }
    }

    /**
     * Remove a book from collection.
     *
     * @param bookId book id
     * @return Result
     */
    @PostMapping("/api/collection/remove")
    public Result removeCollection(@RequestParam("bookId") int bookId) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (username == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        }
        bookCollectionService.removeCollection(user.getId(), bookId);
        return ResultFactory.buildSuccessResult("取消收藏成功");
    }

    /**
     * Check if a book is collected by current user.
     *
     * @param bookId book id
     * @return Result
     */
    @GetMapping("/api/collection/status")
    public Result checkCollectionStatus(@RequestParam("bookId") int bookId) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (username == null) {
            return ResultFactory.buildSuccessResult(false);
        }
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildSuccessResult(false);
        }
        boolean isCollected = bookCollectionService.isCollected(user.getId(), bookId);
        return ResultFactory.buildSuccessResult(isCollected);
    }

    /**
     * Get all collections of current user.
     *
     * @return Result
     */
    @GetMapping("/api/collection/list")
    public Result listCollections() {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (username == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        }
        List<BookCollection> collections = bookCollectionService.listCollectionsByUserId(user.getId());
        return ResultFactory.buildSuccessResult(collections);
    }
}
