package com.gm.wj.dto;

import lombok.Data;

import java.util.Date;

@Data
public class NoteDTO {
    private int id;
    private String title;
    private String content;
    private Date createTime;
    private Date updateTime;
    private int commentCount;
    private int userId;
    private String username;
}
