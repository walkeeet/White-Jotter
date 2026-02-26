package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * Article comment entity.
 * Support two-level replies.
 *
 * @author System
 * @date 2026/02/26
 */
@Data
@Entity
@Table(name = "article_comment")
@ToString
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class ArticleComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotEmpty(message = "评论内容不能为空")
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "article_id")
    private int articleId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "username")
    private String username;

    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "reply_to_user_id")
    private Integer replyToUserId;

    @Column(name = "reply_to_username")
    private String replyToUsername;

    @Column(name = "create_time")
    private Date createTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
    }
}
