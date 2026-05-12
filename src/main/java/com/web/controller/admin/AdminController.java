package com.web.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final long DASHBOARD_CACHE_MS = 30_000L;
    private volatile DashboardData cachedDashboardData;
    private volatile long cachedDashboardAt;

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
        DashboardData dashboardData = loadDashboardData();
        DashboardStats stats = dashboardData.getStats();

        model.addAttribute("plantCount", stats.getPlantCount());
        model.addAttribute("articleCount", stats.getArticleCount());
        model.addAttribute("researchCount", stats.getResearchCount());
        model.addAttribute("userCount", stats.getUserCount());
        model.addAttribute("totalViews", stats.getTotalViews());
        model.addAttribute("recentPlants", dashboardData.getRecentPlants());
        model.addAttribute("recentArticles", dashboardData.getRecentArticles());
        model.addAttribute("recentResearches", dashboardData.getRecentResearches());
        model.addAttribute("recentUsers", dashboardData.getRecentUsers());
        return "admin/dashboard";
    }

    private DashboardData loadDashboardData() {
        long now = System.currentTimeMillis();
        DashboardData cache = cachedDashboardData;
        if (cache != null && now - cachedDashboardAt < DASHBOARD_CACHE_MS) {
            return cache;
        }

        DashboardStats stats = loadDashboardStats();
        List<DashboardItem> recentPlants = new ArrayList<>();
        List<DashboardItem> recentArticles = new ArrayList<>();
        List<DashboardItem> recentResearches = new ArrayList<>();
        List<DashboardItem> recentUsers = new ArrayList<>();

        String sql = """
                select * from (
                    select 'plant' as source, name as title, slug, image, created_at
                    from plants
                    order by created_at desc
                    limit 5
                ) plants_recent
                union all
                select * from (
                    select 'article' as source, title, slug, image_banner as image, created_at
                    from articles
                    order by created_at desc
                    limit 5
                ) articles_recent
                union all
                select * from (
                    select 'research' as source, title, slug, image_banner as image, created_at
                    from research
                    order by created_at desc
                    limit 5
                ) research_recent
                union all
                select * from (
                    select 'user' as source,
                           coalesce(nullif(fullname, ''), email, username) as title,
                           null as slug,
                           null as image,
                           created_date as created_at
                    from users
                    order by created_date desc
                    limit 5
                ) users_recent
                """;

        jdbcTemplate.query(sql, rs -> {
            DashboardItem item = new DashboardItem(
                    rs.getString("title"),
                    rs.getString("slug"),
                    rs.getString("image"),
                    toLocalDateTime(rs.getTimestamp("created_at"))
            );

            switch (rs.getString("source")) {
                case "plant":
                    recentPlants.add(item);
                    break;
                case "article":
                    recentArticles.add(item);
                    break;
                case "research":
                    recentResearches.add(item);
                    break;
                case "user":
                    recentUsers.add(item);
                    break;
                default:
                    break;
            }
        });

        DashboardData data = new DashboardData(stats, recentPlants, recentArticles, recentResearches, recentUsers);
        cachedDashboardData = data;
        cachedDashboardAt = now;
        return data;
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

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    public static class DashboardData {
        private final DashboardStats stats;
        private final List<DashboardItem> recentPlants;
        private final List<DashboardItem> recentArticles;
        private final List<DashboardItem> recentResearches;
        private final List<DashboardItem> recentUsers;

        public DashboardData(
                DashboardStats stats,
                List<DashboardItem> recentPlants,
                List<DashboardItem> recentArticles,
                List<DashboardItem> recentResearches,
                List<DashboardItem> recentUsers
        ) {
            this.stats = stats;
            this.recentPlants = recentPlants;
            this.recentArticles = recentArticles;
            this.recentResearches = recentResearches;
            this.recentUsers = recentUsers;
        }

        public DashboardStats getStats() {
            return stats;
        }

        public List<DashboardItem> getRecentPlants() {
            return recentPlants;
        }

        public List<DashboardItem> getRecentArticles() {
            return recentArticles;
        }

        public List<DashboardItem> getRecentResearches() {
            return recentResearches;
        }

        public List<DashboardItem> getRecentUsers() {
            return recentUsers;
        }
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
