package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Comment entity for Jotter articles.
 *
 * @author Assistant
 * @date 2026-02-04
 */
@Data
@Entity
@Table(name = "jotter_comment")
@ToString
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class JotterComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @NotNull(message = "id 不能为 null")
    private int id;

    /**
     * Article id that this comment belongs to.
     */
    @NotNull(message = "文章ID不能为空")
    private Integer articleId;

    /**
     * User id who made this comment.
     */
    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    /**
     * Parent comment id for nested replies.
     * Null if it's a top-level comment.
     */
    private Integer parentId;

    /**
     * Comment content.
     */
    @NotEmpty(message = "评论内容不能为空")
    private String content;

    /**
     * Comment create time.
     */
    private LocalDateTime createTime;

    /**
     * Transient property for storing user information.
     */
    @Transient
    private User user;

    /**
     * Transient property for storing parent comment user name.
     */
    @Transient
    private String parentUserName;

    /**
     * Transient property for storing child comments (replies).
     */
    @Transient
    private List<JotterComment> replies;
}
