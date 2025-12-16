package com.web.controller.user;

import com.web.entity.Article;
import com.web.entity.Plant;
import com.web.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class UserArticleController {

    @Autowired
    private ArticleService articleService;

    @RequestMapping(value = {"/article-detail/{slug}"}, method = RequestMethod.GET)
    public String articleDetail(Model model, @PathVariable String slug) {
        Article article = articleService.findBySlug(slug);
        if(article == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "plant not found");
        }
        model.addAttribute("article", article);
        return "user/article-detail.html";
    }
}
