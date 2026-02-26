package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

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

    @ManyToOne
    @JoinColumn(name = "article_id")
    @NotNull(message = "文章id不能为空")
    private JotterArticle article;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "用户id不能为空")
    private User user;

    @Column(name = "parent_id")
    private int parentId;

    @ManyToOne
    @JoinColumn(name = "reply_to_user_id")
    private User replyToUser;

    @NotEmpty(message = "评论内容不能为空")
    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name = "create_time")
    private Timestamp createTime;
}
