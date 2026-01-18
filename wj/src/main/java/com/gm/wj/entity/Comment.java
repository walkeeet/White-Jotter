package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.sql.Date;

import static javax.persistence.FetchType.LAZY;

/**
 * Comment entity.
 *
 * @author Evan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comment")
@ToString
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "article_id")
    @JsonIgnoreProperties("author")
    private JotterArticle article;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"password", "salt", "roles"})
    private User user;

    @NotEmpty(message = "评论内容不能为空")
    @Column(columnDefinition = "TEXT")
    private String content;

    private Date createDate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties({"article", "parentComment", "replyComments"})
    private Comment parentComment;

    @Transient
    private int replyCount;
}