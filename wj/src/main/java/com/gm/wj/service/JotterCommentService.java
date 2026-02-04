package com.gm.wj.service;

import com.gm.wj.dao.JotterCommentDAO;
import com.gm.wj.dao.UserDAO;
import com.gm.wj.entity.JotterComment;
import com.gm.wj.entity.User;
import com.gm.wj.util.MyPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Assistant
 * @date 2026-02-04
 */
@Service
public class JotterCommentService {

    @Autowired
    JotterCommentDAO jotterCommentDAO;

    @Autowired
    UserDAO userDAO;

    /**
     * Add a new comment.
     */
    public JotterComment addComment(JotterComment comment) {
        comment.setCreateTime(LocalDateTime.now());
        return jotterCommentDAO.save(comment);
    }

    /**
     * Delete a comment by id. Also deletes all replies if it's a parent comment.
     */
    public void deleteComment(int id) {
        // First delete all replies
        jotterCommentDAO.deleteByParentId(id);
        // Then delete the comment itself
        jotterCommentDAO.deleteById(id);
    }

    /**
     * Get a single comment by id.
     */
    public JotterComment findById(int id) {
        return jotterCommentDAO.findCommentById(id);
    }

    /**
     * List comments with pagination for an article.
     * Returns top-level comments with their replies nested.
     */
    public MyPage<JotterComment> listCommentsByArticle(Integer articleId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JotterComment> parentComments = jotterCommentDAO.findByArticleIdAndParentIdIsNullOrderByCreateTimeDesc(articleId, pageable);

        // Get all replies for this article
        List<JotterComment> allReplies = jotterCommentDAO.findByArticleIdAndParentIdIsNotNullOrderByCreateTimeDesc(articleId);

        // Build a map of parent id to replies
        Map<Integer, List<JotterComment>> repliesMap = allReplies.stream()
                .collect(Collectors.groupingBy(JotterComment::getParentId));

        // Enrich comments with user info and attach replies
        List<JotterComment> enrichedComments = parentComments.getContent().stream()
                .map(comment -> enrichComment(comment, repliesMap))
                .collect(Collectors.toList());

        MyPage<JotterComment> result = new MyPage<>();
        result.setContent(enrichedComments);
        result.setTotalElements(parentComments.getTotalElements());
        result.setPageNumber(parentComments.getPageable().getPageNumber());
        result.setPageSize(parentComments.getPageable().getPageSize());
        result.setNumberOfElements(parentComments.getNumberOfElements());
        result.setTotalPages(parentComments.getTotalPages());

        return result;
    }

    /**
     * Enrich comment with user info and replies.
     */
    private JotterComment enrichComment(JotterComment comment, Map<Integer, List<JotterComment>> repliesMap) {
        // Set user info
        User user = userDAO.findById(comment.getUserId()).orElse(null);
        if (user != null) {
            user.setPassword(null);
            user.setSalt(null);
            comment.setUser(user);
        }

        // Set replies
        List<JotterComment> replies = repliesMap.get(comment.getId());
        if (replies != null) {
            List<JotterComment> enrichedReplies = replies.stream()
                    .map(reply -> enrichReply(reply))
                    .collect(Collectors.toList());
            comment.setReplies(enrichedReplies);
        }

        return comment;
    }

    /**
     * Enrich reply with user info and parent user name.
     */
    private JotterComment enrichReply(JotterComment reply) {
        // Set user info
        User user = userDAO.findById(reply.getUserId()).orElse(null);
        if (user != null) {
            user.setPassword(null);
            user.setSalt(null);
            reply.setUser(user);
        }

        // Set parent user name
        if (reply.getParentId() != null) {
            JotterComment parent = jotterCommentDAO.findCommentById(reply.getParentId());
            if (parent != null) {
                User parentUser = userDAO.findById(parent.getUserId()).orElse(null);
                if (parentUser != null) {
                    reply.setParentUserName(parentUser.getUsername());
                }
            }
        }

        return reply;
    }

    /**
     * Count comments for an article.
     */
    public long countCommentsByArticle(Integer articleId) {
        return jotterCommentDAO.countByArticleId(articleId);
    }

    /**
     * Delete all comments for an article.
     */
    public void deleteCommentsByArticle(Integer articleId) {
        jotterCommentDAO.deleteByArticleId(articleId);
    }
}
