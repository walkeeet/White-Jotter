package com.gm.wj.service;

import com.gm.wj.dao.BookDAO;
import com.gm.wj.dao.UserBookCollectDAO;
import com.gm.wj.dao.UserDAO;
import com.gm.wj.entity.Book;
import com.gm.wj.entity.User;
import com.gm.wj.entity.UserBookCollect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserBookCollectService {
    @Autowired
    UserBookCollectDAO userBookCollectDAO;
    
    @Autowired
    UserDAO userDAO;
    
    @Autowired
    BookDAO bookDAO;

    public List<UserBookCollect> getAllByUserId(int userId) {
        return userBookCollectDAO.findAllByUserId(userId);
    }

    public UserBookCollect collectBook(int userId, int bookId) {
        UserBookCollect existing = userBookCollectDAO.findByUserIdAndBookId(userId, bookId);
        if (existing != null) {
            return existing;
        }
        
        UserBookCollect collect = new UserBookCollect();
        User user = userDAO.findById(userId).orElse(null);
        Book book = bookDAO.findById(bookId).orElse(null);
        
        if (user != null && book != null) {
            collect.setUser(user);
            collect.setBook(book);
            collect.setCollectTime(new Date());
            return userBookCollectDAO.save(collect);
        }
        return null;
    }

    public void cancelCollect(int userId, int bookId) {
        userBookCollectDAO.deleteByUserIdAndBookId(userId, bookId);
    }

    public boolean isCollected(int userId, int bookId) {
        return userBookCollectDAO.findByUserIdAndBookId(userId, bookId) != null;
    }
}
