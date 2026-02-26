package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Comment entity for jotter articles.
 * Supports two-level nested replies.
 *
 * @author Evan
 * @date 2026/2/26
 */
@Data
@Entity
@Table(name = "jotter_comment")
@ToString
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
public class JotterComment {
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
     * User ID who made the comment.
     */
    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id")
    private int userId;

    /**
     * Comment content.
     */
    @NotEmpty(message = "评论内容不能为空")
    @Column(name = "content")
    private String content;

    /**
     * Parent comment ID.
     * Null for top-level comments.
     */
    @Column(name = "parent_id")
    private Integer parentId;

    /**
     * Target user ID for replies.
     * Used for second-level replies to specify who is being replied to.
     */
    @Column(name = "reply_to_user_id")
    private Integer replyToUserId;

    /**
     * Creation time.
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * Update time.
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * Whether the comment is deleted.
     * 0 - not deleted, 1 - deleted.
     */
    @Column(name = "is_deleted")
    private boolean deleted;

    /**
     * Transient property for storing user information.
     */
    @Transient
    private User user;

    /**
     * Transient property for storing reply target user information.
     */
    @Transient
    private User replyToUser;
}
