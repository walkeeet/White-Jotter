package com.gm.wj.service;

import com.gm.wj.dao.BookCollectionDAO;
import com.gm.wj.entity.Book;
import com.gm.wj.entity.BookCollection;
import com.gm.wj.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookCollectionService {
    @Autowired
    private BookCollectionDAO bookCollectionDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    public List<Book> listCollectedBooksByUsername(String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return null;
        }
        List<BookCollection> collections = bookCollectionDAO.findByUser(user);
        return collections.stream()
                .map(BookCollection::getBook)
                .collect(Collectors.toList());
    }

    public boolean isBookCollected(String username, int bookId) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return false;
        }
        Book book = bookService.findById(bookId);
        if (book == null) {
            return false;
        }
        return bookCollectionDAO.existsByUserAndBook(user, book);
    }

    @Transactional
    public boolean collectBook(String username, int bookId) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return false;
        }
        
        Book book = bookService.findById(bookId);
        if (book == null) {
            return false;
        }
        
        if (bookCollectionDAO.existsByUserAndBook(user, book)) {
            return false;
        }
        
        BookCollection collection = new BookCollection();
        collection.setUser(user);
        collection.setBook(book);
        collection.setCollectTime(LocalDateTime.now());
        bookCollectionDAO.save(collection);
        return true;
    }

    @Transactional
    public boolean uncollectBook(String username, int bookId) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return false;
        }
        Book book = bookService.findById(bookId);
        if (book == null) {
            return false;
        }
        bookCollectionDAO.deleteByUserAndBook(user, book);
        return true;
    }

    public List<BookCollection> getUserCollections(String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return null;
        }
        return bookCollectionDAO.findByUser(user);
    }

    public List<BookCollection> listAllCollections() {
        return bookCollectionDAO.findAll(Sort.by(Sort.Direction.DESC, "collectTime"));
    }
}
