package com.web.service;

import com.web.dto.PlantImp;
import com.web.dto.PlantSearch;
import com.web.dto.request.PlantRequestDto;
import com.web.entity.Diseases;
import com.web.entity.Plant;
import com.web.entity.PlantDiseases;
import com.web.entity.PlantMedia;
import com.web.enums.PlantStatus;
import com.web.exception.MessageException;
import com.web.repository.PlantDiseasesRepository;
import com.web.repository.PlantMediaRepository;
import com.web.repository.PlantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PlantService {

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private PlantMediaRepository plantMediaRepository;

    @Autowired
    private PlantDiseasesRepository plantDiseasesRepository;

    public Plant saveOrUpdate(PlantRequestDto dto){
        Plant plant = dto.getPlant();
        if(plant.getId() != null){
            Plant exist = plantRepository.findById(plant.getId()).get();
            plant.setCreatedBy(exist.getCreatedBy());
            plant.setCreatedAt(exist.getCreatedAt());
            plant.setViews(exist.getViews());
        }
        else{

        }
        plantRepository.save(plant);
        List<PlantMedia> list = new ArrayList<>();
        for(String s : dto.getImages()){
            PlantMedia md = new PlantMedia();
            md.setPlant(plant);
            md.setImageLink(s);
            list.add(md);
        }
        plantMediaRepository.saveAll(list);
        plantDiseasesRepository.deleteByPlant(plant.getId());
        for(Long id : dto.getDiseasesIds()){
            Diseases diseases = new Diseases();
            diseases.setId(id);
            PlantDiseases plantDiseases = new PlantDiseases();
            plantDiseases.setDiseases(diseases);
            plantDiseases.setPlant(plant);
            plantDiseasesRepository.save(plantDiseases);
        }
        return null;
    }

    public Page<PlantRepository.PlantAdminListView> getAllByAdmin(Pageable pageable, String q, Long familiesId,PlantStatus plantStatus) {
        // Xử lý chuỗi tìm kiếm: loại bỏ khoảng trắng đầu cuối, nếu rỗng thì set null
        String search = (q != null && !q.trim().isEmpty()) ? q.trim() : null;
        return plantRepository.searchAdminList(search, familiesId, plantStatus, pageable);
    }

    public Page<PlantRepository.PlantPublicListView> getAllByPublic(Pageable pageable, PlantSearch search) {
        PlantSearch safeSearch = search == null ? new PlantSearch() : search;
        String q = (safeSearch.getSearch() != null && !safeSearch.getSearch().trim().isEmpty())
                ? safeSearch.getSearch().trim()
                : null;
        List<Long> familiesId = safeSearch.getFamiliesId() == null ? Collections.emptyList() : safeSearch.getFamiliesId();
        List<Long> diseases = safeSearch.getDiseases() == null ? Collections.emptyList() : safeSearch.getDiseases();
        boolean familiesEmpty = familiesId.isEmpty();
        boolean diseasesEmpty = diseases.isEmpty();

        return plantRepository.searchPublicList(
                q,
                familiesEmpty,
                familiesEmpty ? Collections.singletonList(-1L) : familiesId,
                diseasesEmpty,
                diseasesEmpty ? Collections.singletonList(-1L) : diseases,
                PlantStatus.DA_XUAT_BAN,
                pageable
        );
    }

    public void delete(Long id) {
        try {
            plantRepository.deleteById(id);
        }
        catch (Exception e){
            throw new MessageException("Có lỗi khi xóa cây thực vật này: "+e.getMessage());
        }
    }

    public void deleteImage(Long id) {
        try {
            plantMediaRepository.deleteById(id);
        }
        catch (Exception e){
            throw new MessageException("Có lỗi khi xóa cây thực vật này: "+e.getMessage());
        }
    }

    public Plant findById(Long id){
        return plantRepository.findById(id).orElse(null);
    }

    public List<Plant> cayNoiBatIndex() {
        List<Plant> list = plantRepository.cayNoiBat();
        return list;
    }

    public Plant findBySlug(String slug) {
        Optional<Plant> optionalBlog = plantRepository.findBySlug(slug);
        return optionalBlog.orElse(null);
    }

    public List<PlantImp> findAllName(){
        return plantRepository.findAllName();
    }

    /**
     * Ghi danh sách cây dược liệu ra CSV (Excel có thể mở được).
     */
    public void writePlantsToCsv(Writer writer, String q, Long familiesId, PlantStatus plantStatus) {
        try {
            String search = (q != null && !q.trim().isEmpty()) ? q.trim() : null;
            List<Plant> plants = plantRepository.searchForExport(search, familiesId, plantStatus);

            // Header
            writer.write("ID,TEN_CAY,TEN_KHOA_HOC,HO_THUC_VAT,BO_PHAN_DUNG,TRANG_THAI,NGAY_TAO,NGAY_CAP_NHAT\n");

            for (Plant p : plants) {
                String line = String.format(
                        "%d,%s,%s,%s,%s,%s,%s,%s\n",
                        p.getId(),
                        escapeCsv(p.getName()),
                        escapeCsv(p.getScientificName()),
                        p.getFamilies() != null ? escapeCsv(p.getFamilies().getName()) : "",
                        escapeCsv(p.getPartsUsed()),
                        p.getPlantStatus() != null ? p.getPlantStatus().name() : "",
                        p.getCreatedAt() != null ? p.getCreatedAt().toString() : "",
                        p.getUpdatedAt() != null ? p.getUpdatedAt().toString() : ""
                );
                writer.write(line);
            }
            writer.flush();
        } catch (IOException e) {
            throw new MessageException("Lỗi khi xuất dữ liệu cây dược liệu: " + e.getMessage());
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
