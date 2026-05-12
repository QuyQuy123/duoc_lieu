package com.web.service;

import com.web.entity.Article;
import com.web.enums.ArticleStatus;
import com.web.exception.MessageException;
import com.web.repository.ArticleRepository;
import com.web.utils.SlugGenerator;
import com.web.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;

@Service
//@Transactional
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserUtils userUtils;

    public Page<ArticleRepository.ArticleAdminListView> getAll(String search, ArticleStatus status, Pageable pageable) {
        return articleRepository.findAdminList(
                (search == null || search.trim().isEmpty()) ? null : search.trim(),
                status,
                pageable
        );
    }

    public Page<ArticleRepository.ArticlePublicListView> getAllPublic(String search, Long diseasesId, Pageable pageable) {
        return articleRepository.findPublicList(
                (search == null || search.trim().isEmpty()) ? null : search.trim(),
                diseasesId,
                pageable
        );
    }

    public Article findById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new MessageException("Không tìm thấy bài viết"));
    }

    /**
     * Tạo mới hoặc cập nhật bài viết (nếu có id)
     */
    public Article save(Article article) {
        if (article.getId() == null) {
            if (articleRepository.existsByTitle(article.getTitle())) {
                throw new MessageException("Tiêu đề đã tồn tại");
            }
            if (articleRepository.existsBySlug(article.getSlug())) {
                throw new MessageException("Slug đã tồn tại");
            }
            if(article.getSlug() == null){
                article.setSlug(SlugGenerator.generateSlug(article.getTitle()));
            }
            article.setUser(userUtils.getUserWithAuthority());
            return articleRepository.save(article);
        }
        else {
            // === UPDATE ===
            Article existing = findById(article.getId());

            // Nếu đổi title -> kiểm tra trùng
            if (!existing.getTitle().equals(article.getTitle()) &&
                    articleRepository.existsByTitle(article.getTitle())) {
                throw new MessageException("Tiêu đề đã tồn tại");
            }

            // Nếu đổi slug -> kiểm tra trùng
            if (!existing.getSlug().equals(article.getSlug()) &&
                    articleRepository.existsBySlug(article.getSlug())) {
                throw new MessageException("Slug đã tồn tại");
            }

            existing.setTitle(article.getTitle());
            if(article.getSlug() == null){
                existing.setSlug(SlugGenerator.generateSlug(article.getTitle()));
            }
            else{
                existing.setSlug(article.getSlug());
            }
            existing.setExcerpt(article.getExcerpt());
            existing.setContent(article.getContent());
            existing.setImageBanner(article.getImageBanner());
            existing.setIsFeatured(article.getIsFeatured());
            existing.setAllowComments(article.getAllowComments());
            if(article.getArticleStatus().equals(ArticleStatus.DA_XUAT_BAN) && !existing.getArticleStatus().equals(ArticleStatus.DA_XUAT_BAN)){
                existing.setPublishedAt(LocalDateTime.now());
            }
            existing.setArticleStatus(article.getArticleStatus());
            existing.setDiseases(article.getDiseases());
            return articleRepository.save(existing);
        }
    }

    public void delete(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new MessageException("Bài viết không tồn tại");
        }
        articleRepository.deleteById(id);
    }

    public Article findBySlug(String slug) {
        return articleRepository.findBySlug(slug).orElse(null);
    }

    /**
     * Ghi danh sách bài viết ra CSV.
     */
    public void writeArticlesToCsv(Writer writer, String q, ArticleStatus status) {
        try {
            String search = (q != null && !q.trim().isEmpty()) ? q.trim() : null;
            java.util.List<Article> list = articleRepository.findAllForExport(search, status);
            writer.write("ID,TIEU_DE,CONG_DUNG,TOM_TAT,TAC_GIA,TRANG_THAI,NGAY_TAO,NGAY_CAP_NHAT\n");
            for (Article a : list) {
                String line = String.format(
                        "%d,%s,%s,%s,%s,%s,%s,%s\n",
                        a.getId(),
                        escapeCsv(a.getTitle()),
                        a.getDiseases() != null ? escapeCsv(a.getDiseases().getName()) : "",
                        escapeCsv(a.getExcerpt()),
                        a.getUser() != null ? escapeCsv(a.getUser().getFullname()) : "",
                        a.getArticleStatus() != null ? a.getArticleStatus().name() : "",
                        a.getCreatedAt() != null ? a.getCreatedAt().toString() : "",
                        a.getUpdatedAt() != null ? a.getUpdatedAt().toString() : ""
                );
                writer.write(line);
            }
            writer.flush();
        } catch (IOException e) {
            throw new MessageException("Lỗi khi xuất dữ liệu bài viết: " + e.getMessage());
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        String v = value.replace("\"", "\"\"");
        if (v.contains(",") || v.contains("\"") || v.contains("\n")) {
            return "\"" + v + "\"";
        }
        return v;
    }
}
