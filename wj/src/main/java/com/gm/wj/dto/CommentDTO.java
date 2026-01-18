package com.gm.wj.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CommentDTO {
    private int id;
    private String content;
    private Date createTime;
    private int noteId;
    private int userId;
    private String username;
    private Integer parentId;
    private String parentUsername;
    private List<CommentDTO> replies;
}
