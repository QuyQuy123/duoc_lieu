package com.web.repository;

import com.web.dto.PlantImp;
import com.web.enums.PlantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.web.dto.response.PlantWithMedia;
import com.web.entity.Plant;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlantRepository extends JpaRepository<Plant, Long>, JpaSpecificationExecutor<Plant> {

    @Query("""
            SELECT p FROM Plant p
            WHERE 
                (:q IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(p.scientificName) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(p.otherNames) LIKE LOWER(CONCAT('%', :q, '%')))
            AND (:familiesId IS NULL OR p.families.id = :familiesId)
            AND (:plantStatus IS NULL OR p.plantStatus = :plantStatus)
            """)
    Page<Plant> searchByAdmin(
            @Param("q") String q,
            @Param("familiesId") Long familiesId,
            @Param("plantStatus") PlantStatus plantStatus,
            Pageable pageable
    );

    @Query("""
            SELECT p.id as id,
                   p.image as image,
                   p.name as name,
                   p.scientificName as scientificName,
                   f.name as familyName,
                   p.partsUsed as partsUsed,
                   p.plantStatus as plantStatus,
                   p.createdAt as createdAt,
                   p.updatedAt as updatedAt
            FROM Plant p
            LEFT JOIN p.families f
            WHERE
                (:q IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(p.scientificName) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(p.otherNames) LIKE LOWER(CONCAT('%', :q, '%')))
            AND (:familiesId IS NULL OR p.families.id = :familiesId)
            AND (:plantStatus IS NULL OR p.plantStatus = :plantStatus)
            """)
    Page<PlantAdminListView> searchAdminList(
            @Param("q") String q,
            @Param("familiesId") Long familiesId,
            @Param("plantStatus") PlantStatus plantStatus,
            Pageable pageable
    );

    @Query("""
            SELECT p FROM Plant p
            WHERE 
                (:q IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(p.scientificName) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(p.otherNames) LIKE LOWER(CONCAT('%', :q, '%')))
            AND (:familiesId IS NULL OR p.families.id = :familiesId)
            AND (:plantStatus IS NULL OR p.plantStatus = :plantStatus)
            """)
    List<Plant> searchForExport(
            @Param("q") String q,
            @Param("familiesId") Long familiesId,
            @Param("plantStatus") PlantStatus plantStatus
    );

    @Query("""
            SELECT p FROM Plant p
            WHERE 
                (:q IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(p.scientificName) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(p.otherNames) LIKE LOWER(CONCAT('%', :q, '%')))
            AND (:familiesId IS NULL OR p.families.id = :familiesId)
            """)
    Page<Plant> findAllPublic(
            @Param("q") String q,
            @Param("familiesId") Long familiesId,
            Pageable pageable
    );

    @Query(value = "select p.* from plants p where p.featured = 1 limit 20", nativeQuery = true)
    List<Plant> cayNoiBat();

    @Query("select p from Plant p where p.slug = ?1")
    Optional<Plant> findBySlug(String slug);

    @Query("select p.id as id, p.name as name from Plant p order by p.name asc ")
    List<PlantImp> findAllName();

    interface PlantAdminListView {
        Long getId();
        String getImage();
        String getName();
        String getScientificName();
        String getFamilyName();
        String getPartsUsed();
        PlantStatus getPlantStatus();
        LocalDateTime getCreatedAt();
        LocalDateTime getUpdatedAt();

        default String getColor() {
            return getPlantStatus() == null ? "" : getPlantStatus().getColor();
        }

        default String getStatusLabel() {
            return getPlantStatus() == null ? "" : getPlantStatus().getLabel();
        }
    }
}
