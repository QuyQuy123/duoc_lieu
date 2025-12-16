package com.web.api;

import com.web.dto.PlantImp;
import com.web.dto.PlantSearch;
import com.web.dto.request.PlantRequestDto;
import com.web.entity.Plant;
import com.web.enums.PlantStatus;
import com.web.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plant")
public class PlantApi {

    @Autowired
    private PlantService plantService;

    @GetMapping("/admin/all")
    public Page<Plant> getAll(
            Pageable pageable,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long familiesId,
            @RequestParam(required = false) PlantStatus plantStatus
    ) {
        return plantService.getAllByAdmin(pageable, q, familiesId,plantStatus);
    }

    @PostMapping("/public/all")
    public Page<Plant> getAll(Pageable pageable,@RequestBody PlantSearch plantSearch) {
        return plantService.getAllByPublic(pageable, plantSearch);
    }

    @GetMapping("/admin/all-name")
    public List<PlantImp> getAllName() {
        return plantService.findAllName();
    }

    @PostMapping("/admin/create")
    public ResponseEntity<?> create(@RequestBody PlantRequestDto dto) {
        return ResponseEntity.ok(plantService.saveOrUpdate(dto));
    }

    @GetMapping("/public/all-status")
    public List<Map<String, String>> getAllStatuses() {
        List<Map<String, String>> list = new ArrayList<>();
        for (PlantStatus status : PlantStatus.values()) {
            Map<String, String> item = new HashMap<>();
            item.put("name", status.name());
            item.put("label", status.getLabel());
            list.add(item);
        }
        return list;
    }

    @DeleteMapping("/admin/delete")
    public ResponseEntity<String> delete(@RequestParam Long id) {
        plantService.delete(id);
        return ResponseEntity.ok("Xóa thành công");
    }

    @DeleteMapping("/admin/delete-image")
    public ResponseEntity<String> deleteImage(@RequestParam Long id) {
        plantService.deleteImage(id);
        return ResponseEntity.ok("Xóa thành công");
    }

    @GetMapping("/public/find-by-id")
    public ResponseEntity<?> findById(@RequestParam Long id) {
        Plant result = plantService.findById(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/public/cay-noi-bat-index")
    public ResponseEntity<?> cayNoiBatIndex() {
        List<Plant> result = plantService.cayNoiBatIndex();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/admin/export")
    public void exportPlants(
            HttpServletResponse response,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long familiesId,
            @RequestParam(required = false) PlantStatus plantStatus
    ) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"plants_export.csv\"");
        plantService.writePlantsToCsv(response.getWriter(), q, familiesId, plantStatus);
    }
}
