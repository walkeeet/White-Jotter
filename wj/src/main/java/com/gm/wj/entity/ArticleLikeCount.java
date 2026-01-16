package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

/**
 * Article like count entity.
 *
 * @author Evan
 * @date 2024/1/16
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
     * Article id.
     */
    private int articleId;

    /**
     * Total like count.
     */
    private int likeCount;
}
