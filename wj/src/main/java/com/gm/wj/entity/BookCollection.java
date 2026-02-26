package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

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

    @Column(name = "uid")
    private int uid;

    @Column(name = "bid")
    private int bid;

    @Column(name = "collect_time")
    private Date collectTime;

    @Transient
    private Book book;
}
