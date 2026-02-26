package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Article-User relationship entity.
 * Records the author of each article.
 *
 * @author Evan
 * @date 2026/2/26
 */
@Data
@Entity
@Table(name = "jotter_article_user")
@ToString
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
public class JotterArticleUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    /**
     * Article ID.
     */
    @NotNull(message = "文章ID不能为空")
    @Column(name = "article_id")
    private int articleId;

    /**
     * User ID (author).
     */
    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id")
    private int userId;
}
