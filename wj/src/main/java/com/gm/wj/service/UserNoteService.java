package com.gm.wj.service;

import com.gm.wj.dao.UserNoteDAO;
import com.gm.wj.entity.UserNote;
import com.gm.wj.util.MyPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User note service.
 *
 * @author System
 * @date 2026/02/26
 */
@Service
public class UserNoteService {
    @Autowired
    UserNoteDAO userNoteDAO;

    public MyPage listByUser(int userId, int page, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "updateTime");
        Page<UserNote> notes = userNoteDAO.findByUserIdOrderByUpdateTimeDesc(userId, PageRequest.of(page, size, sort));
        return new MyPage<>(notes);
    }

    public List<UserNote> listAllByUser(int userId) {
        return userNoteDAO.findByUserIdOrderByUpdateTimeDesc(userId);
    }

    public UserNote findById(int id) {
        return userNoteDAO.findById(id);
    }

    public void addOrUpdate(UserNote note) {
        userNoteDAO.save(note);
    }

    public void delete(int id) {
        userNoteDAO.deleteById(id);
    }

    public int countByUser(int userId) {
        return userNoteDAO.countByUserId(userId);
    }
}
