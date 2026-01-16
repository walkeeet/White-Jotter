package com.gm.wj.dao;

import com.gm.wj.entity.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLikeDAO extends JpaRepository<UserLike, Integer> {
    UserLike findByUserIdAndArticleId(int userId, int articleId);
}
