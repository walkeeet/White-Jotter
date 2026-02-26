package com.gm.wj.dao;

import com.gm.wj.entity.BookFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Evan
 * @date 2019/4
 */
public interface BookFavoriteDAO extends JpaRepository<BookFavorite, Integer> {
    List<BookFavorite> findAllByUserId(int userId);

    BookFavorite findByUserIdAndBookId(int userId, int bookId);

    void deleteByUserIdAndBookId(int userId, int bookId);

    boolean existsByUserIdAndBookId(int userId, int bookId);
}
