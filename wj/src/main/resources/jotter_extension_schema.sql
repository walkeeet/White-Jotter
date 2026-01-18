ALTER TABLE jotter_article ADD COLUMN user_id INT;
ALTER TABLE jotter_article ADD COLUMN comment_count INT DEFAULT 0;
ALTER TABLE jotter_article ADD CONSTRAINT fk_article_user FOREIGN KEY (user_id) REFERENCES user(id);

CREATE TABLE IF NOT EXISTS comment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    article_id INT NOT NULL,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    create_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    parent_id INT,
    FOREIGN KEY (article_id) REFERENCES jotter_article(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES comment(id) ON DELETE CASCADE
);

CREATE INDEX idx_comment_article ON comment(article_id);
CREATE INDEX idx_comment_user ON comment(user_id);
CREATE INDEX idx_comment_parent ON comment(parent_id);
CREATE INDEX idx_comment_create_date ON comment(create_date);