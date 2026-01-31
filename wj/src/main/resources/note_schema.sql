-- 笔记表
DROP TABLE IF EXISTS `jotter_note`;
CREATE TABLE `jotter_note` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '发布用户ID',
  `title` varchar(255) NOT NULL COMMENT '笔记标题',
  `content_html` longtext COMMENT 'HTML格式内容',
  `content_md` longtext COMMENT 'Markdown格式内容',
  `note_abstract` varchar(255) DEFAULT NULL COMMENT '摘要',
  `cover` varchar(255) DEFAULT NULL COMMENT '封面图片URL',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `fk_note_user` (`user_id`),
  CONSTRAINT `fk_note_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户笔记表';

-- 评论表
DROP TABLE IF EXISTS `jotter_comment`;
CREATE TABLE `jotter_comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `article_id` int(11) NOT NULL COMMENT '笔记ID',
  `user_id` int(11) NOT NULL COMMENT '评论用户ID',
  `parent_id` int(11) DEFAULT NULL COMMENT '父评论ID，NULL表示一级评论',
  `reply_user_id` int(11) DEFAULT NULL COMMENT '回复的用户ID',
  `content` text NOT NULL COMMENT '评论内容',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `fk_comment_article` (`article_id`),
  KEY `fk_comment_user` (`user_id`),
  KEY `fk_comment_parent` (`parent_id`),
  KEY `fk_comment_reply_user` (`reply_user_id`),
  CONSTRAINT `fk_comment_article` FOREIGN KEY (`article_id`) REFERENCES `jotter_note` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `jotter_comment` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_reply_user` FOREIGN KEY (`reply_user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='笔记评论表';
