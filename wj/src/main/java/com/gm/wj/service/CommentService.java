package com.gm.wj.service;

import com.gm.wj.dao.CommentDAO;
import com.gm.wj.dao.NoteDAO;
import com.gm.wj.dto.CommentDTO;
import com.gm.wj.entity.Comment;
import com.gm.wj.entity.Note;
import com.gm.wj.entity.User;
import com.gm.wj.util.MyPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private NoteDAO noteDAO;

    @Autowired
    @Lazy
    private NoteService noteService;

    public MyPage listByNote(int noteId, int page, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Page<Comment> allComments = commentDAO.findByNoteIdOrderByCreateTimeDesc(noteId, PageRequest.of(0, Integer.MAX_VALUE, sort));
        List<Comment> topLevelComments = allComments.getContent().stream()
                .filter(c -> c.getParent() == null)
                .collect(Collectors.toList());
        
        int totalElements = topLevelComments.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalElements);
        
        List<Comment> pagedComments = startIndex < totalElements ? 
                topLevelComments.subList(startIndex, endIndex) : new ArrayList<>();
        
        List<CommentDTO> commentDTOList = pagedComments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        MyPage<CommentDTO> myPage = new MyPage<>();
        myPage.setContent(commentDTOList);
        myPage.setTotalElements(totalElements);
        myPage.setPageNumber(page);
        myPage.setPageSize(size);
        myPage.setNumberOfElements(pagedComments.size());
        return myPage;
    }

    public CommentDTO findById(int id) {
        Comment comment = commentDAO.findById(id).orElse(null);
        return comment != null ? convertToDTO(comment) : null;
    }

    public Comment findEntityById(int id) {
        return commentDAO.findById(id).orElse(null);
    }

    public void addOrUpdate(Comment comment) {
        if (comment.getId() == 0) {
            comment.setCreateTime(new Date());
            noteService.incrementCommentCount(comment.getNote().getId());
        }
        commentDAO.save(comment);
    }

    public void delete(int id, int userId) {
        Comment comment = commentDAO.findById(id).orElse(null);
        if (comment != null && comment.getUser().getId() == userId) {
            int noteId = comment.getNote().getId();
            int commentCount = 1;
            if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
                commentCount += comment.getReplies().size();
            }
            commentDAO.delete(comment);
            for (int i = 0; i < commentCount; i++) {
                noteService.decrementCommentCount(noteId);
            }
        }
    }

    public void deleteAllByNoteId(int noteId) {
        List<Comment> comments = commentDAO.findByNoteIdOrderByCreateTimeDesc(noteId, PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        commentDAO.deleteAll(comments);
    }

    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        BeanUtils.copyProperties(comment, dto);
        if (comment.getUser() != null) {
            dto.setUserId(comment.getUser().getId());
            dto.setUsername(comment.getUser().getUsername());
        }
        if (comment.getNote() != null) {
            dto.setNoteId(comment.getNote().getId());
        }
        if (comment.getParent() != null) {
            dto.setParentId(comment.getParent().getId());
            if (comment.getParent().getUser() != null) {
                dto.setParentUsername(comment.getParent().getUser().getUsername());
            }
        }
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            List<CommentDTO> replyDTOList = comment.getReplies().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            dto.setReplies(replyDTOList);
        }
        return dto;
    }
}
