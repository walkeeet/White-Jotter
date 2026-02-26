package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * User book favorite entity.
 *
 * @author Evan
 * @date 2019/4
 */
@Data
@Entity
@Table(name = "book_favorite")
@ToString
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
public class BookFavorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    /**
     * User id.
     */
    @Column(name = "user_id")
    private int userId;

    /**
     * Book id.
     */
    @Column(name = "book_id")
    private int bookId;

    /**
     * Favorite time.
     */
    @Column(name = "favorite_time")
    private Date favoriteTime;
}
