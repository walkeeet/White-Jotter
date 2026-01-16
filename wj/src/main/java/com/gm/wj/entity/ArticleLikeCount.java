package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

/**
 * 文章点赞统计实体类
 */
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

    /**
     * 文章ID
     */
    @Column(name = "article_id")
    private int articleId;

    /**
     * 点赞总数
     */
    @Column(name = "like_count")
    private int likeCount;
}