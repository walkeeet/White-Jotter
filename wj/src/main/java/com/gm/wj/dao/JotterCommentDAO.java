package com.gm.wj.dao;

import com.gm.wj.entity.JotterArticle;
import com.gm.wj.entity.JotterComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JotterCommentDAO extends JpaRepository<JotterComment, Integer> {
    List<JotterComment> findByArticleAndParentIdOrderByCreateTimeDesc(JotterArticle article, int parentId);
    Page<JotterComment> findByArticleAndParentId(JotterArticle article, int parentId, Pageable pageable);
    List<JotterComment> findByParentIdOrderByCreateTimeAsc(int parentId);
    int countByArticle(JotterArticle article);
    void deleteByArticle(JotterArticle article);
}
