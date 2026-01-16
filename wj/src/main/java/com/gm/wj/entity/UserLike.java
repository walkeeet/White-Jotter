package com.gm.wj.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;

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

    @Column(name = "user_id")
    private int userId;

    @Column(name = "article_id")
    private int articleId;

    @Column(name = "operation_type")
    private int operationType;

    @Column(name = "operation_time")
    private Timestamp operationTime;
}
