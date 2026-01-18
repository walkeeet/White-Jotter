package com.gm.wj.dao;

import com.gm.wj.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Comment DAO.
 */
public interface CommentDAO extends JpaRepository<Comment, Integer> {
    default Comment findById(int id) {
        return findById((Integer) id).orElse(null);
    }

    @Query("SELECT c FROM Comment c WHERE c.article.id = :articleId AND (c.parentComment IS NULL)")
    Page<Comment> findByArticleIdAndParentCommentIsNull(@Param("articleId") int articleId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.article.id = :articleId")
    long countByArticleId(@Param("articleId") int articleId);

    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :parentId ORDER BY c.createDate ASC")
    List<Comment> findByParentCommentId(@Param("parentId") int parentId);

    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :parentId ORDER BY c.createDate DESC")
    Page<Comment> findByParentCommentId(@Param("parentId") int parentId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.parentComment.id = :parentId")
    long countByParentCommentId(@Param("parentId") int parentId);

    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId")
    List<Comment> findByUserId(@Param("userId") int userId);

    @Query("SELECT c FROM Comment c WHERE c.article.id = :articleId")
    List<Comment> findByArticleId(@Param("articleId") int articleId);
}