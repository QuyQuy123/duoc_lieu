package com.web.api;
import com.web.entity.Article;
import com.web.enums.ArticleStatus;
import com.web.repository.ArticleRepository;
import com.web.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/articles")
public class ArticleApi {

    @Autowired
    private ArticleService articleService;

    @GetMapping("/admin/all")
    public Page<ArticleRepository.ArticleAdminListView> getAll(
          Pageable pageable,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) ArticleStatus status
    ) {
        return articleService.getAll(q, status, pageable);
    }

    @GetMapping("/public/all")
    public Page<ArticleRepository.ArticlePublicListView> getAllPublic(
          Pageable pageable,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long diseasesId
    ) {
        return articleService.getAllPublic(q, diseasesId, pageable);
    }

    @GetMapping("/public/find-by-id")
    public Article findById(@RequestParam Long id) {
        return articleService.findById(id);
    }

    @PostMapping("/admin/save")
    public Article save(@RequestBody Article article) {
        return articleService.save(article);
    }

    @DeleteMapping("/admin/delete")
    public void delete(@RequestParam Long id) {
        articleService.delete(id);
    }

    @GetMapping("/public/article-status")
    public List<Map<String, String>> getAllStatuses() {
        List<Map<String, String>> list = new ArrayList<>();
        for (ArticleStatus status : ArticleStatus.values()) {
            Map<String, String> item = new HashMap<>();
            item.put("name", status.name());
            item.put("label", status.getLabel());
            list.add(item);
        }
        return list;
    }

    @GetMapping("/admin/export")
    public void exportArticles(
            HttpServletResponse response,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) ArticleStatus status
    ) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"articles_export.csv\"");
        articleService.writeArticlesToCsv(response.getWriter(), q, status);
    }
}
