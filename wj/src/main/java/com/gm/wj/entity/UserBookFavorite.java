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
 * @date 2024
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

    @Column(name = "uid")
    private int uid;

    @Column(name = "bid")
    private int bid;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;
}
