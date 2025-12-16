package com.web.service;

import com.google.gson.*;
import com.web.entity.Plant;
import com.web.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.net.http.*;
import java.sql.Date; // Không cần thiết cho PlantService, nhưng giữ lại từ template
import java.time.LocalDate; // Không cần thiết cho PlantService, nhưng giữ lại từ template
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Autowired
    private PlantRepository plantRepository;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    // Regex để tìm kiếm URL ảnh được chèn: Ảnh kèm theo: [link]
    private static final Pattern IMAGE_URL_PATTERN = Pattern.compile("Ảnh kèm theo:\\s*(https?://\\S+)");


    /**
     * Gửi tin nhắn người dùng cùng với dữ liệu cây dược liệu đến API Gemini.
     * @param userMessage Tin nhắn của người dùng.
     * @return Phản hồi từ Gemini hoặc thông báo lỗi.
     */
    public String chatWithGemini(String userMessage, HttpSession session) {
        if(session.getAttribute("history-chat") == null){
            session.setAttribute("Number", 1);
            session.setAttribute("history-chat", "Lịch sử chat của người dùng:\nCâu 1: "+userMessage+"\n");
        }

        else{
            String his = (String) session.getAttribute("history-chat");
            Integer num = (Integer) session.getAttribute("Number");
            his = his + "\n"+"Câu "+num.toString()+": "+userMessage;
            session.setAttribute("Number", ++num);
            session.setAttribute("history-chat",his);
        }
        String his =  (String) session.getAttribute("history-chat");
        try {
            // 1. Lấy dữ liệu plant
            List<Plant> plants = plantRepository.findAll();

            // 2. Chuẩn bị dữ liệu plant cho prompt
            final List<String> fieldsToExcludeByName = Arrays.asList(
                    "description",
                    "createdAt",
                    "updatedAt",
                    "createdBy",
                    "updatedBy"
                    // Không cần thêm plantDiseases, plantMedia, families vào đây
            );

            // Tên package Entity của bạn để loại trừ các Entity khác (phá vỡ vòng lặp)
            final String entityPackagePrefix = "com.web.entity";

            Gson gson = new GsonBuilder()
                    .setExclusionStrategies(new ExclusionStrategy() {
                        @Override
                        public boolean shouldSkipField(FieldAttributes f) {
                            Class<?> fieldType = f.getDeclaredClass();

                            // 1. Phá vỡ quan hệ vòng: Loại bỏ Collection (List/Set) và Map
                            if (Collection.class.isAssignableFrom(fieldType) ||
                                    Map.class.isAssignableFrom(fieldType)) {
                                return true;
                            }

                            // 2. Phá vỡ quan hệ vòng: Loại bỏ các trường Entity (ManyToOne, OneToOne)
                            if (fieldType.getName().startsWith(entityPackagePrefix) && !fieldType.equals(Plant.class)) {
                                return true;
                            }

                            // 3. Loại bỏ các trường theo tên
                            return fieldsToExcludeByName.contains(f.getName());
                        }
                        @Override
                        public boolean shouldSkipClass(Class<?> clazz) {
                            return false;
                        }
                    })
                    .create();

            String plantsJsonData = gson.toJson(plants);

            // 3. Xử lý ảnh và làm sạch userMessage
            String imageUrl = null;
            Matcher matcher = IMAGE_URL_PATTERN.matcher(userMessage);
            if (matcher.find()) {
                imageUrl = matcher.group(1);
                // Loại bỏ tag URL khỏi tin nhắn gốc để có câu hỏi sạch
                userMessage = userMessage.split("Ảnh kèm theo:")[0].trim();
            }
            // Nếu userMessage rỗng sau khi loại bỏ URL, đặt lại câu hỏi mặc định
            if (userMessage.isEmpty() && imageUrl != null) {
                userMessage = "Hãy xác định và phân tích cây dược liệu trong ảnh";
            }

            // 4. Ghép prompt
            String prompt = """
                Bạn là trợ lý AI của website quản lý cây dược liệu, trả lời bằng tiếng Việt, ngắn gọn, thân thiện, 
                trả lời dạng HTML nhé, nếu có link ảnh hãy trả lời dạng thẻ img, set độ rộng 150px cho tôi nhé.
                Các khả năng chính:
                - Tìm kiếm cây dược liệu phù hợp
                - Xác định cây dược liệu dựa vào link hình ảnh được cung cấp (nếu hình ảnh được gửi dạng link cloudinary)
                - Xác định công dụng, cách dùng, nơi trồng của cây dược liệu
                - Các câu hỏi khác thì tìm câu trả lời từ các nguồn khác database
                Đây là lịch sử câu hỏi trước đó của người dùng:
                %s
                
                Dưới đây là **dữ liệu cây dược liệu** từ database dạng json. Hãy sử dụng thông tin này để trả lời các câu hỏi liên quan đến cây dược liệu một cách chính xác nhất có thể:

                %s

                Câu hỏi của người dùng: %s
                """.formatted(his,plantsJsonData, userMessage);

            // Thêm URL ảnh (nếu có) vào cuối prompt để AI xử lý
            if (imageUrl != null) {
                prompt += "\n\n*** LƯU Ý: Hãy phân tích hình ảnh tại link sau để trả lời: " + imageUrl + " ***";
            }


            // 5. Gửi request Gemini
            JsonObject root = new JsonObject();
            JsonArray contents = new JsonArray();
            JsonObject userMsg = new JsonObject();
            userMsg.addProperty("role", "user");

            JsonArray parts = new JsonArray();
            JsonObject partText = new JsonObject();
            partText.addProperty("text", prompt);
            parts.add(partText);

            userMsg.add("parts", parts);
            contents.add(userMsg);

            // Thêm các cấu hình khác (ĐÃ SỬA LỖI TÊN TRƯỜNG config -> generationConfig)
            JsonObject generationConfig = new JsonObject();
            generationConfig.addProperty("temperature", 1); // Điều chỉnh tính sáng tạo
            root.add("generationConfig", generationConfig);
            root.add("contents", contents);
            // Xây dựng request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GEMINI_URL + "?key=" + geminiApiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(root.toString()))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 6. Xử lý phản hồi
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

            if (json.has("candidates")) {
                return json.getAsJsonArray("candidates")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("content")
                        .getAsJsonArray("parts")
                        .get(0).getAsJsonObject()
                        .get("text").getAsString();
            } else if (json.has("error")) {
                return "❌ Lỗi từ Gemini: " + json.getAsJsonObject("error").get("message").getAsString();
            } else {
                return "⚠️ Không nhận được phản hồi từ AI. Phản hồi đầy đủ: " + response.body();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Lỗi hệ thống: " + e.getMessage();
        }
    }


}