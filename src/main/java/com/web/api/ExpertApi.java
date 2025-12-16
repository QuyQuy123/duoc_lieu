package com.web.api;

import com.web.entity.Article;
import com.web.entity.Diseases;
import com.web.entity.Expert;
import com.web.enums.ArticleStatus;
import com.web.service.DiseasesService;
import com.web.service.ExpertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expert")
public class ExpertApi {

    @Autowired
    private ExpertService expertService;

    @GetMapping("/admin/all")
    public Page<Expert> getAll(Pageable pageable,@RequestParam(required = false) String q) {
        return expertService.getAll(q, pageable);
    }


    @GetMapping("/public/all")
    public Page<Expert> getAllPublic(Pageable pageable,@RequestParam(required = false) String q,@RequestParam(required = false) String specialization) {
        return expertService.getAllPublic(q, specialization, pageable);
    }

    @GetMapping("/public/find-by-id")
    public ResponseEntity<Expert> findById(@RequestParam Long id) {
        return ResponseEntity.ok(expertService.findById(id));
    }

    @PostMapping("/admin/create")
    public ResponseEntity<Expert> save(@RequestBody Expert expert) {
        return ResponseEntity.ok(expertService.create(expert));
    }

    @DeleteMapping("/admin/delete")
    public ResponseEntity<String> delete(@RequestParam Long id) {
        expertService.delete(id);
        return ResponseEntity.ok("Xóa thành công");
    }

}
