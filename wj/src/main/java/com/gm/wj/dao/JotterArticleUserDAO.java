package com.gm.wj.dao;

import com.gm.wj.entity.JotterArticleUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Evan
 * @date 2026/2/26
 */
public interface JotterArticleUserDAO extends JpaRepository<JotterArticleUser, Integer> {
    JotterArticleUser findByArticleId(int articleId);

    JotterArticleUser findByUserId(int userId);
}
