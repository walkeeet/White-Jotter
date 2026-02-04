package com.gm.wj.dao;

import com.gm.wj.entity.JotterComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Assistant
 * @date 2026-02-04
 */
public interface JotterCommentDAO extends JpaRepository<JotterComment, Integer> {

    /**
     * Find comment by id.
     */
    JotterComment findCommentById(int id);

    /**
     * Find top-level comments by article id with pagination, ordered by create time desc.
     */
    Page<JotterComment> findByArticleIdAndParentIdIsNullOrderByCreateTimeDesc(Integer articleId, Pageable pageable);

    /**
     * Find all comments by article id (for admin or internal use).
     */
    List<JotterComment> findByArticleId(Integer articleId);

    /**
     * Find replies by parent id, ordered by create time desc.
     */
    List<JotterComment> findByParentIdOrderByCreateTimeDesc(Integer parentId);

    /**
     * Find all replies by article id, ordered by create time desc.
     */
    List<JotterComment> findByArticleIdAndParentIdIsNotNullOrderByCreateTimeDesc(Integer articleId);

    /**
     * Count comments by article id.
     */
    long countByArticleId(Integer articleId);

    /**
     * Delete all comments by article id.
     */
    @Modifying
    @Transactional
    @Query("delete from JotterComment c where c.articleId = ?1")
    void deleteByArticleId(Integer articleId);

    /**
     * Delete all replies by parent id.
     */
    @Modifying
    @Transactional
    @Query("delete from JotterComment c where c.parentId = ?1")
    void deleteByParentId(Integer parentId);
}
