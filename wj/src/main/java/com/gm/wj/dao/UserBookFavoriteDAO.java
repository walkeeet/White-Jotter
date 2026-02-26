package com.gm.wj.dao;

import com.gm.wj.entity.UserBookFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Evan
 * @date 2024
 */
public interface UserBookFavoriteDAO extends JpaRepository<UserBookFavorite,Integer> {
    List<UserBookFavorite> findAllByUid(int uid);
    UserBookFavorite findByUidAndBid(int uid, int bid);
    void deleteByUidAndBid(int uid, int bid);
}
