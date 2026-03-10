package com.gm.wj.dao;

import com.gm.wj.entity.BookCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Book collection DAO.
 *
 * @author Evan
 * @date 2024/03
 */
public interface BookCollectionDAO extends JpaRepository<BookCollection, Integer> {

    /**
     * Find collection by user id and book id.
     *
     * @param userId user id
     * @param bookId book id
     * @return BookCollection
     */
    BookCollection findByUserIdAndBookId(int userId, int bookId);

    /**
     * Find all collections by user id.
     *
     * @param userId user id
     * @return List<BookCollection>
     */
    List<BookCollection> findByUserId(int userId);

    /**
     * Delete collection by user id and book id.
     *
     * @param userId user id
     * @param bookId book id
     */
    @Modifying
    @Transactional
    @Query("delete from BookCollection bc where bc.userId = ?1 and bc.bookId = ?2")
    void deleteByUserIdAndBookId(int userId, int bookId);

    /**
     * Check if a book is collected by user.
     *
     * @param userId user id
     * @param bookId book id
     * @return boolean
     */
    boolean existsByUserIdAndBookId(int userId, int bookId);
}
