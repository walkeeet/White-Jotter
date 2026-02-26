package com.gm.wj.dao;

import com.gm.wj.entity.UserNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * User note DAO.
 *
 * @author System
 * @date 2026/02/26
 */
public interface UserNoteDAO extends JpaRepository<UserNote, Integer> {
    
    Page<UserNote> findByUserIdOrderByUpdateTimeDesc(Integer userId, Pageable pageable);
    
    List<UserNote> findByUserIdOrderByUpdateTimeDesc(Integer userId);
    
    UserNote findById(int id);
    
    int countByUserId(int userId);
}
