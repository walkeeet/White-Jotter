-- 笔记本模块评论功能数据库表结构
-- 创建时间: 2026-02-26

-- ----------------------------
-- Table structure for jotter_article_user
-- 文章与用户关联表（记录文章作者）
-- ----------------------------
DROP TABLE IF EXISTS `jotter_article_user`;
CREATE TABLE `jotter_article_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `article_id` int(11) NOT NULL COMMENT '文章ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_id` (`article_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_article_user_article` FOREIGN KEY (`article_id`) REFERENCES `jotter_article` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_article_user_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章与用户关联表';

-- ----------------------------
-- Table structure for jotter_comment
-- 评论表（支持二级回复）
-- ----------------------------
DROP TABLE IF EXISTS `jotter_comment`;
CREATE TABLE `jotter_comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `article_id` int(11) NOT NULL COMMENT '文章ID',
  `user_id` int(11) NOT NULL COMMENT '评论用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `parent_id` int(11) DEFAULT NULL COMMENT '父评论ID（一级评论为null）',
  `reply_to_user_id` int(11) DEFAULT NULL COMMENT '回复目标用户ID（二级回复使用）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_comment_article` FOREIGN KEY (`article_id`) REFERENCES `jotter_article` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `jotter_comment` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_reply_to_user` FOREIGN KEY (`reply_to_user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章评论表';
