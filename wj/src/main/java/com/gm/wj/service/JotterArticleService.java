package com.gm.wj.service;

import com.gm.wj.dao.JotterArticleDAO;
import com.gm.wj.dto.JotterArticleDTO;
import com.gm.wj.entity.JotterArticle;
import com.gm.wj.entity.JotterArticleUser;
import com.gm.wj.entity.User;
import com.gm.wj.redis.RedisService;
import com.gm.wj.util.MyPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Evan
 * @date 2020/1/14 21:00
 */
@Service
public class JotterArticleService {
    @Autowired
    JotterArticleDAO jotterArticleDAO;
    @Autowired
    RedisService redisService;
    @Autowired
    JotterArticleUserService jotterArticleUserService;
    @Autowired
    UserService userService;
    @Autowired
    JotterCommentService jotterCommentService;

    public MyPage list(int page, int size) {
        MyPage<JotterArticle> articles;
        String key = "articlepage:" + page;
        Object articlePageCache = redisService.get(key);

        if (articlePageCache == null) {
            Sort sort = new Sort(Sort.Direction.DESC, "id");
            Page<JotterArticle> articlesInDb = jotterArticleDAO.findAll(PageRequest.of(page, size, sort));
            articles = new MyPage<>(articlesInDb);
            redisService.set(key, articles);
        } else {
            articles = (MyPage<JotterArticle>) articlePageCache;
        }
        return articles;
    }

    /**
     * List articles with author info and comment count.
     */
    public MyPage<JotterArticleDTO> listWithAuthorAndCommentCount(int page, int size) {
        MyPage<JotterArticle> articles = list(page, size);

        List<Integer> articleIds = articles.getContent().stream()
                .map(JotterArticle::getId)
                .collect(Collectors.toList());

        // Get author info
        List<JotterArticleUser> articleUsers = articleIds.stream()
                .map(jotterArticleUserService::findByArticleId)
                .filter(au -> au != null)
                .collect(Collectors.toList());

        List<Integer> userIds = articleUsers.stream()
                .map(JotterArticleUser::getUserId)
                .distinct()
                .collect(Collectors.toList());

        Map<Integer, User> userMap = userIds.stream()
                .map(id -> userService.findById(id))
                .filter(user -> user != null)
                .collect(Collectors.toMap(User::getId, user -> user));

        Map<Integer, Integer> articleUserMap = articleUsers.stream()
                .collect(Collectors.toMap(JotterArticleUser::getArticleId, JotterArticleUser::getUserId));

        // Convert to DTO
        List<JotterArticleDTO> dtoList = articles.getContent().stream().map(article -> {
            JotterArticleDTO dto = new JotterArticleDTO();
            BeanUtils.copyProperties(article, dto);

            Integer userId = articleUserMap.get(article.getId());
            if (userId != null) {
                dto.setAuthor(userMap.get(userId));
            }

            long commentCount = jotterCommentService.countByArticleId(article.getId());
            dto.setCommentCount(commentCount);

            return dto;
        }).collect(Collectors.toList());

        MyPage<JotterArticleDTO> result = new MyPage<>();
        result.setContent(dtoList);
        result.setTotalElements(articles.getTotalElements());
        result.setPageNumber(articles.getPageNumber());
        result.setPageSize(articles.getPageSize());
        result.setNumberOfElements(articles.getNumberOfElements());
        return result;
    }

//    用于复现异常
//    @Cacheable(value = RedisConfig.REDIS_KEY_DATABASE)
//    public Page list(int page, int size) {
//        Sort sort = new Sort(Sort.Direction.DESC, "id");
//        return jotterArticleDAO.findAll(PageRequest.of(page, size, sort));
//    }


    public JotterArticle findById(int id) {
        JotterArticle article;
        String key = "article:" + id;
        Object articleCache = redisService.get(key);

        if (articleCache == null) {
            article = jotterArticleDAO.findById(id);
            redisService.set(key, article);
        } else {
            article = (JotterArticle) articleCache;
        }
        return article;
    }

    /**
     * Get article with author info and comment count.
     */
    public JotterArticleDTO findByIdWithAuthorAndCommentCount(int id) {
        JotterArticle article = findById(id);
        if (article == null) {
            return null;
        }

        JotterArticleDTO dto = new JotterArticleDTO();
        BeanUtils.copyProperties(article, dto);

        JotterArticleUser articleUser = jotterArticleUserService.findByArticleId(id);
        if (articleUser != null) {
            User author = userService.findById(articleUser.getUserId());
            dto.setAuthor(author);
        }

        long commentCount = jotterCommentService.countByArticleId(id);
        dto.setCommentCount(commentCount);

        return dto;
    }

    public void addOrUpdate(JotterArticle article) {
        jotterArticleDAO.save(article);

        redisService.delete("article" + article.getId());
        Set<String> keys = redisService.getKeysByPattern("articlepage*");
        redisService.delete(keys);
    }

    /**
     * Add or update article with author.
     */
    public void addOrUpdate(JotterArticle article, int userId) {
        // Set article date if new
        if (article.getId() == 0) {
            article.setArticleDate(Date.valueOf(LocalDate.now()));
        }

        JotterArticle saved = jotterArticleDAO.save(article);

        // Save article-user relationship if new article
        if (article.getId() == 0 || jotterArticleUserService.findByArticleId(saved.getId()) == null) {
            JotterArticleUser articleUser = new JotterArticleUser();
            articleUser.setArticleId(saved.getId());
            articleUser.setUserId(userId);
            jotterArticleUserService.save(articleUser);
        }

        redisService.delete("article" + saved.getId());
        Set<String> keys = redisService.getKeysByPattern("articlepage*");
        redisService.delete(keys);
    }

    public void delete(int id) {
        jotterArticleDAO.deleteById(id);

        redisService.delete("article:" + id);
        Set<String> keys = redisService.getKeysByPattern("articlepage*");
        redisService.delete(keys);
    }

    /**
     * Check if user is the author of the article.
     */
    public boolean isArticleAuthor(int articleId, int userId) {
        JotterArticleUser articleUser = jotterArticleUserService.findByArticleId(articleId);
        return articleUser != null && articleUser.getUserId() == userId;
    }
}
