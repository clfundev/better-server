package io.github.clfundev.better.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.clfundev.better.server.common.InstantToUtcDatetimeHandler;
import lombok.Data;

import java.time.Instant;

@Data
@TableName(value = "tb_user", autoResultMap = true)
public class User {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

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
    @TableField(value = "created_at", typeHandler = InstantToUtcDatetimeHandler.class)
    private Instant createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", typeHandler = InstantToUtcDatetimeHandler.class)
    private Instant updatedAt;
}