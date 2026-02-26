package com.gm.wj.service;

import com.gm.wj.dao.BookCollectionDAO;
import com.gm.wj.entity.Book;
import com.gm.wj.entity.BookCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class BookCollectionService {
    @Autowired
    private BookCollectionDAO bookCollectionDAO;
    @Autowired
    private BookService bookService;

    public List<BookCollection> listByUid(int uid) {
        List<BookCollection> collections = bookCollectionDAO.findAllByUid(uid);
        for (BookCollection collection : collections) {
            Book book = bookService.list().stream()
                    .filter(b -> b.getId() == collection.getBid())
                    .findFirst()
                    .orElse(null);
            collection.setBook(book);
        }
        return collections;
    }

    public boolean isCollected(int uid, int bid) {
        return bookCollectionDAO.existsByUidAndBid(uid, bid);
    }

    public void addCollection(int uid, int bid) {
        if (!isCollected(uid, bid)) {
            BookCollection collection = new BookCollection();
            collection.setUid(uid);
            collection.setBid(bid);
            collection.setCollectTime(new Date());
            bookCollectionDAO.save(collection);
        }
    }

    @Transactional
    public void removeCollection(int uid, int bid) {
        bookCollectionDAO.deleteByUidAndBid(uid, bid);
    }
}
