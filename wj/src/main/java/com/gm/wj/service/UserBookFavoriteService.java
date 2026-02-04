package com.gm.wj.service;

import com.gm.wj.dao.UserBookFavoriteDAO;
import com.gm.wj.entity.UserBookFavorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * User book favorite service.
 *
 * @author Assistant
 * @date 2026/2
 */
@Service
public class UserBookFavoriteService {

    @Autowired
    private UserBookFavoriteDAO userBookFavoriteDAO;

    /**
     * Add a book to user's favorites.
     *
     * @param userId the user ID
     * @param bookId the book ID
     * @return the created favorite record
     */
    public UserBookFavorite addFavorite(int userId, int bookId) {
        if (userBookFavoriteDAO.existsByUserIdAndBookId(userId, bookId)) {
            return null;
        }
        UserBookFavorite favorite = new UserBookFavorite();
        favorite.setUserId(userId);
        favorite.setBookId(bookId);
        favorite.setFavoriteTime(new Date());
        return userBookFavoriteDAO.save(favorite);
    }

    /**
     * Remove a book from user's favorites.
     *
     * @param userId the user ID
     * @param bookId the book ID
     */
    public void removeFavorite(int userId, int bookId) {
        userBookFavoriteDAO.deleteByUserIdAndBookId(userId, bookId);
    }

    /**
     * Get all favorite books by user ID.
     *
     * @param userId the user ID
     * @return list of favorite records
     */
    public List<UserBookFavorite> getFavoritesByUserId(int userId) {
        return userBookFavoriteDAO.findAllByUserIdOrderByFavoriteTimeDesc(userId);
    }

    /**
     * Check if user has favorited a book.
     *
     * @param userId the user ID
     * @param bookId the book ID
     * @return true if favorited, false otherwise
     */
    public boolean isFavorited(int userId, int bookId) {
        return userBookFavoriteDAO.existsByUserIdAndBookId(userId, bookId);
    }
}
