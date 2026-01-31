package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@Table(name = "jotter_comment")
@ToString
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
public class JotterComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @NotNull(message = "id 不能为 null")
    private int id;

    @Column(name = "article_id")
    @NotNull(message = "文章 id 不能为 null")
    private int articleId;

    @Column(name = "user_id")
    @NotNull(message = "用户 id 不能为 null")
    private int userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "parent_id")
    private Integer parentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private JotterComment parent;

    @Column(name = "reply_user_id")
    private Integer replyUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_user_id", insertable = false, updatable = false)
    private User replyUser;

    @NotEmpty(message = "评论内容不能为空")
    @Column(columnDefinition = "text")
    private String content;

    @Column(name = "create_time")
    private Timestamp createTime;

    @Transient
    private List<JotterComment> children;
}
