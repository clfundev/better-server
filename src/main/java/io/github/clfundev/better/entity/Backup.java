package io.github.clfundev.better.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.clfundev.better.common.InstantToUtcDatetimeHandler;
import lombok.Data;

import java.time.Instant;

@Data
@TableName(value = "tb_backup", autoResultMap = true)
public class Backup {

    /**
     * 备份ID（32位无连字符UUID）
     */
    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 所属用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 文件大小（字节）
     */
    private Long size;

    /**
     * 创建时间（UTC）
     */
    @TableField(value = "created_at", typeHandler = InstantToUtcDatetimeHandler.class)
    private Instant createdAt;
}
