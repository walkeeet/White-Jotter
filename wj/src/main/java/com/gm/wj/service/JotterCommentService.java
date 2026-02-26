package com.gm.wj.service;

import com.gm.wj.dao.JotterCommentDAO;
import com.gm.wj.entity.JotterArticle;
import com.gm.wj.entity.JotterComment;
import com.gm.wj.entity.User;
import com.gm.wj.util.MyPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JotterCommentService {
    @Autowired
    JotterCommentDAO jotterCommentDAO;

    public JotterComment findById(int id) {
        return jotterCommentDAO.findById(id).orElse(null);
    }

    public Map<String, Object> listCommentsByArticle(JotterArticle article, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Page<JotterComment> parentComments = jotterCommentDAO.findByArticleAndParentId(article, 0, PageRequest.of(page, size, sort));
        
        Map<Integer, List<JotterComment>> replyMap = new HashMap<>();
        for (JotterComment comment : parentComments.getContent()) {
            List<JotterComment> replies = jotterCommentDAO.findByParentIdOrderByCreateTimeAsc(comment.getId());
            replyMap.put(comment.getId(), replies);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("comments", new MyPage<>(parentComments));
        result.put("replies", replyMap);
        return result;
    }

    public List<JotterComment> findReplies(int parentId) {
        return jotterCommentDAO.findByParentIdOrderByCreateTimeAsc(parentId);
    }

    public JotterComment addComment(JotterComment comment, User user) {
        comment.setUser(user);
        comment.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return jotterCommentDAO.save(comment);
    }

    public JotterComment replyComment(int commentId, JotterComment replyComment, User user, User replyToUser) {
        JotterComment parentComment = findById(commentId);
        if (parentComment == null) {
            return null;
        }
        
        int rootParentId = parentComment.getParentId() == 0 ? parentComment.getId() : parentComment.getParentId();
        replyComment.setParentId(rootParentId);
        replyComment.setUser(user);
        replyComment.setReplyToUser(replyToUser);
        replyComment.setCreateTime(new Timestamp(System.currentTimeMillis()));
        
        return jotterCommentDAO.save(replyComment);
    }

    @Transactional
    public void deleteComment(int commentId, User user) {
        JotterComment comment = findById(commentId);
        if (comment != null && comment.getUser().getId() == user.getId()) {
            if (comment.getParentId() == 0) {
                List<JotterComment> replies = findReplies(commentId);
                jotterCommentDAO.deleteAll(replies);
            }
            jotterCommentDAO.deleteById(commentId);
        }
    }

    public int countByArticle(JotterArticle article) {
        return jotterCommentDAO.countByArticle(article);
    }

    @Transactional
    public void deleteByArticle(JotterArticle article) {
        jotterCommentDAO.deleteByArticle(article);
    }
}
