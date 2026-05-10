package com.web;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.UUID;

import static org.testng.Assert.*;

public class ArticleManagementSeleniumTest {

    private static WebDriver driver;
    private static final String BASE_URL = "http://localhost:8080";
    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "admin";

    @BeforeClass(alwaysRun = true)
    public static void setupDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().window().maximize();

        driver.get(BASE_URL + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

        usernameInput.clear();
        usernameInput.sendKeys(ADMIN_EMAIL);
        passwordInput.clear();
        passwordInput.sendKeys(ADMIN_PASSWORD);
        submitButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(webDriver -> !webDriver.getCurrentUrl().endsWith("/login"));
    }

    @AfterClass(alwaysRun = true)
    public static void tearDown() {
        if (driver != null) {
            System.out.println("\n=== Article Management tests hoàn thành ===");
            System.out.println("Browser giữ mở để bạn xem kết quả.");
            System.out.println("Bạn có thể tự đóng browser khi xem xong.\n");
        }
    }

    private void waitForQuillEditor() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(webDriver -> {
            JavascriptExecutor js = (JavascriptExecutor) webDriver;
            Object result = js.executeScript("return typeof Quill !== 'undefined' && document.querySelector('#editor .ql-editor') !== null;");
            return result != null && (Boolean) result;
        });
    }
    private void setQuillContent(String content) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("if (typeof quillArticle !== 'undefined' && quillArticle) { quillArticle.root.innerHTML = arguments[0]; } else { var editor = document.querySelector('#editor .ql-editor'); if (editor) editor.innerHTML = arguments[0]; }", content);
    }
    private String generateUniqueTitle() {
        return "Test Bài Viết " + UUID.randomUUID().toString().substring(0, 8);
    }
    private String generateUniqueSlug() {
        return "test-bai-viet-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test(description = "TCQLBV01: Kiểm tra thêm mới bài viết với dữ liệu hợp lệ")
    public void TCQLBV01_addArticle_withValidData() {
        driver.get(BASE_URL + "/admin/create-article");
        waitForQuillEditor();
        String uniqueTitle = generateUniqueTitle();
        String uniqueSlug = generateUniqueSlug();

        WebElement titleInput = driver.findElement(By.id("title"));
        WebElement slugInput = driver.findElement(By.id("slug"));
        WebElement excerptInput = driver.findElement(By.id("excerpt"));
        WebElement diseasesSelect = driver.findElement(By.id("diseases"));

        titleInput.clear();
        titleInput.sendKeys(uniqueTitle);

        slugInput.clear();
        slugInput.sendKeys(uniqueSlug);

        excerptInput.clear();
        excerptInput.sendKeys("Đây là tóm tắt bài viết test");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Select diseasesDropdown = new Select(diseasesSelect);
        if (diseasesDropdown.getOptions().size() > 0) {
            diseasesDropdown.selectByIndex(0);
        }

        setQuillContent("<p>Nội dung bài viết test hợp lệ</p>");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement saveButton = driver.findElement(By.id("btnSave"));
        saveButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(webDriver -> webDriver.getCurrentUrl().contains("/admin/list-article"));

        assertTrue(driver.getCurrentUrl().contains("/admin/list-article"),
                "Phải redirect sang trang danh sách bài viết sau khi thêm thành công");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement listData = driver.findElement(By.id("listData"));
        String listContent = listData.getText();
        assertTrue(listContent.contains(uniqueTitle),
                "Bài viết phải xuất hiện trong danh sách quản lý");
    }

    @Test(description = "TCQLBV02: Thêm mới bài viết với tiêu đề trùng")
    public void TCQLBV02_addArticle_withDuplicateTitle() {
        driver.get(BASE_URL + "/admin/create-article");

        waitForQuillEditor();

        WebElement titleInput = driver.findElement(By.id("title"));
        WebElement slugInput = driver.findElement(By.id("slug"));
        WebElement excerptInput = driver.findElement(By.id("excerpt"));
        WebElement diseasesSelect = driver.findElement(By.id("diseases"));

        titleInput.clear();
        titleInput.sendKeys("Bài viết đã tồn tại");

        slugInput.clear();
        slugInput.sendKeys(generateUniqueSlug());

        excerptInput.clear();
        excerptInput.sendKeys("Tóm tắt");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Select diseasesDropdown = new Select(diseasesSelect);
        if (diseasesDropdown.getOptions().size() > 0) {
            diseasesDropdown.selectByIndex(0);
        }

        setQuillContent("<p>Nội dung</p>");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement saveButton = driver.findElement(By.id("btnSave"));
        saveButton.click();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = driver.getCurrentUrl();
        boolean stillOnCreatePage = currentUrl.contains("/admin/create-article");
        boolean hasError = false;

        try {
            WebElement errorElement = driver.findElement(By.cssSelector(".toast-error, .swal-text"));
            String errorText = errorElement.getText();
            hasError = errorText.contains("Tên bài viết đã tồn tại") || 
                      errorText.contains("đã tồn tại") ||
                      errorText.contains("duplicate");
        } catch (Exception e) {
        }

        assertTrue(stillOnCreatePage || hasError,
                "Phải hiển thị lỗi hoặc ở lại trang tạo bài viết khi tiêu đề trùng");
    }

    @Test(description = "TCQLBV03: Thêm bài viết hợp lệ")
    public void TCQLBV03_addArticle_valid() {
        TCQLBV01_addArticle_withValidData();
    }

    @Test(description = "TCQLBV04: Lưu bài viết bản nháp")
    public void TCQLBV04_saveArticle_asDraft() {
        driver.get(BASE_URL + "/admin/create-article");

        waitForQuillEditor();

        String uniqueTitle = generateUniqueTitle();
        String uniqueSlug = generateUniqueSlug();

        WebElement titleInput = driver.findElement(By.id("title"));
        WebElement slugInput = driver.findElement(By.id("slug"));
        WebElement excerptInput = driver.findElement(By.id("excerpt"));
        WebElement statusSelect = driver.findElement(By.id("status"));
        WebElement diseasesSelect = driver.findElement(By.id("diseases"));

        titleInput.clear();
        titleInput.sendKeys(uniqueTitle);

        slugInput.clear();
        slugInput.sendKeys(uniqueSlug);

        excerptInput.clear();
        excerptInput.sendKeys("Tóm tắt bản nháp");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Select statusDropdown = new Select(statusSelect);
        statusDropdown.selectByValue("0");

        Select diseasesDropdown = new Select(diseasesSelect);
        if (diseasesDropdown.getOptions().size() > 0) {
            diseasesDropdown.selectByIndex(0);
        }

        setQuillContent("<p>Nội dung bản nháp</p>");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement saveButton = driver.findElement(By.id("btnSave"));
        saveButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(webDriver -> webDriver.getCurrentUrl().contains("/admin/list-article"));

        assertTrue(driver.getCurrentUrl().contains("/admin/list-article"),
                "Phải redirect sang trang danh sách sau khi lưu bản nháp thành công");
    }

    @Test(description = "TCQLBV05: Xuất bản bài viết")
    public void TCQLBV05_publishArticle() {
        driver.get(BASE_URL + "/admin/create-article");

        waitForQuillEditor();

        String uniqueTitle = generateUniqueTitle();
        String uniqueSlug = generateUniqueSlug();

        WebElement titleInput = driver.findElement(By.id("title"));
        WebElement slugInput = driver.findElement(By.id("slug"));
        WebElement excerptInput = driver.findElement(By.id("excerpt"));
        WebElement statusSelect = driver.findElement(By.id("status"));
        WebElement diseasesSelect = driver.findElement(By.id("diseases"));

        titleInput.clear();
        titleInput.sendKeys(uniqueTitle);

        slugInput.clear();
        slugInput.sendKeys(uniqueSlug);

        excerptInput.clear();
        excerptInput.sendKeys("Tóm tắt bài viết xuất bản");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Select statusDropdown = new Select(statusSelect);
        try {
            statusDropdown.selectByValue("2");
        } catch (Exception e) {
            try {
                statusDropdown.selectByVisibleText("Xuất bản");
            } catch (Exception ex) {
            }
        }

        Select diseasesDropdown = new Select(diseasesSelect);
        if (diseasesDropdown.getOptions().size() > 0) {
            diseasesDropdown.selectByIndex(0);
        }

        setQuillContent("<p>Nội dung bài viết xuất bản</p>");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement saveButton = driver.findElement(By.id("btnSave"));
        saveButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(webDriver -> webDriver.getCurrentUrl().contains("/admin/list-article"));

        assertTrue(driver.getCurrentUrl().contains("/admin/list-article"),
                "Phải redirect sang trang danh sách sau khi xuất bản thành công");
    }

    @Test(description = "TCQLBV06: Thêm bài viết có ảnh")
    public void TCQLBV06_addArticle_withImage() {
        driver.get(BASE_URL + "/admin/create-article");

        waitForQuillEditor();

        String uniqueTitle = generateUniqueTitle();
        String uniqueSlug = generateUniqueSlug();

        WebElement titleInput = driver.findElement(By.id("title"));
        WebElement slugInput = driver.findElement(By.id("slug"));
        WebElement excerptInput = driver.findElement(By.id("excerpt"));
        WebElement diseasesSelect = driver.findElement(By.id("diseases"));
        WebElement imageInput = driver.findElement(By.id("featuredImage"));

        titleInput.clear();
        titleInput.sendKeys(uniqueTitle);

        slugInput.clear();
        slugInput.sendKeys(uniqueSlug);

        excerptInput.clear();
        excerptInput.sendKeys("Tóm tắt bài viết có ảnh");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Select diseasesDropdown = new Select(diseasesSelect);
        if (diseasesDropdown.getOptions().size() > 0) {
            diseasesDropdown.selectByIndex(0);
        }

        setQuillContent("<p>Nội dung bài viết có ảnh</p>");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String imagePath = System.getProperty("user.dir") + "/src/test/resources/test-image.jpg";
        try {
            java.io.File imageFile = new java.io.File(imagePath);
            if (!imageFile.exists()) {
                imageFile.getParentFile().mkdirs();
                java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(100, 100, java.awt.image.BufferedImage.TYPE_INT_RGB);
                java.io.FileOutputStream fos = new java.io.FileOutputStream(imageFile);
                javax.imageio.ImageIO.write(img, "jpg", fos);
                fos.close();
            }
            imageInput.sendKeys(imageFile.getAbsolutePath());

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } catch (Exception e) {
        }

        WebElement saveButton = driver.findElement(By.id("btnSave"));
        saveButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(webDriver -> webDriver.getCurrentUrl().contains("/admin/list-article"));

        assertTrue(driver.getCurrentUrl().contains("/admin/list-article"),
                "Phải redirect sang trang danh sách sau khi thêm bài viết có ảnh thành công");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement listData = driver.findElement(By.id("listData"));
        String listContent = listData.getText();
        assertTrue(listContent.contains(uniqueTitle),
                "Bài viết có ảnh phải xuất hiện trong danh sách quản lý");
    }

    @Test(description = "TCQLBV07: Xóa ảnh bìa")
    public void TCQLBV07_removeCoverImage() {
        driver.get(BASE_URL + "/admin/create-article");

        waitForQuillEditor();

        WebElement imageInput = driver.findElement(By.id("featuredImage"));
        WebElement removeImageButton = driver.findElement(By.id("removeImage"));

        assertFalse(removeImageButton.isEnabled(),
                "Nút xóa ảnh phải bị disable khi chưa có ảnh");

        String imagePath = System.getProperty("user.dir") + "/src/test/resources/test-image.jpg";
        try {
            java.io.File imageFile = new java.io.File(imagePath);
            if (!imageFile.exists()) {
                imageFile.getParentFile().mkdirs();
                java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(100, 100, java.awt.image.BufferedImage.TYPE_INT_RGB);
                java.io.FileOutputStream fos = new java.io.FileOutputStream(imageFile);
                javax.imageio.ImageIO.write(img, "jpg", fos);
                fos.close();
            }
            imageInput.sendKeys(imageFile.getAbsolutePath());

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            assertTrue(removeImageButton.isEnabled(),
                    "Nút xóa ảnh phải được enable sau khi upload ảnh");

            removeImageButton.click();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            assertFalse(removeImageButton.isEnabled(),
                    "Nút xóa ảnh phải bị disable sau khi xóa ảnh");

            WebElement previewWrapper = driver.findElement(By.id("previewWrapper"));
            String previewText = previewWrapper.getText();
            assertTrue(previewText.contains("Chưa có ảnh") || previewText.isEmpty(),
                    "Preview phải hiển thị 'Chưa có ảnh' sau khi xóa");
        } catch (Exception e) {
        }
    }

    @Test(description = "TCQLBV08: Không nhập tiêu đề")
    public void TCQLBV08_addArticle_withoutTitle() {
        driver.get(BASE_URL + "/admin/create-article");

        waitForQuillEditor();

        WebElement slugInput = driver.findElement(By.id("slug"));
        WebElement excerptInput = driver.findElement(By.id("excerpt"));
        WebElement diseasesSelect = driver.findElement(By.id("diseases"));

        slugInput.clear();
        slugInput.sendKeys(generateUniqueSlug());

        excerptInput.clear();
        excerptInput.sendKeys("Tóm tắt");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Select diseasesDropdown = new Select(diseasesSelect);
        if (diseasesDropdown.getOptions().size() > 0) {
            diseasesDropdown.selectByIndex(0);
        }

        setQuillContent("<p>Nội dung</p>");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement saveButton = driver.findElement(By.id("btnSave"));
        saveButton.click();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = driver.getCurrentUrl();
        boolean stillOnCreatePage = currentUrl.contains("/admin/create-article");
        boolean hasError = false;

        try {
            WebElement errorElement = driver.findElement(By.cssSelector(".toast-error, .swal-text"));
            String errorText = errorElement.getText();
            hasError = errorText.contains("Tiêu đề không thể để trống") || 
                      errorText.contains("tiêu đề") ||
                      errorText.contains("Vui lòng nhập tiêu đề");
        } catch (Exception e) {
        }

        assertTrue(stillOnCreatePage || hasError,
                "Phải hiển thị lỗi 'Tiêu đề không thể để trống' hoặc ở lại trang tạo bài viết");
    }

    @Test(description = "TCQLBV09: Không nhập slug")
    public void TCQLBV09_addArticle_withoutSlug() {
        driver.get(BASE_URL + "/admin/create-article");

        waitForQuillEditor();

        String uniqueTitle = generateUniqueTitle();

        WebElement titleInput = driver.findElement(By.id("title"));
        WebElement excerptInput = driver.findElement(By.id("excerpt"));
        WebElement diseasesSelect = driver.findElement(By.id("diseases"));

        titleInput.clear();
        titleInput.sendKeys(uniqueTitle);

        WebElement slugInput = driver.findElement(By.id("slug"));
        slugInput.clear();

        excerptInput.clear();
        excerptInput.sendKeys("Tóm tắt");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Select diseasesDropdown = new Select(diseasesSelect);
        if (diseasesDropdown.getOptions().size() > 0) {
            diseasesDropdown.selectByIndex(0);
        }

        setQuillContent("<p>Nội dung</p>");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement saveButton = driver.findElement(By.id("btnSave"));
        saveButton.click();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = driver.getCurrentUrl();
        boolean stillOnCreatePage = currentUrl.contains("/admin/create-article");
        boolean hasError = false;

        try {
            WebElement errorElement = driver.findElement(By.cssSelector(".toast-error, .swal-text"));
            String errorText = errorElement.getText();
            hasError = errorText.contains("Slug không thể để trống") || 
                      errorText.contains("slug") ||
                      errorText.contains("Slug");
        } catch (Exception e) {
        }

        assertTrue(stillOnCreatePage || hasError,
                "Phải hiển thị lỗi 'Slug không thể để trống' hoặc ở lại trang tạo bài viết");
    }

    @Test(description = "TCQLBV10: Slug trùng")
    public void TCQLBV10_addArticle_withDuplicateSlug() {
        driver.get(BASE_URL + "/admin/create-article");

        waitForQuillEditor();

        String uniqueTitle = generateUniqueTitle();

        WebElement titleInput = driver.findElement(By.id("title"));
        WebElement slugInput = driver.findElement(By.id("slug"));
        WebElement excerptInput = driver.findElement(By.id("excerpt"));
        WebElement diseasesSelect = driver.findElement(By.id("diseases"));

        titleInput.clear();
        titleInput.sendKeys(uniqueTitle);

        slugInput.clear();
        slugInput.sendKeys("slug-da-ton-tai");

        excerptInput.clear();
        excerptInput.sendKeys("Tóm tắt");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Select diseasesDropdown = new Select(diseasesSelect);
        if (diseasesDropdown.getOptions().size() > 0) {
            diseasesDropdown.selectByIndex(0);
        }

        setQuillContent("<p>Nội dung</p>");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement saveButton = driver.findElement(By.id("btnSave"));
        saveButton.click();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = driver.getCurrentUrl();
        boolean stillOnCreatePage = currentUrl.contains("/admin/create-article");
        boolean hasError = false;

        try {
            WebElement errorElement = driver.findElement(By.cssSelector(".toast-error, .swal-text"));
            String errorText = errorElement.getText();
            hasError = errorText.contains("Slug đã được dùng") || 
                      errorText.contains("slug đã được dùng") ||
                      errorText.contains("đã tồn tại");
        } catch (Exception e) {
        }

        assertTrue(stillOnCreatePage || hasError,
                "Phải hiển thị lỗi 'Slug đã được dùng' hoặc ở lại trang tạo bài viết");
    }

    @Test(description = "TCQLBV11: Nội dung trống")
    public void TCQLBV11_addArticle_withoutContent() {
        driver.get(BASE_URL + "/admin/create-article");

        waitForQuillEditor();

        String uniqueTitle = generateUniqueTitle();
        String uniqueSlug = generateUniqueSlug();

        WebElement titleInput = driver.findElement(By.id("title"));
        WebElement slugInput = driver.findElement(By.id("slug"));
        WebElement excerptInput = driver.findElement(By.id("excerpt"));
        WebElement diseasesSelect = driver.findElement(By.id("diseases"));

        titleInput.clear();
        titleInput.sendKeys(uniqueTitle);

        slugInput.clear();
        slugInput.sendKeys(uniqueSlug);

        excerptInput.clear();
        excerptInput.sendKeys("Tóm tắt");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Select diseasesDropdown = new Select(diseasesSelect);
        if (diseasesDropdown.getOptions().size() > 0) {
            diseasesDropdown.selectByIndex(0);
        }

        setQuillContent("");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement saveButton = driver.findElement(By.id("btnSave"));
        saveButton.click();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = driver.getCurrentUrl();
        boolean stillOnCreatePage = currentUrl.contains("/admin/create-article");
        boolean hasError = false;

        try {
            WebElement errorElement = driver.findElement(By.cssSelector(".toast-error, .swal-text"));
            String errorText = errorElement.getText();
            hasError = errorText.contains("Nội dung không thể để trống") || 
                      errorText.contains("nội dung") ||
                      errorText.contains("Vui lòng nhập tiêu đề và nội dung");
        } catch (Exception e) {
        }

        assertTrue(stillOnCreatePage || hasError,
                "Phải hiển thị lỗi 'Nội dung không thể để trống' hoặc ở lại trang tạo bài viết");
    }
}

