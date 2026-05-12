package com.web.repository;

import com.web.enums.ArticleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.web.entity.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {

    boolean existsByTitle(String title);

    boolean existsBySlug(String slug);

    // Tìm kiếm + lọc theo trạng thái (nếu truyền)
    @Query("""
            SELECT a FROM Article a
            WHERE (:search IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%')) 
                   OR LOWER(a.excerpt) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:status IS NULL OR a.articleStatus = :status)
            """)
    Page<Article> findAllByParam(String search, ArticleStatus status, Pageable pageable);

    @Query("""
            SELECT a.id as id,
                   a.imageBanner as imageBanner,
                   a.title as title,
                   d.name as diseasesName,
                   a.excerpt as excerpt,
                   u.fullname as userFullname,
                   a.views as views,
                   a.articleStatus as articleStatus,
                   a.createdAt as createdAt,
                   a.updatedAt as updatedAt
            FROM Article a
            LEFT JOIN a.diseases d
            LEFT JOIN a.user u
            WHERE (:search IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(a.excerpt) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:status IS NULL OR a.articleStatus = :status)
            """)
    Page<ArticleAdminListView> findAdminList(String search, ArticleStatus status, Pageable pageable);

    @Query("""
            SELECT a FROM Article a
            WHERE (:search IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%')) 
                   OR LOWER(a.excerpt) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:diseasesId IS NULL OR a.diseases.id = :diseasesId)
              and a.articleStatus = 'DA_XUAT_BAN'
            """)
    Page<Article> findAllByParam(String search,Long diseasesId, Pageable pageable);

    @Query("""
            SELECT a.id as id,
                   a.imageBanner as imageBanner,
                   a.title as title,
                   a.slug as slug,
                   d.name as diseasesName,
                   a.excerpt as excerpt,
                   u.fullname as userFullname,
                   a.views as views,
                   a.isFeatured as isFeatured,
                   a.createdAt as createdAt
            FROM Article a
            LEFT JOIN a.diseases d
            LEFT JOIN a.user u
            WHERE (:search IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(a.excerpt) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:diseasesId IS NULL OR a.diseases.id = :diseasesId)
              AND a.articleStatus = 'DA_XUAT_BAN'
            """)
    Page<ArticlePublicListView> findPublicList(String search, Long diseasesId, Pageable pageable);

    @Query("select a from Article a where a.slug = ?1")
    Optional<Article> findBySlug(String slug);

    @Query("""
            SELECT a FROM Article a
            WHERE (:search IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%')) 
                   OR LOWER(a.excerpt) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:status IS NULL OR a.articleStatus = :status)
            """)
    java.util.List<Article> findAllForExport(String search, ArticleStatus status);

    interface ArticleAdminListView {
        Long getId();
        String getImageBanner();
        String getTitle();
        String getDiseasesName();
        String getExcerpt();
        String getUserFullname();
        Long getViews();
        ArticleStatus getArticleStatus();
        LocalDateTime getCreatedAt();
        LocalDateTime getUpdatedAt();

        default String getColor() {
            return getArticleStatus() == null ? "" : getArticleStatus().getColor();
        }

        default String getStatusLabel() {
            return getArticleStatus() == null ? "" : getArticleStatus().getLabel();
        }
    }

    interface ArticlePublicListView {
        Long getId();
        String getImageBanner();
        String getTitle();
        String getSlug();
        String getDiseasesName();
        String getExcerpt();
        String getUserFullname();
        Long getViews();
        Boolean getIsFeatured();
        LocalDateTime getCreatedAt();
    }
}
