package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * Book collection entity.
 *
 * @author Evan
 * @date 2024/03
 */
@Data
@Entity
@Table(name = "book_collection")
@ToString
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
public class BookCollection {
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
     * Collection time.
     */
    @Column(name = "collect_time")
    private Date collectTime;

    /**
     * Transient property for storing book information.
     */
    @Transient
    private Book book;
}
