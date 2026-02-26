package com.gm.wj.dto;

import com.gm.wj.entity.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Comment DTO with nested replies.
 *
 * @author Evan
 * @date 2026/2/26
 */
@Data
public class CommentDTO {
    private int id;
    private int articleId;
    private int userId;
    private String content;
    private Integer parentId;
    private Integer replyToUserId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private boolean deleted;
    private User user;
    private User replyToUser;
    private List<CommentDTO> replies;
}
