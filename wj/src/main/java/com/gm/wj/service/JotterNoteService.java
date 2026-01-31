package com.gm.wj.service;

import com.gm.wj.dao.JotterNoteDAO;
import com.gm.wj.entity.JotterNote;
import com.gm.wj.redis.RedisService;
import com.gm.wj.util.MyPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Service
public class JotterNoteService {
    @Autowired
    JotterNoteDAO jotterNoteDAO;
    @Autowired
    JotterCommentService jotterCommentService;
    @Autowired
    RedisService redisService;

    public MyPage<JotterNote> list(int page, int size) {
        MyPage<JotterNote> notes;
        String key = "notepage:" + page;
        Object notePageCache = redisService.get(key);

        if (notePageCache == null) {
            Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
            Page<JotterNote> notesInDb = jotterNoteDAO.findAll(PageRequest.of(page, size, sort));
            notes = new MyPage<>(notesInDb);
            
            for (JotterNote note : notes.getContent()) {
                note.setCommentCount(jotterCommentService.countCommentsByArticleId(note.getId()));
            }
            
            redisService.set(key, notes);
        } else {
            notes = (MyPage<JotterNote>) notePageCache;
        }
        return notes;
    }

    public MyPage<JotterNote> listByUserId(int userId, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Page<JotterNote> notesInDb = jotterNoteDAO.findByUserId(userId, PageRequest.of(page, size, sort));
        MyPage<JotterNote> notes = new MyPage<>(notesInDb);
        
        for (JotterNote note : notes.getContent()) {
            note.setCommentCount(jotterCommentService.countCommentsByArticleId(note.getId()));
        }
        return notes;
    }

    public JotterNote findById(int id) {
        JotterNote note;
        String key = "note:" + id;
        Object noteCache = redisService.get(key);

        if (noteCache == null) {
            note = jotterNoteDAO.findById(id);
            if (note != null) {
                note.setCommentCount(jotterCommentService.countCommentsByArticleId(id));
            }
            redisService.set(key, note);
        } else {
            note = (JotterNote) noteCache;
        }
        return note;
    }

    public JotterNote addOrUpdate(JotterNote note) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (note.getId() == 0) {
            note.setCreateTime(now);
            note.setUpdateTime(now);
        } else {
            note.setUpdateTime(now);
        }
        JotterNote saved = jotterNoteDAO.save(note);

        redisService.delete("note:" + note.getId());
        Set<String> keys = redisService.getKeysByPattern("notepage*");
        redisService.delete(keys);
        
        return saved;
    }

    @Transactional
    public void delete(int id, int userId) {
        jotterNoteDAO.deleteByIdAndUserId(id, userId);
        jotterCommentService.deleteCommentsByArticleId(id);

        redisService.delete("note:" + id);
        Set<String> keys = redisService.getKeysByPattern("notepage*");
        redisService.delete(keys);
    }

    public boolean isOwner(int id, int userId) {
        JotterNote note = jotterNoteDAO.findById(id);
        return note != null && note.getUserId() == userId;
    }
}
