package com.web.utils;

import com.web.entity.Plant;
import com.web.repository.PlantRepository;
import com.web.utils.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class PlantImageSyncService {

    private final CloudinaryService cloudinaryService;
    private final PlantRepository plantRepository;

    private final String IMAGE_FOLDER = "D:\\code\\Quan_ly_cay_thuoc_sourcecode\\upload\\2025_06";

    public void uploadAndSyncPlantImages() {
        try {
            // B1. Lấy tất cả file trong thư mục
            File folder = new File(IMAGE_FOLDER);
            File[] files = folder.listFiles((dir, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".jpg") || lower.endsWith(".png") || lower.endsWith(".jpeg") || lower.endsWith(".webp");
            });

            if (files == null || files.length == 0) {
                System.out.println("❌ Không tìm thấy file ảnh trong thư mục: " + IMAGE_FOLDER);
                return;
            }

            // B2. Upload tất cả file song song (đa luồng)
            List<String> uploadedUrls = Collections.synchronizedList(new ArrayList<>());
            ExecutorService executor = Executors.newFixedThreadPool(Math.min(files.length, 8));

            for (File file : files) {
                executor.submit(() -> {
                    try {
                        String url = cloudinaryService.uploadFile(file); // uploadFile(File file)
                        uploadedUrls.add(url);
                        System.out.println("✅ Uploaded: " + file.getName());
                    } catch (Exception e) {
                        System.err.println("❌ Lỗi upload file: " + file.getName());
                        e.printStackTrace();
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(15, TimeUnit.MINUTES);

            System.out.println("🎯 Tổng số ảnh upload thành công: " + uploadedUrls.size());

            // B3. Lấy toàn bộ plant từ database
            List<Plant> plants = plantRepository.findAll();
            if (plants.isEmpty()) {
                System.out.println("⚠️ Không có plant nào trong database.");
                return;
            }

            // B4. Gán ảnh tuần hoàn cho từng plant
            int imgCount = uploadedUrls.size();
            for (int i = 0; i < plants.size(); i++) {
                String imageUrl = uploadedUrls.get(i % imgCount); // quay vòng lại
                plants.get(i).setImage(imageUrl);
            }

            // B5. Lưu lại tất cả
            plantRepository.saveAll(plants);
            System.out.println("✅ Cập nhật ảnh cho " + plants.size() + " plant thành công!");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Upload bị gián đoạn", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Có lỗi khi upload và gán ảnh", e);
        }
    }
}
