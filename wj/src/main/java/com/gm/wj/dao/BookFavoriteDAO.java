package com.gm.wj.dao;

import com.gm.wj.entity.Book;
import com.gm.wj.entity.BookFavorite;
import com.gm.wj.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookFavoriteDAO extends JpaRepository<BookFavorite, Integer> {

    List<BookFavorite> findByUser(User user);

    BookFavorite findByUserAndBook(User user, Book book);

    void deleteByUserAndBook(User user, Book book);

    boolean existsByUserAndBook(User user, Book book);
}
