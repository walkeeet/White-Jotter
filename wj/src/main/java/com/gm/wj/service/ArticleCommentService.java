package com.gm.wj.service;

import com.gm.wj.dao.ArticleCommentDAO;
import com.gm.wj.entity.ArticleComment;
import com.gm.wj.util.MyPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Article comment service.
 *
 * @author System
 * @date 2026/02/26
 */
@Service
public class ArticleCommentService {
    @Autowired
    ArticleCommentDAO articleCommentDAO;

    public MyPage listByArticle(int articleId, int page, int size) {
        Page<ArticleComment> comments = articleCommentDAO.findByArticleIdAndParentIdIsNullOrderByCreateTimeDesc(articleId, PageRequest.of(page, size));
        
        List<Map<String, Object>> content = new ArrayList<>();
        for (ArticleComment comment : comments.getContent()) {
            Map<String, Object> commentMap = convertToMap(comment);
            List<ArticleComment> replies = articleCommentDAO.findByParentIdOrderByCreateTimeAsc(comment.getId());
            List<Map<String, Object>> replyList = new ArrayList<>();
            for (ArticleComment reply : replies) {
                replyList.add(convertToMap(reply));
            }
            commentMap.put("replies", replyList);
            content.add(commentMap);
        }
        
        MyPage<Map<String, Object>> result = new MyPage<>();
        result.setContent(content);
        result.setTotalElements(comments.getTotalElements());
        result.setTotalPages(comments.getTotalPages());
        result.setSize(comments.getSize());
        result.setNumber(comments.getNumber());
        return result;
    }

    private Map<String, Object> convertToMap(ArticleComment comment) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", comment.getId());
        map.put("content", comment.getContent());
        map.put("articleId", comment.getArticleId());
        map.put("userId", comment.getUserId());
        map.put("username", comment.getUsername());
        map.put("parentId", comment.getParentId());
        map.put("replyToUserId", comment.getReplyToUserId());
        map.put("replyToUsername", comment.getReplyToUsername());
        map.put("createTime", comment.getCreateTime());
        return map;
    }

    public int countByArticle(int articleId) {
        return articleCommentDAO.countByArticleId(articleId);
    }

    public void add(ArticleComment comment) {
        articleCommentDAO.save(comment);
    }

    public void delete(int id) {
        ArticleComment comment = articleCommentDAO.findById(id);
        if (comment != null) {
            if (comment.getParentId() == null) {
                List<ArticleComment> replies = articleCommentDAO.findByParentIdOrderByCreateTimeAsc(id);
                for (ArticleComment reply : replies) {
                    articleCommentDAO.delete(reply);
                }
            }
            articleCommentDAO.delete(comment);
        }
    }

    public ArticleComment findById(int id) {
        return articleCommentDAO.findById(id);
    }

    public MyPage listByUser(int userId, int page, int size) {
        Page<ArticleComment> comments = articleCommentDAO.findByUserId(userId, PageRequest.of(page, size));
        return new MyPage<>(comments);
    }
}
