package com.gm.wj.dao;

import com.gm.wj.entity.BookCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookCollectionDAO extends JpaRepository<BookCollection, Integer> {
    List<BookCollection> findAllByUid(int uid);
    BookCollection findByUidAndBid(int uid, int bid);
    void deleteByUidAndBid(int uid, int bid);
    boolean existsByUidAndBid(int uid, int bid);
}
