package com.gm.wj.dao;

import com.gm.wj.entity.ArticleComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Article comment DAO.
 *
 * @author System
 * @date 2026/02/26
 */
public interface ArticleCommentDAO extends JpaRepository<ArticleComment, Integer> {
    
    Page<ArticleComment> findByArticleIdAndParentIdIsNullOrderByCreateTimeDesc(Integer articleId, Pageable pageable);
    
    List<ArticleComment> findByParentIdOrderByCreateTimeAsc(Integer parentId);
    
    int countByArticleId(int articleId);
    
    @Query("SELECT c FROM ArticleComment c WHERE c.userId = ?1 ORDER BY c.createTime DESC")
    Page<ArticleComment> findByUserId(Integer userId, Pageable pageable);
    
    ArticleComment findById(int id);
}
