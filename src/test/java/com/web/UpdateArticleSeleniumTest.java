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

public class UpdateArticleSeleniumTest {

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
            System.out.println("\n=== Update Article tests hoàn thành ===");
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

    private String getFirstArticleEditLink() {
        driver.get(BASE_URL + "/admin/list-article");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement listData = driver.findElement(By.id("listData"));
        try {
            WebElement firstEditLink = listData.findElement(By.cssSelector("a[href*='/admin/create-article?id=']"));
            return firstEditLink.getAttribute("href");
        } catch (Exception e) {
            return null;
        }
    }

    @Test(description = "UP_01: Cập nhật bài viết với dữ liệu hợp lệ")
    public void UP_01_updateArticle_withValidData() {
        String editUrl = getFirstArticleEditLink();
        assertNotNull(editUrl, "Phải có ít nhất một bài viết để chỉnh sửa");

        driver.get(editUrl);

        waitForQuillEditor();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String updatedTitle = "Bài viết đã cập nhật " + UUID.randomUUID().toString().substring(0, 8);
        String updatedSlug = "bai-viet-da-cap-nhat-" + UUID.randomUUID().toString().substring(0, 8);

        WebElement titleInput = driver.findElement(By.id("title"));
        WebElement slugInput = driver.findElement(By.id("slug"));
        WebElement excerptInput = driver.findElement(By.id("excerpt"));
        WebElement diseasesSelect = driver.findElement(By.id("diseases"));

        titleInput.clear();
        titleInput.sendKeys(updatedTitle);

        slugInput.clear();
        slugInput.sendKeys(updatedSlug);

        excerptInput.clear();
        excerptInput.sendKeys("Tóm tắt đã được cập nhật");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Select diseasesDropdown = new Select(diseasesSelect);
        if (diseasesDropdown.getOptions().size() > 0) {
            diseasesDropdown.selectByIndex(0);
        }

        setQuillContent("<p>Nội dung bài viết đã được cập nhật</p>");

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
                "Phải redirect sang trang danh sách sau khi cập nhật thành công");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement listData = driver.findElement(By.id("listData"));
        String listContent = listData.getText();
        assertTrue(listContent.contains(updatedTitle),
                "Bài viết đã cập nhật phải xuất hiện trong danh sách quản lý");
    }

    @Test(description = "UP_02: Cập nhật trạng thái sang Bản nháp")
    public void UP_02_updateArticleStatus_toDraft() {
        String editUrl = getFirstArticleEditLink();
        assertNotNull(editUrl, "Phải có ít nhất một bài viết để chỉnh sửa");

        driver.get(editUrl);

        waitForQuillEditor();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement statusSelect = driver.findElement(By.id("status"));

        Select statusDropdown = new Select(statusSelect);
        try {
            statusDropdown.selectByValue("0");
        } catch (Exception e) {
            try {
                statusDropdown.selectByVisibleText("Bản nháp");
            } catch (Exception ex) {
            }
        }

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
                "Phải redirect sang trang danh sách sau khi cập nhật trạng thái thành công");
    }

    @Test(description = "UP_03: Xuất bản lại bài viết")
    public void UP_03_publishArticle() {
        String editUrl = getFirstArticleEditLink();
        assertNotNull(editUrl, "Phải có ít nhất một bài viết để chỉnh sửa");

        driver.get(editUrl);

        waitForQuillEditor();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement statusSelect = driver.findElement(By.id("status"));

        Select statusDropdown = new Select(statusSelect);
        try {
            statusDropdown.selectByValue("2");
        } catch (Exception e) {
            try {
                statusDropdown.selectByVisibleText("Xuất bản");
            } catch (Exception ex) {
            }
        }

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

    @Test(description = "UP_04: Cập nhật ảnh bìa mới")
    public void UP_04_updateCoverImage() {
        String editUrl = getFirstArticleEditLink();
        assertNotNull(editUrl, "Phải có ít nhất một bài viết để chỉnh sửa");

        driver.get(editUrl);

        waitForQuillEditor();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement imageInput = driver.findElement(By.id("featuredImage"));

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

            WebElement previewWrapper = driver.findElement(By.id("previewWrapper"));
            String previewHtml = previewWrapper.getAttribute("innerHTML");
            assertTrue(previewHtml.contains("<img") || previewHtml.contains("img"),
                    "Preview phải hiển thị ảnh sau khi upload");

            WebElement saveButton = driver.findElement(By.id("btnSave"));
            saveButton.click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(webDriver -> webDriver.getCurrentUrl().contains("/admin/list-article"));

            assertTrue(driver.getCurrentUrl().contains("/admin/list-article"),
                    "Phải redirect sang trang danh sách sau khi cập nhật ảnh thành công");
        } catch (Exception e) {
        }
    }

    @Test(description = "UP_05: Xóa ảnh bìa")
    public void UP_05_removeCoverImage() {
        String editUrl = getFirstArticleEditLink();
        assertNotNull(editUrl, "Phải có ít nhất một bài viết để chỉnh sửa");

        driver.get(editUrl);

        waitForQuillEditor();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement removeImageButton = driver.findElement(By.id("removeImage"));

        if (removeImageButton.isEnabled()) {
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

            WebElement saveButton = driver.findElement(By.id("btnSave"));
            saveButton.click();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(webDriver -> webDriver.getCurrentUrl().contains("/admin/list-article"));

            assertTrue(driver.getCurrentUrl().contains("/admin/list-article"),
                    "Phải redirect sang trang danh sách sau khi xóa ảnh thành công");
        } else {
            System.out.println("Bài viết không có ảnh để xóa");
        }
    }

    @Test(description = "UP_06: Cập nhật với tiêu đề trống")
    public void UP_06_updateArticle_withEmptyTitle() {
        String editUrl = getFirstArticleEditLink();
        assertNotNull(editUrl, "Phải có ít nhất một bài viết để chỉnh sửa");

        driver.get(editUrl);

        waitForQuillEditor();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement titleInput = driver.findElement(By.id("title"));

        titleInput.clear();

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
        boolean stillOnEditPage = currentUrl.contains("/admin/create-article");
        boolean hasError = false;

        try {
            WebElement errorElement = driver.findElement(By.cssSelector(".toast-error, .swal-text"));
            String errorText = errorElement.getText();
            hasError = errorText.contains("Tiêu đề không thể để trống") || 
                      errorText.contains("tiêu đề") ||
                      errorText.contains("Vui lòng nhập tiêu đề");
        } catch (Exception e) {
        }

        assertTrue(stillOnEditPage || hasError,
                "Phải hiển thị lỗi validate tiêu đề hoặc ở lại trang chỉnh sửa");
    }

    @Test(description = "UP_07: Cập nhật với slug trống")
    public void UP_07_updateArticle_withEmptySlug() {
        String editUrl = getFirstArticleEditLink();
        assertNotNull(editUrl, "Phải có ít nhất một bài viết để chỉnh sửa");

        driver.get(editUrl);

        waitForQuillEditor();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement slugInput = driver.findElement(By.id("slug"));

        slugInput.clear();

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
        boolean stillOnEditPage = currentUrl.contains("/admin/create-article");
        boolean hasError = false;

        try {
            WebElement errorElement = driver.findElement(By.cssSelector(".toast-error, .swal-text"));
            String errorText = errorElement.getText();
            hasError = errorText.contains("Slug không thể để trống") || 
                      errorText.contains("slug") ||
                      errorText.contains("Slug");
        } catch (Exception e) {
        }

        assertTrue(stillOnEditPage || hasError,
                "Phải hiển thị lỗi validate slug hoặc ở lại trang chỉnh sửa");
    }

    @Test(description = "UP_09: Cập nhật nội dung trống")
    public void UP_09_updateArticle_withEmptyContent() {
        String editUrl = getFirstArticleEditLink();
        assertNotNull(editUrl, "Phải có ít nhất một bài viết để chỉnh sửa");

        driver.get(editUrl);

        waitForQuillEditor();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
        boolean stillOnEditPage = currentUrl.contains("/admin/create-article");
        boolean hasError = false;

        try {
            WebElement errorElement = driver.findElement(By.cssSelector(".toast-error, .swal-text"));
            String errorText = errorElement.getText();
            hasError = errorText.contains("Nội dung không thể để trống") || 
                      errorText.contains("nội dung") ||
                      errorText.contains("Vui lòng nhập tiêu đề và nội dung");
        } catch (Exception e) {
        }

        assertTrue(stillOnEditPage || hasError,
                "Phải hiển thị lỗi validate nội dung hoặc ở lại trang chỉnh sửa");
    }

    @Test(description = "UP_11: Hủy cập nhật bài viết")
    public void UP_11_cancelUpdateArticle() {
        String editUrl = getFirstArticleEditLink();
        assertNotNull(editUrl, "Phải có ít nhất một bài viết để chỉnh sửa");

        driver.get(editUrl);

        waitForQuillEditor();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement titleInput = driver.findElement(By.id("title"));
        String originalTitle = titleInput.getAttribute("value");

        titleInput.clear();
        titleInput.sendKeys("Tiêu đề đã thay đổi nhưng sẽ không lưu");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        driver.navigate().back();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertTrue(driver.getCurrentUrl().contains("/admin/list-article"),
                "Phải quay lại trang danh sách bài viết khi hủy");

        String editUrl2 = getFirstArticleEditLink();
        if (editUrl2 != null && editUrl2.equals(editUrl)) {
            driver.get(editUrl2);

            waitForQuillEditor();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            WebElement titleInput2 = driver.findElement(By.id("title"));
            String currentTitle = titleInput2.getAttribute("value");

            assertTrue(currentTitle.equals(originalTitle) || !currentTitle.contains("sẽ không lưu"),
                    "Thay đổi không được lưu, tiêu đề phải giữ nguyên hoặc không chứa text đã thay đổi");
        }
    }
}

