package com.gm.wj.dao;

import com.gm.wj.entity.Book;
import com.gm.wj.entity.BookCollection;
import com.gm.wj.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface BookCollectionDAO extends JpaRepository<BookCollection, Integer> {
    List<BookCollection> findByUser(User user);
    Optional<BookCollection> findByUserAndBook(User user, Book book);
    boolean existsByUserAndBook(User user, Book book);
    
    @Modifying
    @Transactional
    void deleteByUserAndBook(User user, Book book);
}
