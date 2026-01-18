package com.gm.wj.dao;

import com.gm.wj.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteDAO extends JpaRepository<Note, Integer> {
    Page<Note> findByUserIdOrderByCreateTimeDesc(int userId, Pageable pageable);
}
