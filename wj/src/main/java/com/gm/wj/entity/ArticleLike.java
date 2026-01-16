package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Date;

/**
 * 用户点赞记录实体类
 */
@Data
@Entity
@Table(name = "article_like")
@ToString
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class ArticleLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private int userId;

    /**
     * 文章ID
     */
    @Column(name = "article_id")
    private int articleId;

    /**
     * 操作类型：1-点赞，0-取消点赞
     */
    @Column(name = "action_type")
    private int actionType;

    /**
     * 操作时间
     */
    @Column(name = "create_time")
    private Date createTime;
}