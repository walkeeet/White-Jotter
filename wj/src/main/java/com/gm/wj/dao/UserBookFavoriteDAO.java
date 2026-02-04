package com.gm.wj.dao;

import com.gm.wj.entity.UserBookFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * User book favorite DAO.
 *
 * @author Assistant
 * @date 2026/2
 */
public interface UserBookFavoriteDAO extends JpaRepository<UserBookFavorite, Integer> {

    /**
     * Find all favorite records by user ID.
     *
     * @param userId the user ID
     * @return list of favorite records
     */
    List<UserBookFavorite> findAllByUserIdOrderByFavoriteTimeDesc(int userId);

    /**
     * Find favorite record by user ID and book ID.
     *
     * @param userId the user ID
     * @param bookId the book ID
     * @return favorite record if exists, null otherwise
     */
    UserBookFavorite findByUserIdAndBookId(int userId, int bookId);

    /**
     * Delete favorite record by user ID and book ID.
     *
     * @param userId the user ID
     * @param bookId the book ID
     */
    void deleteByUserIdAndBookId(int userId, int bookId);

    /**
     * Check if user has favorited a book.
     *
     * @param userId the user ID
     * @param bookId the book ID
     * @return true if favorited, false otherwise
     */
    boolean existsByUserIdAndBookId(int userId, int bookId);
}
