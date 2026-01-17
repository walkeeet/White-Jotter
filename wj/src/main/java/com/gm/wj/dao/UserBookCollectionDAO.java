package com.gm.wj.dao;

import com.gm.wj.entity.UserBookCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBookCollectionDAO extends JpaRepository<UserBookCollection, Integer> {
    List<UserBookCollection> findByUserId(int uid);
    UserBookCollection findByUserIdAndBookId(int uid, int bid);
    void deleteByUserIdAndBookId(int uid, int bid);
}
