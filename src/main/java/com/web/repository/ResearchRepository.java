package com.web.repository;

import com.web.enums.ResearchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.web.entity.*;

import java.util.Optional;

@Repository
public interface ResearchRepository extends JpaRepository<Research, Long>, JpaSpecificationExecutor<Research> {
    Boolean existsByTitle(String title);

    @Query("""
            SELECT a FROM Research a
            WHERE (:search IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%')) 
                   OR LOWER(a.authors) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:status IS NULL OR a.researchStatus = :status)
            """)
    Page<Research> findAllByParam(String search, ResearchStatus status, Pageable pageable);

    @Query("""
            SELECT a FROM Research a
            WHERE (:search IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%')) 
                   OR LOWER(a.authors) LIKE LOWER(CONCAT('%', :search, '%')))
                   AND (:field IS NULL OR a.field = :field)
                   AND (:publishedYear IS NULL OR a.publishedYear = :publishedYear)
            """)
    Page<Research> findAllByParam(String search,String field, Integer publishedYear, Pageable pageable);

    boolean existsBySlug(String slug);

    @Query("select r from Research r where r.slug = ?1")
    Optional<Research> findBySlug(String slug);

    @Query("""
            SELECT a FROM Research a
            WHERE (:search IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%')) 
                   OR LOWER(a.authors) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:status IS NULL OR a.researchStatus = :status)
            """)
    java.util.List<Research> findAllForExport(String search, ResearchStatus status);
}
