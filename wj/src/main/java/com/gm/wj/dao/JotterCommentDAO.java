package com.gm.wj.dao;

import com.gm.wj.entity.JotterComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Evan
 * @date 2026/2/26
 */
public interface JotterCommentDAO extends JpaRepository<JotterComment, Integer> {

    /**
     * Find all comments by article ID (excluding deleted ones).
     */
    List<JotterComment> findByArticleIdAndDeletedFalseOrderByCreateTimeDesc(int articleId);

    /**
     * Find top-level comments by article ID with pagination.
     */
    Page<JotterComment> findByArticleIdAndParentIdIsNullAndDeletedFalseOrderByCreateTimeDesc(int articleId, Pageable pageable);

    /**
     * Find all replies by parent IDs.
     */
    List<JotterComment> findByParentIdInAndDeletedFalseOrderByCreateTimeAsc(List<Integer> parentIds);

    /**
     * Count comments by article ID.
     */
    long countByArticleIdAndDeletedFalse(int articleId);

    /**
     * Find comment by ID.
     */
    JotterComment findById(int id);
}
