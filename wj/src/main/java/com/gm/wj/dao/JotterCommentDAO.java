package com.gm.wj.dao;

import com.gm.wj.entity.JotterComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JotterCommentDAO extends JpaRepository<JotterComment, Integer> {

    List<JotterComment> findByArticleIdAndParentIdIsNullOrderByCreateTimeDesc(int articleId);

    List<JotterComment> findByParentIdOrderByCreateTimeDesc(int parentId);

    @Query("SELECT c FROM JotterComment c WHERE c.articleId = :articleId AND c.parentId IS NULL")
    Page<JotterComment> findRootCommentsByArticleId(@Param("articleId") int articleId, Pageable pageable);

    int countByArticleId(int articleId);

    List<JotterComment> findByUserId(int userId);

    void deleteByArticleId(int articleId);
}
