-- ----------------------------
-- Table structure for book_favorite
-- ----------------------------
DROP TABLE IF EXISTS `book_favorite`;
CREATE TABLE `book_favorite` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `book_id` int(11) NOT NULL,
  `favorite_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_book` (`user_id`,`book_id`),
  KEY `fk_book_favorite_user` (`user_id`),
  KEY `fk_book_favorite_book` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
