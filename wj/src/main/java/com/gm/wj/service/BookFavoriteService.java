package com.gm.wj.service;

import com.gm.wj.dao.BookFavoriteDAO;
import com.gm.wj.entity.Book;
import com.gm.wj.entity.BookFavorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Evan
 * @date 2019/4
 */
@Service
public class BookFavoriteService {
    @Autowired
    private BookFavoriteDAO bookFavoriteDAO;
    @Autowired
    private BookService bookService;

    public void addBookFavorite(int userId, int bookId) {
        if (bookFavoriteDAO.existsByUserIdAndBookId(userId, bookId)) {
            return;
        }
        BookFavorite bookFavorite = new BookFavorite();
        bookFavorite.setUserId(userId);
        bookFavorite.setBookId(bookId);
        bookFavorite.setFavoriteTime(new Date());
        bookFavoriteDAO.save(bookFavorite);
    }

    public void deleteBookFavorite(int userId, int bookId) {
        bookFavoriteDAO.deleteByUserIdAndBookId(userId, bookId);
    }

    public List<BookFavorite> listByUserId(int userId) {
        return bookFavoriteDAO.findAllByUserId(userId);
    }

    public List<Book> listFavoriteBooks(int userId) {
        List<BookFavorite> favorites = bookFavoriteDAO.findAllByUserId(userId);
        List<Integer> bookIds = favorites.stream()
                .map(BookFavorite::getBookId)
                .collect(Collectors.toList());
        return bookService.list().stream()
                .filter(book -> bookIds.contains(book.getId()))
                .collect(Collectors.toList());
    }

    public boolean isFavorite(int userId, int bookId) {
        return bookFavoriteDAO.existsByUserIdAndBookId(userId, bookId);
    }
}
