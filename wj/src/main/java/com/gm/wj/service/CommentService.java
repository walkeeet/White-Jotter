package com.gm.wj.service;

import com.gm.wj.dao.CommentDAO;
import com.gm.wj.dao.JotterArticleDAO;
import com.gm.wj.dao.UserDAO;
import com.gm.wj.entity.Comment;
import com.gm.wj.entity.JotterArticle;
import com.gm.wj.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentDAO commentDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    JotterArticleDAO jotterArticleDAO;

    public List<Comment> listByArticleId(int articleId) {
        List<Comment> comments = commentDAO.findByArticleIdOrderByCommentDateDesc(articleId);
        for (Comment comment : comments) {
            User user = userDAO.findById(comment.getUserId());
            comment.setUser(user);
            if (comment.getParentId() != null) {
                Comment parentComment = commentDAO.findById(comment.getParentId());
                if (parentComment != null) {
                    User parentUser = userDAO.findById(parentComment.getUserId());
                    parentComment.setUser(parentUser);
                    comment.setParentComment(parentComment);
                }
            }
        }
        return comments;
    }

    public Page<Comment> listByArticleIdWithPage(int articleId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentDAO.findByArticleIdOrderByCommentDateDesc(articleId, pageable);
        for (Comment comment : commentPage.getContent()) {
            User user = userDAO.findById(comment.getUserId());
            comment.setUser(user);
            if (comment.getParentId() != null) {
                Comment parentComment = commentDAO.findById(comment.getParentId());
                if (parentComment != null) {
                    User parentUser = userDAO.findById(parentComment.getUserId());
                    parentComment.setUser(parentUser);
                    comment.setParentComment(parentComment);
                }
            }
        }
        return commentPage;
    }

    public Comment findById(int id) {
        return commentDAO.findById(id);
    }

    public void add(Comment comment) {
        comment.setCommentDate(new Date(System.currentTimeMillis()));
        commentDAO.save(comment);
        JotterArticle article = jotterArticleDAO.findById(comment.getArticleId());
        if (article != null) {
            article.setCommentCount(article.getCommentCount() + 1);
            jotterArticleDAO.save(article);
        }
    }

    public void delete(int id) {
        Comment comment = commentDAO.findById(id);
        if (comment != null) {
            commentDAO.deleteById(id);
            JotterArticle article = jotterArticleDAO.findById(comment.getArticleId());
            if (article != null) {
                article.setCommentCount(article.getCommentCount() - 1);
                jotterArticleDAO.save(article);
            }
        }
    }

    public void deleteByArticleId(int articleId) {
        commentDAO.deleteByArticleId(articleId);
    }
}