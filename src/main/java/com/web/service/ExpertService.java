package com.web.service;

import com.web.entity.Article;
import com.web.entity.Expert;
import com.web.enums.ArticleStatus;
import com.web.exception.MessageException;
import com.web.repository.ArticleRepository;
import com.web.repository.ExpertRepository;
import com.web.utils.SlugGenerator;
import com.web.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ExpertService {

    @Autowired
    private ExpertRepository expertRepository;

    public Expert create(Expert expert){
        expert.setStatus(1);
        expertRepository.save(expert);
        return expert;
    }

    public Page<ExpertRepository.ExpertAdminListView> getAll(String search, Pageable pageable) {
        return expertRepository.findAdminList(
                (search == null || search.trim().isEmpty()) ? null : search.trim(),
                pageable
        );
    }

    public Page<Expert> getAllPublic(String search, String specialization, Pageable pageable) {
        return expertRepository.findAllByParam(
                (search == null || search.trim().isEmpty()) ? null : search.trim(),
                specialization,
                pageable
        );
    }

    public Expert findById(Long id) {
        return expertRepository.findById(id)
                .orElseThrow(() -> new MessageException("Không tìm thấy chuyên gia"));
    }


    public void delete(Long id) {
        if (!expertRepository.existsById(id)) {
            throw new MessageException("Chuyên gia không tồn tại");
        }
        expertRepository.deleteById(id);
    }
}
