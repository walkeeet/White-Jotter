package com.gm.wj.dao;

import com.gm.wj.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentDAO extends JpaRepository<Comment, Integer> {
    Comment findById(int id);

    List<Comment> findByArticleIdAndParentId(int articleId, Integer parentId);

    List<Comment> findByArticleIdOrderByCommentDateDesc(int articleId);

    Page<Comment> findByArticleIdOrderByCommentDateDesc(int articleId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("delete from Comment c where c.articleId = ?1")
    void deleteByArticleId(int articleId);
}