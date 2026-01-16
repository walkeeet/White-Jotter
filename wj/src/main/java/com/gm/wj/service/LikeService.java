package com.gm.wj.service;

import com.gm.wj.dao.ArticleLikeCountDAO;
import com.gm.wj.dao.UserLikeDAO;
import com.gm.wj.entity.ArticleLikeCount;
import com.gm.wj.entity.UserLike;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Evan
 * @date 2024/1/16
 */
@Service
public class LikeService {
    @Autowired
    UserLikeDAO userLikeDAO;
    @Autowired
    ArticleLikeCountDAO articleLikeCountDAO;
    @Autowired
    UserService userService;

    public int toggleLike(int articleId) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (username == null) {
            return -1;
        }

        com.gm.wj.entity.User user = userService.findByUsername(username);
        UserLike latestLike = userLikeDAO.findLatestByUserIdAndArticleId(user.getId(), articleId);

        Timestamp now = new Timestamp(new Date().getTime());
        UserLike newLike = new UserLike();
        newLike.setUserId(user.getId());
        newLike.setArticleId(articleId);
        newLike.setOperationTime(now);

        ArticleLikeCount articleLikeCount = articleLikeCountDAO.findByArticleId(articleId);
        if (articleLikeCount == null) {
            articleLikeCount = new ArticleLikeCount();
            articleLikeCount.setArticleId(articleId);
            articleLikeCount.setLikeCount(0);
        }

        if (latestLike == null) {
            newLike.setOperationType(1);
            userLikeDAO.save(newLike);
            articleLikeCount.setLikeCount(articleLikeCount.getLikeCount() + 1);
            articleLikeCountDAO.save(articleLikeCount);
            return 1;
        } else {
            if (latestLike.getOperationType() == 1) {
                newLike.setOperationType(0);
                userLikeDAO.save(newLike);
                articleLikeCount.setLikeCount(articleLikeCount.getLikeCount() - 1);
                if (articleLikeCount.getLikeCount() < 0) {
                    articleLikeCount.setLikeCount(0);
                }
                articleLikeCountDAO.save(articleLikeCount);
                return 0;
            } else {
                newLike.setOperationType(1);
                userLikeDAO.save(newLike);
                articleLikeCount.setLikeCount(articleLikeCount.getLikeCount() + 1);
                articleLikeCountDAO.save(articleLikeCount);
                return 1;
            }
        }
    }

    public int getLikeCount(int articleId) {
        ArticleLikeCount articleLikeCount = articleLikeCountDAO.findByArticleId(articleId);
        if (articleLikeCount == null) {
            return 0;
        }
        return articleLikeCount.getLikeCount();
    }

    public int getUserLikeStatus(int articleId) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        if (username == null) {
            return -1;
        }

        com.gm.wj.entity.User user = userService.findByUsername(username);
        UserLike latestLike = userLikeDAO.findLatestByUserIdAndArticleId(user.getId(), articleId);

        if (latestLike == null) {
            return 0;
        }
        return latestLike.getOperationType();
    }
}
