package com.web.service;

import com.google.gson.*;
import com.web.entity.Plant;
import com.web.repository.PlantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Autowired
    private PlantRepository plantRepository;

    // Model ổn định (đã từng gọi được): gemini-2.5-flash trên v1beta
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
            // kiểm tra xem có ảnh không
            String imageUrl = null;
            Matcher matcher = IMAGE_URL_PATTERN.matcher(userMessage);
            if (matcher.find()) {
                imageUrl = matcher.group(1);
                userMessage = userMessage.split("Ảnh kèm theo:")[0].trim();
            }


            if (imageUrl != null) {
                return answerImageOnly(userMessage, imageUrl, his);
            }

            // 1. Lấy dữ liệu plant cho trường hợp chat text (RAG với DB)
            List<Plant> plants = plantRepository.findAll();

            // 2. Chuẩn bị dữ liệu plant cho prompt
            final List<String> fieldsToExcludeByName = Arrays.asList(
                    "description",
                    "createdAt",
                    "updatedAt",
                    "createdBy",
                    "updatedBy"

            );


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

            // 3. Xử lý ảnh và làm sạch userMessage (đã tách ở trên)
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

            if (imageUrl != null) {
                JsonObject imagePart = buildImagePart(imageUrl);
                if (imagePart != null) {
                    parts.add(imagePart);
                }
            }

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

    private JsonObject buildImagePart(String imageUrl) {
        if (imageUrl.startsWith("data:image")) {
            return buildImagePartFromDataUrl(imageUrl);
        }
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(imageUrl)).build();
            HttpResponse<byte[]> res = client.send(req, HttpResponse.BodyHandlers.ofByteArray());
            if (res.statusCode() >= 300) return null;
            HttpHeaders headers = res.headers();
            String mime = headers.firstValue("content-type").orElse("image/jpeg");
            String base64 = Base64.getEncoder().encodeToString(res.body());
            JsonObject part = new JsonObject();
            JsonObject inline = new JsonObject();
            inline.addProperty("mimeType", mime);
            inline.addProperty("data", base64);
            part.add("inline_data", inline);
            return part;
        } catch (Exception ex) {
            return null;
        }
    }

    private JsonObject buildImagePartFromDataUrl(String dataUrl) {
        try {
            // data:[<mediatype>][;base64],<data>
            String[] parts = dataUrl.split(",");
            if (parts.length != 2) return null;
            String meta = parts[0]; // e.g. data:image/png;base64
            String base64Data = parts[1];
            String mime = "image/jpeg";
            int start = meta.indexOf(":");
            int semi = meta.indexOf(";");
            if (start != -1 && semi != -1 && semi > start) {
                mime = meta.substring(start + 1, semi);
            }
            JsonObject part = new JsonObject();
            JsonObject inline = new JsonObject();
            inline.addProperty("mimeType", mime);
            inline.addProperty("data", base64Data);
            part.add("inline_data", inline);
            return part;
        } catch (Exception e) {
            return null;
        }
    }

    private String identifyPlantNameFromImage(String imageUrl) {
        try {
            JsonObject root = new JsonObject();
            JsonArray contents = new JsonArray();

            JsonObject userMsg = new JsonObject();
            userMsg.addProperty("role", "user");

            JsonArray parts = new JsonArray();

            JsonObject textPart = new JsonObject();
            textPart.addProperty("text",
                    "Bạn là chuyên gia thực vật. Hãy nhìn vào ảnh sau và cho biết cây này là cây gì. " +
                            "Trả về DUY NHẤT một chuỗi JSON với cấu trúc: {\"common_name\":\"...\",\"scientific_name\":\"...\"}. " +
                            "Nếu không chắc chắn, hãy vẫn đoán tên gần đúng nhất.");
            parts.add(textPart);

            JsonObject imagePart = buildImagePart(imageUrl);
            if (imagePart != null) {
                parts.add(imagePart);
            }

            userMsg.add("parts", parts);
            contents.add(userMsg);

            root.add("contents", contents);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GEMINI_URL + "?key=" + geminiApiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(root.toString()))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            if (!json.has("candidates")) {
                log.warn("Gemini image identify: no candidates, response={}", response.body());
                return null;
            }
            String text = json.getAsJsonArray("candidates")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString();
            log.info("Gemini guess (raw text) = {}", text);

            try {
                JsonObject obj = JsonParser.parseString(text).getAsJsonObject();
                if (obj.has("common_name")) {
                    String v = obj.get("common_name").getAsString();
                    log.info("Gemini guess common_name = {}", v);
                    return v;
                }
                if (obj.has("scientific_name")) {
                    String v = obj.get("scientific_name").getAsString();
                    log.info("Gemini guess scientific_name = {}", v);
                    return v;
                }
            } catch (Exception e) {
                // nếu không parse được JSON thì trả nguyên text
            }
            return text;
        } catch (Exception e) {
            log.error("Gemini image identify error", e);
            return null;
        }
    }

    // dùng khi chỉ muốn hỏi bằng ảnh, không qua DB
    private String answerImageOnly(String userMessage, String imageUrl, String history) {
        try {
            if (userMessage == null || userMessage.isBlank()) {
                userMessage = "Hãy cho tôi biết đây là cây gì trong ảnh, tên thường gọi và tên khoa học (nếu có).";
            }

            String prompt = """
                Bạn là chuyên gia thực vật. Trả lời bằng tiếng Việt, ngắn gọn, thân thiện, dạng HTML.
                Hãy nhìn vào hình ảnh kèm theo và cho biết đây là cây gì, tên thường gọi và tên khoa học (nếu biết),
                kèm mô tả ngắn về đặc điểm nhận dạng chính.

                Lịch sử chat trước đó (nếu có):
                %s

                Câu hỏi của người dùng: %s
                """.formatted(history, userMessage);

            JsonObject root = new JsonObject();
            JsonArray contents = new JsonArray();
            JsonObject userMsg = new JsonObject();
            userMsg.addProperty("role", "user");

            JsonArray parts = new JsonArray();
            JsonObject partText = new JsonObject();
            partText.addProperty("text", prompt);
            parts.add(partText);

            JsonObject imagePart = buildImagePart(imageUrl);
            if (imagePart != null) {
                parts.add(imagePart);
            }

            userMsg.add("parts", parts);
            contents.add(userMsg);

            JsonObject generationConfig = new JsonObject();
            generationConfig.addProperty("temperature", 0.5);
            root.add("generationConfig", generationConfig);
            root.add("contents", contents);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GEMINI_URL + "?key=" + geminiApiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(root.toString()))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

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
            log.error("answerImageOnly error", e);
            return "❌ Lỗi hệ thống khi xử lý ảnh: " + e.getMessage();
        }
    }
}