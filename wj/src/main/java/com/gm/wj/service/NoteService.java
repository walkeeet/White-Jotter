package com.gm.wj.service;

import com.gm.wj.dao.NoteDAO;
import com.gm.wj.dto.NoteDTO;
import com.gm.wj.entity.Note;
import com.gm.wj.entity.User;
import com.gm.wj.util.MyPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteService {
    @Autowired
    private NoteDAO noteDAO;

    @Autowired
    @Lazy
    private CommentService commentService;

    public MyPage list(int page, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Page<Note> notes = noteDAO.findAll(PageRequest.of(page, size, sort));
        List<NoteDTO> noteDTOList = notes.getContent().stream().map(this::convertToDTO).collect(Collectors.toList());
        MyPage<NoteDTO> myPage = new MyPage<>();
        myPage.setContent(noteDTOList);
        myPage.setTotalElements(notes.getTotalElements());
        myPage.setPageNumber(notes.getPageable().getPageNumber());
        myPage.setPageSize(notes.getPageable().getPageSize());
        myPage.setNumberOfElements(notes.getNumberOfElements());
        return myPage;
    }

    public MyPage listByUser(int userId, int page, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Page<Note> notes = noteDAO.findByUserIdOrderByCreateTimeDesc(userId, PageRequest.of(page, size, sort));
        List<NoteDTO> noteDTOList = notes.getContent().stream().map(this::convertToDTO).collect(Collectors.toList());
        MyPage<NoteDTO> myPage = new MyPage<>();
        myPage.setContent(noteDTOList);
        myPage.setTotalElements(notes.getTotalElements());
        myPage.setPageNumber(notes.getPageable().getPageNumber());
        myPage.setPageSize(notes.getPageable().getPageSize());
        myPage.setNumberOfElements(notes.getNumberOfElements());
        return myPage;
    }

    public NoteDTO findById(int id) {
        Note note = noteDAO.findById(id).orElse(null);
        return note != null ? convertToDTO(note) : null;
    }

    public Note findEntityById(int id) {
        return noteDAO.findById(id).orElse(null);
    }

    public void addOrUpdate(Note note) {
        if (note.getId() == 0) {
            note.setCreateTime(new Date());
            note.setUpdateTime(new Date());
            note.setCommentCount(0);
        } else {
            note.setUpdateTime(new Date());
        }
        noteDAO.save(note);
    }

    public void delete(int id) {
        commentService.deleteAllByNoteId(id);
        noteDAO.deleteById(id);
    }

    public void incrementCommentCount(int noteId) {
        Note note = noteDAO.findById(noteId).orElse(null);
        if (note != null) {
            note.setCommentCount(note.getCommentCount() + 1);
            noteDAO.save(note);
        }
    }

    public void decrementCommentCount(int noteId) {
        Note note = noteDAO.findById(noteId).orElse(null);
        if (note != null && note.getCommentCount() > 0) {
            note.setCommentCount(note.getCommentCount() - 1);
            noteDAO.save(note);
        }
    }

    private NoteDTO convertToDTO(Note note) {
        NoteDTO dto = new NoteDTO();
        BeanUtils.copyProperties(note, dto);
        if (note.getUser() != null) {
            dto.setUserId(note.getUser().getId());
            dto.setUsername(note.getUser().getUsername());
        }
        return dto;
    }
}
