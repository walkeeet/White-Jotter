package com.gm.wj.controller;

import com.gm.wj.entity.User;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.LikeService;
import com.gm.wj.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 点赞控制器
 */
@RestController
public class LikeController {
    @Autowired
    LikeService likeService;

    @Autowired
    UserService userService;

    /**
     * 点赞或取消点赞
     */
    @PostMapping("/api/article/like/{articleId}")
    public Result toggleLike(@PathVariable("articleId") int articleId) {
        // 获取当前登录用户
        Subject subject = SecurityUtils.getSubject();
        String username = subject.getPrincipal() == null ? null : subject.getPrincipal().toString();

        if (username == null) {
            return ResultFactory.buildFailResult("请先登录");
        }

        // 获取用户ID
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        }

        // 切换点赞状态
        int result = likeService.toggleLike(user.getId(), articleId);

        // 获取最新的点赞数
        int likeCount = likeService.getLikeCount(articleId);
        boolean hasLiked = likeService.hasLiked(user.getId(), articleId);

        Map<String, Object> data = new HashMap<>();
        data.put("action", result == 1 ? "liked" : "unliked");
        data.put("likeCount", likeCount);
        data.put("hasLiked", hasLiked);

        return ResultFactory.buildSuccessResult(data);
    }

    /**
     * 获取文章点赞信息（点赞数和当前用户是否点赞）
     */
    @GetMapping("/api/article/like/{articleId}")
    public Result getLikeInfo(@PathVariable("articleId") int articleId) {
        // 获取点赞数
        int likeCount = likeService.getLikeCount(articleId);

        // 获取当前登录用户
        Subject subject = SecurityUtils.getSubject();
        String username = subject.getPrincipal() == null ? null : subject.getPrincipal().toString();

        Map<String, Object> data = new HashMap<>();
        data.put("likeCount", likeCount);

        if (username != null) {
            // 获取用户ID
            User user = userService.findByUsername(username);
            if (user != null) {
                // 检查是否已点赞
                boolean hasLiked = likeService.hasLiked(user.getId(), articleId);
                data.put("hasLiked", hasLiked);
            } else {
                data.put("hasLiked", false);
            }
        } else {
            data.put("hasLiked", false);
        }

        return ResultFactory.buildSuccessResult(data);
    }
}