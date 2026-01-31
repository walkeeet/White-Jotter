package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uid")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bid")
    private Book book;

    @Column(name = "favorite_time")
    private LocalDateTime favoriteTime;

    @PrePersist
    public void prePersist() {
        if (favoriteTime == null) {
            favoriteTime = LocalDateTime.now();
        }
    }
}
