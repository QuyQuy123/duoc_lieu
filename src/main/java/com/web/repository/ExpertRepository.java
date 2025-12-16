package com.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.web.entity.*;

import java.util.Optional;

@Repository
public interface ExpertRepository extends JpaRepository<Expert, Long> {
    Optional<Expert> findBySlug(String slug);


//    Page<Expert> findByPlantsContaining(Plant plant, Pageable pageable);

    @Query("SELECT e FROM Expert e WHERE e.name LIKE %?1% OR e.specialization LIKE %?1% OR e.bio LIKE %?1%")
    Page<Expert> search(String keyword, Pageable pageable);

    @Query("""
            SELECT p FROM Expert p
            WHERE 
                (:q IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(p.contactEmail) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%')))
            """)
    Page<Expert> findAllByParam(String q, Pageable pageable);

    @Query("""
            SELECT p FROM Expert p
            WHERE 
                (:q IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(p.contactEmail) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%')))
                AND (:specialization IS NULL OR p.specialization = :specialization) 
            """)
    Page<Expert> findAllByParam(String q, String specialization, Pageable pageable);

    boolean existsBySlug(String slug);
}
