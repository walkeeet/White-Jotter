package com.gm.wj.dao;

import com.gm.wj.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentDAO extends JpaRepository<Comment, Integer> {
    Page<Comment> findByNoteIdOrderByCreateTimeDesc(int noteId, Pageable pageable);
    Page<Comment> findByParentIdOrderByCreateTimeDesc(int parentId, Pageable pageable);
}
