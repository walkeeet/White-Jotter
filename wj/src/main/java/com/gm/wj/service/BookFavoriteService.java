package com.gm.wj.service;

import com.gm.wj.dao.BookFavoriteDAO;
import com.gm.wj.entity.Book;
import com.gm.wj.entity.BookFavorite;
import com.gm.wj.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookFavoriteService {

    @Autowired
    BookFavoriteDAO bookFavoriteDAO;

    public void addFavorite(User user, Book book) {
        if (bookFavoriteDAO.existsByUserAndBook(user, book)) {
            return;
        }
        BookFavorite favorite = new BookFavorite();
        favorite.setUser(user);
        favorite.setBook(book);
        bookFavoriteDAO.save(favorite);
    }

    @Transactional
    public void removeFavorite(User user, Book book) {
        bookFavoriteDAO.deleteByUserAndBook(user, book);
    }

    public List<Book> listFavorites(User user) {
        List<BookFavorite> favorites = bookFavoriteDAO.findByUser(user);
        return favorites.stream()
                .map(BookFavorite::getBook)
                .collect(Collectors.toList());
    }

    public boolean isFavorite(User user, Book book) {
        return bookFavoriteDAO.existsByUserAndBook(user, book);
    }
}
