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
     * 是否启用：1-启用；0-禁用
     */
    private Integer enabled;

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
     * 创建时间
     */
    private Instant createdAt;

    /**
     * 更新时间
     */
    private Instant updatedAt;
}
