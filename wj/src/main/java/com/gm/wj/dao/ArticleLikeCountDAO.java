package com.gm.wj.dao;

import com.gm.wj.entity.ArticleLikeCount;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Evan
 * @date 2024/1/16
 */
public interface ArticleLikeCountDAO extends JpaRepository<ArticleLikeCount, Integer> {
    ArticleLikeCount findByArticleId(int articleId);
}
