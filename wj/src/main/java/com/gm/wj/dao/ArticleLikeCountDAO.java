package com.gm.wj.dao;

import com.gm.wj.entity.ArticleLikeCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleLikeCountDAO extends JpaRepository<ArticleLikeCount, Integer> {
    ArticleLikeCount findByArticleId(int articleId);
}
