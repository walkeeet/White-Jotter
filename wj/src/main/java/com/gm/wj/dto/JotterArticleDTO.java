package com.gm.wj.dto;

import com.gm.wj.entity.JotterArticle;
import com.gm.wj.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Jotter Article DTO with author information and comment count.
 *
 * @author Evan
 * @date 2026/2/26
 */
@Data
public class JotterArticleDTO {
    private int id;
    private String articleTitle;
    private String articleContentHtml;
    private String articleContentMd;
    private String articleAbstract;
    private String articleCover;
    private LocalDateTime articleDate;
    private User author;
    private long commentCount;
}
