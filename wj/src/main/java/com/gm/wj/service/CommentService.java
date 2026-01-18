package com.gm.wj.service;

import com.gm.wj.dao.CommentDAO;
import com.gm.wj.dao.JotterArticleDAO;
import com.gm.wj.dao.UserDAO;
import com.gm.wj.entity.Comment;
import com.gm.wj.entity.JotterArticle;
import com.gm.wj.entity.User;
import com.gm.wj.util.MyPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

/**
 * Comment service.
 */
@Service
public class CommentService {
    @Autowired
    CommentDAO commentDAO;

    @Autowired
    JotterArticleDAO jotterArticleDAO;

    @Autowired
    UserDAO userDAO;

    @Transactional
    public Comment addComment(int articleId, int userId, String content, Integer parentCommentId) {
        JotterArticle article = jotterArticleDAO.findById(articleId);
        User user = userDAO.findById(userId);

        Comment comment = Comment.builder()
                .article(article)
                .user(user)
                .content(content)
                .createDate(new Date(System.currentTimeMillis()))
                .build();

        if (parentCommentId != null) {
            Comment parentComment = commentDAO.findById(parentCommentId);
            comment.setParentComment(parentComment);
        }

        commentDAO.save(comment);

        long commentCount = commentDAO.countByArticleId(articleId);
        article.setCommentCount((int) commentCount);
        jotterArticleDAO.save(article);

        return comment;
    }

    @Transactional
    public void deleteComment(int commentId, int userId) {
        Comment comment = commentDAO.findById(commentId);

        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }

        if (comment.getUser().getId() != userId) {
            throw new RuntimeException("无权限删除该评论");
        }

        List<Comment> replies = commentDAO.findByParentCommentId(commentId);
        for (Comment reply : replies) {
            commentDAO.delete(reply);
        }

        int articleId = comment.getArticle().getId();
        commentDAO.delete(comment);

        long commentCount = commentDAO.countByArticleId(articleId);
        JotterArticle article = jotterArticleDAO.findById(articleId);
        article.setCommentCount((int) commentCount);
        jotterArticleDAO.save(article);
    }

    public MyPage getCommentsByArticleId(int articleId, int page, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "createDate");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<Comment> commentPage = commentDAO.findByArticleIdAndParentCommentIsNull(articleId, pageRequest);

        for (Comment comment : commentPage.getContent()) {
            long replyCount = commentDAO.countByParentCommentId(comment.getId());
            comment.setReplyCount((int) replyCount);
        }

        return new MyPage<>(commentPage);
    }

    public List<Comment> getRepliesByParentCommentId(int parentCommentId) {
        return commentDAO.findByParentCommentId(parentCommentId);
    }

    public Comment getCommentById(int commentId) {
        return commentDAO.findById(commentId);
    }

    @Transactional
    public void deleteArticleComments(int articleId) {
        List<Comment> comments = commentDAO.findByArticleId(articleId);
        for (Comment comment : comments) {
            commentDAO.delete(comment);
        }
    }

    public void addComment(int articleId, Comment comment) {
        JotterArticle article = jotterArticleDAO.findById(articleId);
        comment.setArticle(article);
        commentDAO.save(comment);

        long commentCount = commentDAO.countByArticleId(articleId);
        article.setCommentCount((int) commentCount);
        jotterArticleDAO.save(article);
    }

    public void updateComment(Comment comment) {
        commentDAO.save(comment);
    }

    public Page<Comment> listCommentsByArticleId(int articleId, int page, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "createDate");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return commentDAO.findByArticleIdAndParentCommentIsNull(articleId, pageRequest);
    }

    public long countCommentsByArticleId(int articleId) {
        return commentDAO.countByArticleId(articleId);
    }

    public Page<Comment> listRepliesByCommentId(int commentId, int page, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "createDate");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return commentDAO.findByParentCommentId(commentId, pageRequest);
    }

    @Transactional
    public void deleteComment(int commentId) {
        Comment comment = commentDAO.findById(commentId);
        if (comment != null) {
            List<Comment> replies = commentDAO.findByParentCommentId(commentId);
            for (Comment reply : replies) {
                commentDAO.delete(reply);
            }
            commentDAO.delete(comment);

            JotterArticle article = comment.getArticle();
            long commentCount = commentDAO.countByArticleId(article.getId());
            article.setCommentCount((int) commentCount);
            jotterArticleDAO.save(article);
        }
    }
}
