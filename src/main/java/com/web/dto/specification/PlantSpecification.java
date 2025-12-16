package com.web.dto.specification;

import com.web.entity.Diseases;
import com.web.entity.Plant;
import com.web.entity.PlantDiseases;
import com.web.enums.PlantStatus;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class PlantSpecification implements Specification<Plant> {

    private String search;

    private List<Long> familiesId = new ArrayList<>();

    private List<Long> diseases = new ArrayList<>();

    @Override
    public Predicate toPredicate(Root<Plant> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();
        query.distinct(true);
        if (diseases != null && !diseases.isEmpty()) {
            Join<Plant, PlantDiseases> productCategoryJoin = root.join("plantDiseases", JoinType.INNER);
            Join<PlantDiseases, Diseases> join = productCategoryJoin.join("diseases", JoinType.INNER);
            predicate = cb.and(predicate, join.get("id").in(diseases));
        }

        if (familiesId != null && !familiesId.isEmpty()) {
            predicate = cb.and(predicate, root.get("families").get("id").in(familiesId));
        }

        if (search != null && !search.isEmpty() && !search.isBlank()) {
            String searchPattern = "%" + search.trim() + "%";
            Predicate namePredicate = cb.like(cb.lower(root.get("name")), searchPattern.toLowerCase());
            Predicate scientificNamePredicate = cb.like(cb.lower(root.get("scientificName")), searchPattern.toLowerCase());
            Predicate otherNamesPredicate = cb.like(cb.lower(root.get("otherNames")), searchPattern.toLowerCase());
            Predicate searchPredicate = cb.or(namePredicate, scientificNamePredicate, otherNamesPredicate);
            predicate = cb.and(predicate, searchPredicate);
        }

        predicate = cb.and(predicate, cb.equal(root.get("plantStatus"), PlantStatus.DA_XUAT_BAN));
        return predicate;
    }
}
