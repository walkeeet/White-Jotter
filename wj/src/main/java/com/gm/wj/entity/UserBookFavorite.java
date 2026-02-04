package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * User book favorite entity.
 * Represents a user's favorite book collection.
 *
 * @author Assistant
 * @date 2026/2
 */
@Data
@Entity
@Table(name = "user_book_favorite")
@ToString
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class UserBookFavorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    /**
     * User ID who favorited the book.
     */
    @Column(name = "user_id")
    private int userId;

    /**
     * Book ID that was favorited.
     */
    @Column(name = "book_id")
    private int bookId;

    /**
     * Time when the book was favorited.
     */
    @Column(name = "favorite_time")
    private Date favoriteTime;

    /**
     * Associated book entity (for query convenience).
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", insertable = false, updatable = false)
    private Book book;
}
