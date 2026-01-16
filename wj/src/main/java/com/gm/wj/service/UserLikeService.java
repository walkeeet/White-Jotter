package com.gm.wj.service;

import com.gm.wj.dao.ArticleLikeCountDAO;
import com.gm.wj.dao.UserLikeDAO;
import com.gm.wj.entity.ArticleLikeCount;
import com.gm.wj.entity.UserLike;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
public class UserLikeService {
    @Autowired
    UserLikeDAO userLikeDAO;

    @Autowired
    ArticleLikeCountDAO articleLikeCountDAO;

    public boolean isLiked(int userId, int articleId) {
        UserLike userLike = userLikeDAO.findByUserIdAndArticleId(userId, articleId);
        return userLike != null && userLike.getOperationType() == 1;
    }

    public int getLikeCount(int articleId) {
        ArticleLikeCount count = articleLikeCountDAO.findByArticleId(articleId);
        return count == null ? 0 : count.getLikeCount();
    }

    @Transactional
    public void like(int userId, int articleId) {
        UserLike existingLike = userLikeDAO.findByUserIdAndArticleId(userId, articleId);
        
        if (existingLike != null) {
            if (existingLike.getOperationType() == 1) {
                userLikeDAO.delete(existingLike);
                decrementLikeCount(articleId);
            } else {
                existingLike.setOperationType(1);
                existingLike.setOperationTime(new Timestamp(System.currentTimeMillis()));
                userLikeDAO.save(existingLike);
                incrementLikeCount(articleId);
            }
        } else {
            UserLike userLike = new UserLike();
            userLike.setUserId(userId);
            userLike.setArticleId(articleId);
            userLike.setOperationType(1);
            userLike.setOperationTime(new Timestamp(System.currentTimeMillis()));
            userLikeDAO.save(userLike);
            incrementLikeCount(articleId);
        }
    }

    private void incrementLikeCount(int articleId) {
        ArticleLikeCount count = articleLikeCountDAO.findByArticleId(articleId);
        if (count == null) {
            count = new ArticleLikeCount();
            count.setArticleId(articleId);
            count.setLikeCount(1);
            articleLikeCountDAO.save(count);
        } else {
            count.setLikeCount(count.getLikeCount() + 1);
            articleLikeCountDAO.save(count);
        }
    }

    private void decrementLikeCount(int articleId) {
        ArticleLikeCount count = articleLikeCountDAO.findByArticleId(articleId);
        if (count != null && count.getLikeCount() > 0) {
            count.setLikeCount(count.getLikeCount() - 1);
            articleLikeCountDAO.save(count);
        }
    }
}
