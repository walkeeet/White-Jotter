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
@Table(name = "jotter_note")
@ToString
@JsonIgnoreProperties({"handler", "hibernateLazyInitializer"})
public class JotterNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @NotNull(message = "id 不能为 null")
    private int id;

    @Column(name = "user_id")
    @NotNull(message = "用户 id 不能为 null")
    private int userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @NotEmpty(message = "笔记标题不能为空")
    @Column(name = "title")
    private String title;

    @Column(name = "content_html", columnDefinition = "longtext")
    private String contentHtml;

    @Column(name = "content_md", columnDefinition = "longtext")
    private String contentMd;

    @Column(name = "note_abstract")
    private String noteAbstract;

    @Column(name = "cover")
    private String cover;

    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "update_time")
    private Timestamp updateTime;

    @Transient
    private int commentCount;
}
