package com.gm.wj.service;

import com.gm.wj.dao.BookDAO;
import com.gm.wj.dao.UserBookFavoriteDAO;
import com.gm.wj.entity.Book;
import com.gm.wj.entity.UserBookFavorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Evan
 * @date 2024
 */
@Service
public class UserBookFavoriteService {
    @Autowired
    private UserBookFavoriteDAO userBookFavoriteDAO;
    @Autowired
    private BookDAO bookDAO;

    public void addFavorite(int uid, int bid) {
        UserBookFavorite favorite = new UserBookFavorite();
        favorite.setUid(uid);
        favorite.setBid(bid);
        favorite.setCreateTime(new Date());
        userBookFavoriteDAO.save(favorite);
    }

    public void removeFavorite(int uid, int bid) {
        userBookFavoriteDAO.deleteByUidAndBid(uid, bid);
    }

    public boolean isFavorite(int uid, int bid) {
        return userBookFavoriteDAO.findByUidAndBid(uid, bid) != null;
    }

    public List<Book> listFavoriteBooks(int uid) {
        List<UserBookFavorite> favorites = userBookFavoriteDAO.findAllByUid(uid);
        List<Book> books = new ArrayList<>();
        for (UserBookFavorite favorite : favorites) {
            Book book = bookDAO.findById(favorite.getBid()).orElse(null);
            if (book != null) {
                books.add(book);
            }
        }
        return books;
    }

    public List<Integer> listFavoriteBookIds(int uid) {
        List<UserBookFavorite> favorites = userBookFavoriteDAO.findAllByUid(uid);
        List<Integer> bookIds = new ArrayList<>();
        for (UserBookFavorite favorite : favorites) {
            bookIds.add(favorite.getBid());
        }
        return bookIds;
    }
}
