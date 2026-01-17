package com.gm.wj.dao;

import com.gm.wj.entity.UserBookCollect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBookCollectDAO extends JpaRepository<UserBookCollect, Integer> {
    List<UserBookCollect> findAllByUserId(int userId);
    UserBookCollect findByUserIdAndBookId(int userId, int bookId);
    void deleteByUserIdAndBookId(int userId, int bookId);
}
