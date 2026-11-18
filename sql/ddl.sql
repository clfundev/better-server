CREATE DATABASE IF NOT EXISTS `better` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

use better;

CREATE TABLE `tb_user`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `username`   VARCHAR(50)  NOT NULL,
    `password`   VARCHAR(100) NOT NULL,
    `enabled`    TINYINT      NOT NULL DEFAULT 1 COMMENT '是否启用：1-启用；0-禁用',
    `nickname`   VARCHAR(50)  NOT NULL COMMENT '昵称',
    `profile`    VARCHAR(255) NULL COMMENT '个人简介',
    `avatar`     VARCHAR(255) NULL COMMENT '头像文件相对路径',
    `created_at` DATETIME     NOT NULL COMMENT '创建时间（UTC）',
    `updated_at` DATETIME     NOT NULL COMMENT '更新时间（UTC）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `tb_refresh_token`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`    BIGINT       NOT NULL,
    `token_hash` VARCHAR(100) NOT NULL,
    `revoked`    TINYINT      NOT NULL DEFAULT 0 COMMENT '是否被吊销：0-有效；1-被吊销',
    `expires_at` DATETIME     NOT NULL COMMENT '过期时间（UTC）',
    `created_at` DATETIME     NOT NULL COMMENT '签发时间（UTC）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_token_hash` (`token_hash`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_expires_at` (`expires_at`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE `tb_backup`
(
    `id`         CHAR(32) NOT NULL COMMENT '备份ID（32位无连字符UUID）',
    `user_id`    BIGINT   NOT NULL,
    `size`       BIGINT   NOT NULL COMMENT '文件大小（字节）',
    `created_at` DATETIME NOT NULL COMMENT '创建时间（UTC）',
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;
