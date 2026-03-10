package com.gm.wj.service;

import com.gm.wj.dao.BookCollectionDAO;
import com.gm.wj.entity.BookCollection;
import com.gm.wj.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Book collection service.
 *
 * @author Evan
 * @date 2024/03
 */
@Service
public class BookCollectionService {
    @Autowired
    private BookCollectionDAO bookCollectionDAO;

    @Autowired
    private BookService bookService;

    /**
     * Add a book to collection.
     *
     * @param userId user id
     * @param bookId book id
     * @return BookCollection
     */
    public BookCollection addCollection(int userId, int bookId) {
        if (bookCollectionDAO.existsByUserIdAndBookId(userId, bookId)) {
            return null;
        }
        BookCollection collection = new BookCollection();
        collection.setUserId(userId);
        collection.setBookId(bookId);
        collection.setCollectTime(new Date());
        return bookCollectionDAO.save(collection);
    }

    /**
     * Remove a book from collection.
     *
     * @param userId user id
     * @param bookId book id
     */
    public void removeCollection(int userId, int bookId) {
        bookCollectionDAO.deleteByUserIdAndBookId(userId, bookId);
    }

    /**
     * Check if a book is collected by user.
     *
     * @param userId user id
     * @param bookId book id
     * @return boolean
     */
    public boolean isCollected(int userId, int bookId) {
        return bookCollectionDAO.existsByUserIdAndBookId(userId, bookId);
    }

    /**
     * Get all collections by user id.
     *
     * @param userId user id
     * @return List<BookCollection>
     */
    public List<BookCollection> listCollectionsByUserId(int userId) {
        List<BookCollection> collections = bookCollectionDAO.findByUserId(userId);
        for (BookCollection collection : collections) {
            Book book = bookService.findById(collection.getBookId());
            collection.setBook(book);
        }
        return collections;
    }
}
