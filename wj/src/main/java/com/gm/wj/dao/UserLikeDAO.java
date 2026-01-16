package com.gm.wj.dao;

import com.gm.wj.entity.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Evan
 * @date 2024/1/16
 */
public interface UserLikeDAO extends JpaRepository<UserLike, Integer> {
    List<UserLike> findByUserIdAndArticleId(int userId, int articleId);

    @Query(value = "SELECT * FROM user_like WHERE user_id = :userId AND article_id = :articleId ORDER BY operation_time DESC LIMIT 1", nativeQuery = true)
    UserLike findLatestByUserIdAndArticleId(@Param("userId") int userId, @Param("articleId") int articleId);
}
