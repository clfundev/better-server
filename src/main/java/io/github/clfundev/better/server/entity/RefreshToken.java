package io.github.clfundev.better.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.clfundev.better.server.common.InstantToUtcDatetimeHandler;
import lombok.Data;

import java.time.Instant;

@Data
@TableName(value = "tb_refresh_token", autoResultMap = true)
public class RefreshToken {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属用户ID，可用于按用户批量吊销
     */
    @TableField("user_id")
    private Long userId;

    /**
     * RefreshToken 的 SHA-256 十六进制哈希
     */
    @TableField("token_hash")
    private String tokenHash;

    /**
     * 是否被吊销：0-有效；1-被吊销
     */
    @TableField("revoked")
    private Integer revoked;

    /**
     * 过期时间
     */
    @TableField(value = "expires_at", typeHandler = InstantToUtcDatetimeHandler.class)
    private Instant expiresAt;

    /**
     * 签发时间
     */
    @TableField(value = "created_at", typeHandler = InstantToUtcDatetimeHandler.class)
    private Instant createdAt;
}