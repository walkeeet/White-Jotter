package com.gm.wj.service;

import com.gm.wj.dao.JotterCommentDAO;
import com.gm.wj.dto.CommentDTO;
import com.gm.wj.entity.JotterComment;
import com.gm.wj.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Evan
 * @date 2026/2/26
 */
@Service
public class JotterCommentService {
    @Autowired
    JotterCommentDAO jotterCommentDAO;
    @Autowired
    UserService userService;

    /**
     * Add a new comment.
     */
    public JotterComment addComment(JotterComment comment) {
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        comment.setDeleted(false);
        return jotterCommentDAO.save(comment);
    }

    /**
     * Delete a comment (soft delete).
     */
    public void deleteComment(int commentId, int userId) {
        JotterComment comment = jotterCommentDAO.findById(commentId);
        if (comment != null && comment.getUserId() == userId) {
            comment.setDeleted(true);
            comment.setUpdateTime(LocalDateTime.now());
            jotterCommentDAO.save(comment);
        }
    }

    /**
     * Get comments by article ID with pagination.
     * Returns top-level comments with nested replies.
     */
    public Page<CommentDTO> getCommentsByArticleId(int articleId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JotterComment> topLevelComments = jotterCommentDAO
                .findByArticleIdAndParentIdIsNullAndDeletedFalseOrderByCreateTimeDesc(articleId, pageable);

        // Get all parent IDs
        List<Integer> parentIds = topLevelComments.getContent().stream()
                .map(JotterComment::getId)
                .collect(Collectors.toList());

        // Get all replies for these parent comments
        List<JotterComment> allReplies = parentIds.isEmpty()
                ? new ArrayList<>()
                : jotterCommentDAO.findByParentIdInAndDeletedFalseOrderByCreateTimeAsc(parentIds);

        // Group replies by parent ID
        Map<Integer, List<JotterComment>> repliesMap = allReplies.stream()
                .collect(Collectors.groupingBy(JotterComment::getParentId));

        // Build user cache to avoid repeated queries
        List<Integer> userIds = new ArrayList<>();
        userIds.addAll(topLevelComments.getContent().stream().map(JotterComment::getUserId).collect(Collectors.toList()));
        userIds.addAll(allReplies.stream().map(JotterComment::getUserId).collect(Collectors.toList()));
        userIds.addAll(allReplies.stream()
                .filter(r -> r.getReplyToUserId() != null)
                .map(JotterComment::getReplyToUserId)
                .collect(Collectors.toList()));

        List<Integer> distinctUserIds = userIds.stream().distinct().collect(Collectors.toList());
        Map<Integer, User> userMap = distinctUserIds.stream()
                .map(id -> userService.findById(id))
                .filter(user -> user != null)
                .collect(Collectors.toMap(User::getId, user -> user));

        // Convert to DTO
        return topLevelComments.map(comment -> {
            CommentDTO dto = convertToDTO(comment, userMap);
            List<CommentDTO> replies = repliesMap.getOrDefault(comment.getId(), new ArrayList<>())
                    .stream()
                    .map(reply -> convertToDTO(reply, userMap))
                    .collect(Collectors.toList());
            dto.setReplies(replies);
            return dto;
        });
    }

    /**
     * Count comments by article ID.
     */
    public long countByArticleId(int articleId) {
        return jotterCommentDAO.countByArticleIdAndDeletedFalse(articleId);
    }

    /**
     * Get comment by ID.
     */
    public JotterComment findById(int id) {
        return jotterCommentDAO.findById(id);
    }

    private CommentDTO convertToDTO(JotterComment comment, Map<Integer, User> userMap) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setArticleId(comment.getArticleId());
        dto.setUserId(comment.getUserId());
        dto.setContent(comment.getContent());
        dto.setParentId(comment.getParentId());
        dto.setReplyToUserId(comment.getReplyToUserId());
        dto.setCreateTime(comment.getCreateTime());
        dto.setUpdateTime(comment.getUpdateTime());
        dto.setDeleted(comment.isDeleted());
        dto.setUser(userMap.get(comment.getUserId()));
        if (comment.getReplyToUserId() != null) {
            dto.setReplyToUser(userMap.get(comment.getReplyToUserId()));
        }
        return dto;
    }
}
