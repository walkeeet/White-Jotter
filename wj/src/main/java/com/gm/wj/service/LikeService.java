package com.gm.wj.service;

import com.gm.wj.dao.ArticleLikeCountDAO;
import com.gm.wj.dao.ArticleLikeDAO;
import com.gm.wj.entity.ArticleLike;
import com.gm.wj.entity.ArticleLikeCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Calendar;

/**
 * 点赞服务
 */
@Service
public class LikeService {
    @Autowired
    ArticleLikeDAO articleLikeDAO;

    @Autowired
    ArticleLikeCountDAO articleLikeCountDAO;

    /**
     * 点赞或取消点赞
     *
     * @param userId    用户ID
     * @param articleId 文章ID
     * @return 操作结果：1-点赞成功，0-取消点赞成功
     */
    public int toggleLike(int userId, int articleId) {
        // 查找是否已经有点赞记录
        ArticleLike existingLike = articleLikeDAO.findByUserIdAndArticleId(userId, articleId);

        if (existingLike == null) {
            // 没有点赞记录，新建点赞记录
            ArticleLike like = new ArticleLike();
            like.setUserId(userId);
            like.setArticleId(articleId);
            like.setActionType(1); // 点赞
            like.setCreateTime(new Date(Calendar.getInstance().getTimeInMillis()));
            articleLikeDAO.save(like);

            // 更新点赞统计
            updateLikeCount(articleId, 1);

            return 1; // 点赞成功
        } else {
            // 已经有点赞记录，切换状态
            if (existingLike.getActionType() == 1) {
                // 当前是点赞状态，改为取消点赞
                existingLike.setActionType(0);
                existingLike.setCreateTime(new Date(Calendar.getInstance().getTimeInMillis()));
                articleLikeDAO.save(existingLike);

                // 更新点赞统计
                updateLikeCount(articleId, -1);

                return 0; // 取消点赞成功
            } else {
                // 当前是取消点赞状态，改为点赞
                existingLike.setActionType(1);
                existingLike.setCreateTime(new Date(Calendar.getInstance().getTimeInMillis()));
                articleLikeDAO.save(existingLike);

                // 更新点赞统计
                updateLikeCount(articleId, 1);

                return 1; // 点赞成功
            }
        }
    }

    /**
     * 更新点赞统计
     *
     * @param articleId 文章ID
     * @param delta     变化量（1或-1）
     */
    private void updateLikeCount(int articleId, int delta) {
        ArticleLikeCount count = articleLikeCountDAO.findByArticleId(articleId);

        if (count == null) {
            // 没有统计记录，新建
            count = new ArticleLikeCount();
            count.setArticleId(articleId);
            count.setLikeCount(delta);
        } else {
            // 已有统计记录，更新数量
            int newCount = count.getLikeCount() + delta;
            count.setLikeCount(newCount > 0 ? newCount : 0); // 保证点赞数不小于0
        }

        articleLikeCountDAO.save(count);
    }

    /**
     * 获取文章点赞数
     *
     * @param articleId 文章ID
     * @return 点赞数
     */
    public int getLikeCount(int articleId) {
        ArticleLikeCount count = articleLikeCountDAO.findByArticleId(articleId);
        return count != null ? count.getLikeCount() : 0;
    }

    /**
     * 检查用户是否已点赞
     *
     * @param userId    用户ID
     * @param articleId 文章ID
     * @return 是否已点赞
     */
    public boolean hasLiked(int userId, int articleId) {
        ArticleLike like = articleLikeDAO.findByUserIdAndArticleId(userId, articleId);
        return like != null && like.getActionType() == 1;
    }
}