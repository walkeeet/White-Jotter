package com.gm.wj.dao;

import com.gm.wj.entity.JotterNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JotterNoteDAO extends JpaRepository<JotterNote, Integer> {

    JotterNote findById(int id);

    List<JotterNote> findByUserIdOrderByCreateTimeDesc(int userId);

    Page<JotterNote> findByUserId(int userId, Pageable pageable);

    void deleteByIdAndUserId(int id, int userId);
}
