package com.gm.wj.dao;

import com.gm.wj.entity.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 用户点赞记录DAO
 */
public interface ArticleLikeDAO extends JpaRepository<ArticleLike, Integer> {
    /**
     * 根据用户ID和文章ID查找点赞记录
     */
    ArticleLike findByUserIdAndArticleId(int userId, int articleId);

    /**
     * 根据文章ID统计点赞数
     */
    int countByArticleIdAndActionType(int articleId, int actionType);
}