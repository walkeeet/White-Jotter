package com.gm.wj.service;

import com.gm.wj.dao.JotterCommentDAO;
import com.gm.wj.entity.JotterComment;
import com.gm.wj.util.MyPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
public class JotterCommentService {
    @Autowired
    JotterCommentDAO jotterCommentDAO;

    public MyPage<JotterComment> listCommentsByArticleId(int articleId, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Page<JotterComment> commentPage = jotterCommentDAO.findRootCommentsByArticleId(articleId, PageRequest.of(page, size, sort));
        MyPage<JotterComment> myPage = new MyPage<>(commentPage);
        
        List<JotterComment> rootComments = myPage.getContent();
        for (JotterComment comment : rootComments) {
            List<JotterComment> children = jotterCommentDAO.findByParentIdOrderByCreateTimeDesc(comment.getId());
            comment.setChildren(children);
        }
        
        return myPage;
    }

    public int countCommentsByArticleId(int articleId) {
        return jotterCommentDAO.countByArticleId(articleId);
    }

    public JotterComment addComment(JotterComment comment) {
        comment.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return jotterCommentDAO.save(comment);
    }

    @Transactional
    public void deleteComment(int id, int userId) {
        JotterComment comment = jotterCommentDAO.findById(id).orElse(null);
        if (comment != null && comment.getUserId() == userId) {
            if (comment.getParentId() == null) {
                List<JotterComment> children = jotterCommentDAO.findByParentIdOrderByCreateTimeDesc(id);
                for (JotterComment child : children) {
                    jotterCommentDAO.deleteById(child.getId());
                }
            }
            jotterCommentDAO.deleteById(id);
        }
    }

    @Transactional
    public void deleteCommentsByArticleId(int articleId) {
        jotterCommentDAO.deleteByArticleId(articleId);
    }
}
