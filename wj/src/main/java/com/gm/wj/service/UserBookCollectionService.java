package com.gm.wj.service;

import com.gm.wj.dao.BookDAO;
import com.gm.wj.dao.UserBookCollectionDAO;
import com.gm.wj.dao.UserDAO;
import com.gm.wj.entity.Book;
import com.gm.wj.entity.User;
import com.gm.wj.entity.UserBookCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserBookCollectionService {

    @Autowired
    private UserBookCollectionDAO userBookCollectionDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private BookDAO bookDAO;

    public void addCollection(int uid, int bid) {
        Optional<User> userOptional = userDAO.findById(uid);
        Optional<Book> bookOptional = bookDAO.findById(bid);

        if (userOptional.isPresent() && bookOptional.isPresent()) {
            User user = userOptional.get();
            Book book = bookOptional.get();
            UserBookCollection collection = new UserBookCollection();
            collection.setUser(user);
            collection.setBook(book);
            collection.setCollectionTime(new Date());
            userBookCollectionDAO.save(collection);
        }
    }

    public void removeCollection(int uid, int bid) {
        userBookCollectionDAO.deleteByUserIdAndBookId(uid, bid);
    }

    public List<UserBookCollection> getCollectionsByUserId(int uid) {
        return userBookCollectionDAO.findByUserId(uid);
    }

    public boolean isCollected(int uid, int bid) {
        return userBookCollectionDAO.findByUserIdAndBookId(uid, bid) != null;
    }
}
