package com.gm.wj.controller;

import com.gm.wj.entity.Book;
import com.gm.wj.entity.User;
import com.gm.wj.result.Result;
import com.gm.wj.result.ResultFactory;
import com.gm.wj.service.BookCollectionService;
import com.gm.wj.service.BookService;
import com.gm.wj.service.UserService;
import com.gm.wj.util.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;

/**
 * Library controller.
 *
 * @author Evan
 * @date 2019/4
 */
@RestController
public class LibraryController {
    @Autowired
    BookService bookService;
    @Autowired
    BookCollectionService bookCollectionService;
    @Autowired
    UserService userService;

    @GetMapping("/api/books")
    public Result listBooks() {
        return ResultFactory.buildSuccessResult(bookService.list());
    }

    @PostMapping("/api/admin/content/books")
    public Result addOrUpdateBooks(@RequestBody @Valid Book book) {
        bookService.addOrUpdate(book);
        return ResultFactory.buildSuccessResult("修改成功");
    }

    @PostMapping("/api/admin/content/books/delete")
    public Result deleteBook(@RequestBody @Valid Book book) {
        bookService.deleteById(book.getId());
        return ResultFactory.buildSuccessResult("删除成功");
    }

    @GetMapping("/api/search")
    public Result searchResult(@RequestParam("keywords") String keywords) {
        if ("".equals(keywords)) {
            return ResultFactory.buildSuccessResult(bookService.list());
        } else {
            return ResultFactory.buildSuccessResult(bookService.Search(keywords));
        }
    }

    @GetMapping("/api/categories/{cid}/books")
    public Result listByCategory(@PathVariable("cid") int cid) {
        if (0 != cid) {
            return ResultFactory.buildSuccessResult(bookService.listByCategory(cid));
        } else {
            return ResultFactory.buildSuccessResult(bookService.list());
        }
    }

    @PostMapping("/api/admin/content/books/covers")
    public String coversUpload(MultipartFile file) {
        String folder = "D:/workspace/img";
        File imageFolder = new File(folder);
        File f = new File(imageFolder, StringUtils.getRandomString(6) + file.getOriginalFilename()
                .substring(file.getOriginalFilename().length() - 4));
        if (!f.getParentFile().exists())
            f.getParentFile().mkdirs();
        try {
            file.transferTo(f);
            String imgURL = "http://localhost:8443/api/file/" + f.getName();
            return imgURL;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @PostMapping("/api/collection/add")
    public Result addCollection(@RequestParam("bid") int bid) {
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        if (username == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        }
        bookCollectionService.addCollection(user.getId(), bid);
        return ResultFactory.buildSuccessResult("收藏成功");
    }

    @PostMapping("/api/collection/remove")
    public Result removeCollection(@RequestParam("bid") int bid) {
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        if (username == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        }
        bookCollectionService.removeCollection(user.getId(), bid);
        return ResultFactory.buildSuccessResult("取消收藏成功");
    }

    @GetMapping("/api/collection/list")
    public Result listCollections() {
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        if (username == null) {
            return ResultFactory.buildFailResult("请先登录");
        }
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildFailResult("用户不存在");
        }
        return ResultFactory.buildSuccessResult(bookCollectionService.listByUid(user.getId()));
    }

    @GetMapping("/api/collection/status")
    public Result checkCollectionStatus(@RequestParam("bid") int bid) {
        String username = SecurityUtils.getSubject().getPrincipal().toString();
        if (username == null) {
            return ResultFactory.buildSuccessResult(false);
        }
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResultFactory.buildSuccessResult(false);
        }
        boolean isCollected = bookCollectionService.isCollected(user.getId(), bid);
        return ResultFactory.buildSuccessResult(isCollected);
    }

}
