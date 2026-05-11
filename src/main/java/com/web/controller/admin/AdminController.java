package com.web.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping(value = {"/create-article"}, method = RequestMethod.GET)
    public String createArticle() {
        return "admin/create-article";
    }

    @RequestMapping(value = {"/create-plant"}, method = RequestMethod.GET)
    public String createPlant() {
        return "admin/create-plant";
    }

    @RequestMapping(value = {"/create-user"}, method = RequestMethod.GET)
    public String createUser() {
        return "admin/create-user";
    }

    @RequestMapping(value = {"/create-research"}, method = RequestMethod.GET)
    public String createResearch() {
        return "admin/create-research";
    }

    @RequestMapping(value = {"/create-expert"}, method = RequestMethod.GET)
    public String createExpert() {
        return "admin/create-expert";
    }

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index() {
        return "redirect:/admin/dashboard";
    }

    @RequestMapping(value = {"/dashboard"}, method = RequestMethod.GET)
    public String dashboard(Model model) {
        DashboardStats stats = loadDashboardStats();

        model.addAttribute("plantCount", stats.getPlantCount());
        model.addAttribute("articleCount", stats.getArticleCount());
        model.addAttribute("researchCount", stats.getResearchCount());
        model.addAttribute("userCount", stats.getUserCount());
        model.addAttribute("totalViews", stats.getTotalViews());
        model.addAttribute("recentPlants", loadRecentItems("""
                select name as title, slug, image, created_at
                from plants
                order by created_at desc
                limit 5
                """));
        model.addAttribute("recentArticles", loadRecentItems("""
                select title, slug, image_banner as image, created_at
                from articles
                order by created_at desc
                limit 5
                """));
        model.addAttribute("recentResearches", loadRecentItems("""
                select title, slug, image_banner as image, created_at
                from research
                order by created_at desc
                limit 5
                """));
        model.addAttribute("recentUsers", loadRecentItems("""
                select coalesce(nullif(fullname, ''), email, username) as title,
                       null as slug,
                       null as image,
                       created_date as created_at
                from users
                order by created_date desc
                limit 5
                """));
        return "admin/dashboard";
    }

    private DashboardStats loadDashboardStats() {
        String sql = """
                select
                    (select count(*) from plants) as plant_count,
                    (select count(*) from articles) as article_count,
                    (select count(*) from research) as research_count,
                    (select count(*) from users) as user_count,
                    (
                        (select coalesce(sum(views), 0) from plants) +
                        (select coalesce(sum(views), 0) from articles) +
                        (select coalesce(sum(views), 0) from research)
                    ) as total_views
                """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new DashboardStats(
                rs.getLong("plant_count"),
                rs.getLong("article_count"),
                rs.getLong("research_count"),
                rs.getLong("user_count"),
                rs.getLong("total_views")
        ));
    }

    private List<DashboardItem> loadRecentItems(String sql) {
        return jdbcTemplate.query(sql, (rs, rowNum) -> new DashboardItem(
                rs.getString("title"),
                rs.getString("slug"),
                rs.getString("image"),
                toLocalDateTime(rs.getTimestamp("created_at"))
        ));
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    public static class DashboardStats {
        private final Long plantCount;
        private final Long articleCount;
        private final Long researchCount;
        private final Long userCount;
        private final Long totalViews;

        public DashboardStats(Long plantCount, Long articleCount, Long researchCount, Long userCount, Long totalViews) {
            this.plantCount = plantCount;
            this.articleCount = articleCount;
            this.researchCount = researchCount;
            this.userCount = userCount;
            this.totalViews = totalViews;
        }

        public Long getPlantCount() {
            return plantCount;
        }

        public Long getArticleCount() {
            return articleCount;
        }

        public Long getResearchCount() {
            return researchCount;
        }

        public Long getUserCount() {
            return userCount;
        }

        public Long getTotalViews() {
            return totalViews;
        }
    }

    public static class DashboardItem {
        private final String title;
        private final String slug;
        private final String image;
        private final LocalDateTime createdAt;

        public DashboardItem(String title, String slug, String image, LocalDateTime createdAt) {
            this.title = title;
            this.slug = slug;
            this.image = image;
            this.createdAt = createdAt;
        }

        public String getTitle() {
            return title;
        }

        public String getSlug() {
            return slug;
        }

        public String getImage() {
            return image;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }

    @RequestMapping(value = {"/list-article"}, method = RequestMethod.GET)
    public String listArticle() {
        return "admin/list-article";
    }

    @RequestMapping(value = {"/list-diseases"}, method = RequestMethod.GET)
    public String listDiseases() {
        return "admin/list-diseases";
    }

    @RequestMapping(value = {"/list-families"}, method = RequestMethod.GET)
    public String listFamilies() {
        return "admin/list-families";
    }

    @RequestMapping(value = {"/list-plant"}, method = RequestMethod.GET)
    public String listPlant() {
        return "admin/list-plant";
    }

    @RequestMapping(value = {"/list-user"}, method = RequestMethod.GET)
    public String listUser() {
        return "admin/list-user";
    }

    @RequestMapping(value = {"/list-research"}, method = RequestMethod.GET)
    public String listResearch() {
        return "admin/list-research";
    }

    @RequestMapping(value = {"/list-expert"}, method = RequestMethod.GET)
    public String listExpert() {
        return "admin/list-expert";
    }
}
