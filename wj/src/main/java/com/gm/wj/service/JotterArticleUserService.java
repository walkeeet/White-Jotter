package com.gm.wj.service;

import com.gm.wj.dao.JotterArticleUserDAO;
import com.gm.wj.entity.JotterArticleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Evan
 * @date 2026/2/26
 */
@Service
public class JotterArticleUserService {
    @Autowired
    JotterArticleUserDAO jotterArticleUserDAO;

    public void save(JotterArticleUser articleUser) {
        jotterArticleUserDAO.save(articleUser);
    }

    public JotterArticleUser findByArticleId(int articleId) {
        return jotterArticleUserDAO.findByArticleId(articleId);
    }

    public void deleteByArticleId(int articleId) {
        JotterArticleUser articleUser = findByArticleId(articleId);
        if (articleUser != null) {
            jotterArticleUserDAO.delete(articleUser);
        }
    }
}
