package com.gm.wj.controller;

import com.gm.wj.entity.User;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.UserLikeService;
import com.gm.wj.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LikeController {
    @Autowired
    UserLikeService userLikeService;

    @Autowired
    UserService userService;

    @GetMapping("/api/like/status/{articleId}")
    public Result getLikeStatus(@PathVariable("articleId") int articleId) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        
        if (username == null) {
            Map<String, Object> data = new HashMap<>();
            data.put("isLiked", false);
            data.put("likeCount", userLikeService.getLikeCount(articleId));
            return ResultFactory.buildSuccessResult(data);
        }
        
        User user = userService.findByUsername(username);
        boolean isLiked = userLikeService.isLiked(user.getId(), articleId);
        int likeCount = userLikeService.getLikeCount(articleId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("isLiked", isLiked);
        data.put("likeCount", likeCount);
        
        return ResultFactory.buildSuccessResult(data);
    }

    @PostMapping("/api/like/{articleId}")
    public Result like(@PathVariable("articleId") int articleId) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        
        if (username == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        
        User user = userService.findByUsername(username);
        userLikeService.like(user.getId(), articleId);
        
        boolean isLiked = userLikeService.isLiked(user.getId(), articleId);
        int likeCount = userLikeService.getLikeCount(articleId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("isLiked", isLiked);
        data.put("likeCount", likeCount);
        
        return ResultFactory.buildSuccessResult(data);
    }
}
