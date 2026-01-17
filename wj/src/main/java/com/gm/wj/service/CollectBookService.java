package com.gm.wj.service;

import com.gm.wj.dao.BookDAO;
import com.gm.wj.dao.CollectBookDAO;
import com.gm.wj.dao.UserDAO;
import com.gm.wj.entity.Book;
import com.gm.wj.entity.CollectBook;
import com.gm.wj.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CollectBookService {
    @Autowired
    CollectBookDAO collectBookDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    BookDAO bookDAO;

    public CollectBook addCollectBook(String username, int bookId) {
        User user = userDAO.findByUsername(username);
        Book book = bookDAO.findById(bookId).orElse(null);
        
        if (user == null || book == null) {
            return null;
        }
        
        CollectBook collectBook = new CollectBook();
        collectBook.setUser(user);
        collectBook.setBook(book);
        collectBook.setCollectTime(new Date());
        
        return collectBookDAO.save(collectBook);
    }

    public void deleteCollectBook(String username, int bookId) {
        User user = userDAO.findByUsername(username);
        Book book = bookDAO.findById(bookId).orElse(null);
        
        if (user != null && book != null) {
            collectBookDAO.deleteByUserAndBook(user, book);
        }
    }

    public boolean isCollected(String username, int bookId) {
        User user = userDAO.findByUsername(username);
        Book book = bookDAO.findById(bookId).orElse(null);
        
        if (user == null || book == null) {
            return false;
        }
        
        return collectBookDAO.findByUserAndBook(user, book) != null;
    }

    public List<CollectBook> listByUser(String username) {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            return null;
        }
        return collectBookDAO.findAllByUser(user);
    }
}
