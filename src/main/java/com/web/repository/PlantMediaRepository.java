package com.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.web.entity.PlantMedia;

import javax.print.attribute.standard.Media;
import java.util.List;

@Repository
public interface PlantMediaRepository extends JpaRepository<PlantMedia, Long> {

}
