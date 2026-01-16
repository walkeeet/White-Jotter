package com.gm.wj.dao;

import com.gm.wj.entity.ArticleLikeCount;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 文章点赞统计DAO
 */
public interface ArticleLikeCountDAO extends JpaRepository<ArticleLikeCount, Integer> {
    /**
     * 根据文章ID查找点赞统计
     */
    ArticleLikeCount findByArticleId(int articleId);
}