package com.gm.wj.controller;

import com.gm.wj.entity.User;
import com.gm.wj.entity.UserBookFavorite;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.UserBookFavoriteService;
import com.gm.wj.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * User book favorite controller.
 *
 * @author Assistant
 * @date 2026/2
 */
@RestController
public class UserBookFavoriteController {

    @Autowired
    private UserBookFavoriteService userBookFavoriteService;

    @Autowired
    private UserService userService;

    /**
     * Add a book to current user's favorites.
     *
     * @param params request body containing bookId
     * @return result
     */
    @PostMapping("/api/favorites")
    public Result addFavorite(@RequestBody Map<String, Integer> params) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }

        String username = subject.getPrincipal().toString();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        }

        Integer bookId = params.get("bookId");
        if (bookId == null) {
            return ResultFactory.buildFailResult("书籍ID不能为空");
        }

        UserBookFavorite favorite = userBookFavoriteService.addFavorite(user.getId(), bookId);
        if (favorite == null) {
            return ResultFactory.buildFailResult("已经收藏过该书籍");
        }
        return ResultFactory.buildSuccessResult("收藏成功");
    }

    /**
     * Remove a book from current user's favorites.
     *
     * @param bookId the book ID
     * @return result
     */
    @DeleteMapping("/api/favorites/{bookId}")
    public Result removeFavorite(@PathVariable("bookId") int bookId) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }

        String username = subject.getPrincipal().toString();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        }

        userBookFavoriteService.removeFavorite(user.getId(), bookId);
        return ResultFactory.buildSuccessResult("取消收藏成功");
    }

    /**
     * Get current user's favorite books.
     *
     * @return result with list of favorites
     */
    @GetMapping("/api/favorites")
    public Result getFavorites() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildFailResult("请先登录");
        }

        String username = subject.getPrincipal().toString();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        }

        List<UserBookFavorite> favorites = userBookFavoriteService.getFavoritesByUserId(user.getId());
        return ResultFactory.buildSuccessResult(favorites);
    }

    /**
     * Check if current user has favorited a book.
     *
     * @param bookId the book ID
     * @return result with boolean
     */
    @GetMapping("/api/favorites/check/{bookId}")
    public Result checkFavorite(@PathVariable("bookId") int bookId) {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return ResultFactory.buildSuccessResult(false);
        }

        String username = subject.getPrincipal().toString();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildSuccessResult(false);
        }

        boolean isFavorited = userBookFavoriteService.isFavorited(user.getId(), bookId);
        return ResultFactory.buildSuccessResult(isFavorited);
    }
}
