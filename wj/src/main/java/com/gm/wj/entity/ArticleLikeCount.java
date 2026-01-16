package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "article_like_count")
@ToString
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class ArticleLikeCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "article_id")
    private int articleId;

    @Column(name = "like_count")
    private int likeCount;
}
