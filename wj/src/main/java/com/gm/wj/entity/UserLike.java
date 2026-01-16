package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * User like record entity.
 *
 * @author Evan
 * @date 2024/1/16
 */
@Data
@Entity
@Table(name = "user_like")
@ToString
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class UserLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    /**
     * User id.
     */
    private int userId;

    /**
     * Article id.
     */
    private int articleId;

    /**
     * Operation type: 1-like, 0-cancel like.
     */
    private int operationType;

    /**
     * Operation time.
     */
    private Timestamp operationTime;
}
