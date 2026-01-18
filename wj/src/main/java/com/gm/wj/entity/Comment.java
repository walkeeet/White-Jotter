package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Date;

@Data
@Entity
@Table(name = "comment")
@ToString
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @NotNull(message = "id 不能为 null")
    private int id;

    @NotNull(message = "文章id不能为空")
    private int articleId;

    @NotNull(message = "用户id不能为空")
    private int userId;

    @NotEmpty(message = "评论内容不能为空")
    private String content;

    private Integer parentId;

    private Date commentDate;

    @Transient
    private User user;

    @Transient
    private Comment parentComment;
}