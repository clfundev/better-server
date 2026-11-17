package io.github.clfundev.better.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class UserVo {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 个人简介
     */
    private String profile;

    /**
     * 头像文件相对路径
     */
    private String avatar;

    /**
     * 注册时间
     */
    private Instant createdAt;
}
