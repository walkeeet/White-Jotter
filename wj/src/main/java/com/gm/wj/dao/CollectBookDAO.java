package com.gm.wj.dao;

import com.gm.wj.entity.Book;
import com.gm.wj.entity.CollectBook;
import com.gm.wj.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectBookDAO extends JpaRepository<CollectBook, Integer> {
    CollectBook findByUserAndBook(User user, Book book);
    List<CollectBook> findAllByUser(User user);
    void deleteByUserAndBook(User user, Book book);
}
