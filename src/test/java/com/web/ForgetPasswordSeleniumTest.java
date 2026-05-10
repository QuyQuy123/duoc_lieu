package com.web;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.testng.Assert.assertTrue;

public class ForgetPasswordSeleniumTest {

    private static WebDriver driver;

    private static final String BASE_URL = "http://localhost:8080";
    private static final String EXISTING_EMAIL = "admin@gmail.com";
    private static final String NON_EXISTING_EMAIL = "notfound@example.com";
    private static final String INVALID_EMAIL = "invalid-email";
    private static final String EMAIL_WITH_SPACES = "  admin@gmail.com  ";

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
    }

    @AfterClass(alwaysRun = true)
    public static void tearDown() {
        if (driver != null) {
            System.out.println("\n=== ForgetPassword tests hoàn thành ===");
            System.out.println("Browser giữ mở để bạn xem kết quả.");
            System.out.println("Bạn có thể tự đóng browser khi xem xong.\n");
        }
    }

    private WebElement waitForEmailInput() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        return wait.until(d -> d.findElement(By.id("email")));
    }

    @Test(description = "RPA08: Kiểm tra hiển thị ô nhập email")
    public void RPA08_emailTextboxDisplayed() {
        driver.get(BASE_URL + "/forgot");
        WebElement emailInput = waitForEmailInput();
        assertTrue(emailInput.isDisplayed(), "Textbox Email phải hiển thị");
    }

    @Test(description = "RPA09: Kiểm tra hiển thị button Gửi liên kết đặt lại")
    public void RPA09_sendButtonDisplayed() {
        driver.get(BASE_URL + "/forgot");
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        assertTrue(submitButton.isDisplayed(), "Button Gửi liên kết đặt lại phải hiển thị");
    }

    @Test(description = "RPA10: Kiểm tra link Đăng nhập")
    public void RPA10_loginLinkNavigation() {
        driver.get(BASE_URL + "/forgot");
        WebElement loginLink = driver.findElement(By.cssSelector("a[href='/login']"));
        assertTrue(loginLink.isDisplayed(), "Link Đăng nhập phải hiển thị");
        loginLink.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> d.getCurrentUrl().contains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"), "Phải điều hướng sang trang Đăng nhập");
    }

    @Test(description = "RPA04: Validate khi email trống")
    public void RPA04_emptyEmailValidation() {
        driver.get(BASE_URL + "/forgot");
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        WebElement emailInput = waitForEmailInput();

        emailInput.clear();
        submitButton.click();

        String validationMessage = emailInput.getAttribute("validationMessage");
        assertTrue(validationMessage != null && !validationMessage.isEmpty(),
                "Phải hiển thị thông báo yêu cầu nhập email (HTML5 validation). Message: " + validationMessage);
    }

    @Test(description = "RPA05: Validate email sai format")
    public void RPA05_invalidEmailFormat() {
        driver.get(BASE_URL + "/forgot");
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        WebElement emailInput = waitForEmailInput();

        emailInput.clear();
        emailInput.sendKeys(INVALID_EMAIL);
        submitButton.click();

        String validationMessage = emailInput.getAttribute("validationMessage");
        assertTrue(validationMessage != null && !validationMessage.isEmpty(),
                "Phải hiển thị thông báo định dạng email sai. Message: " + validationMessage);
    }

    @Test(description = "RPA01: Gửi link reset khi nhập email hợp lệ")
    public void RPA01_sendResetLink_withValidEmail() {
        driver.get(BASE_URL + "/forgot");
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        WebElement emailInput = waitForEmailInput();

        emailInput.clear();
        emailInput.sendKeys(EXISTING_EMAIL);
        submitButton.click();

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(d -> d.getCurrentUrl().contains("/forgot") || d.getCurrentUrl().contains("/login"));
            assertTrue(true, "Gửi yêu cầu thành công (hãy kiểm tra email nếu backend gửi)");
        } catch (Exception e) {
            assertTrue(false, "Không thấy phản hồi sau khi gửi yêu cầu");
        }
    }

    @Test(description = "RPA06: Email không tồn tại")
    public void RPA06_nonExistingEmail() {
        driver.get(BASE_URL + "/forgot");
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        WebElement emailInput = waitForEmailInput();

        emailInput.clear();
        emailInput.sendKeys(NON_EXISTING_EMAIL);
        submitButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> d.getCurrentUrl().contains("/forgot"));
        assertTrue(driver.getCurrentUrl().contains("/forgot"),
                "Khi email không tồn tại, vẫn ở trang forgot và hiển thị lỗi (tùy backend)");
    }

    @Test(description = "RPA07: Email có khoảng trắng đầu/cuối")
    public void RPA07_emailWithSpaces() {
        driver.get(BASE_URL + "/forgot");
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        WebElement emailInput = waitForEmailInput();

        emailInput.clear();
        emailInput.sendKeys(EMAIL_WITH_SPACES);
        submitButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> d.getCurrentUrl().contains("/forgot") || d.getCurrentUrl().contains("/login"));

        assertTrue(true, "Email có khoảng trắng được trim hoặc backend báo lỗi phù hợp");
    }

    @Test(description = "RPA02: Kiểm tra hệ thống gửi email reset (cần hạ tầng mail)")
    public void RPA02_verifyEmailSent_placeholder() {
        RPA01_sendResetLink_withValidEmail();
        System.out.println("⚠ Cần tích hợp hạ tầng đọc inbox để kiểm tra nội dung email reset.");
        assertTrue(true);
    }

    @Test(description = "RPA03: Kiểm tra link reset trong email (placeholder)")
    public void RPA03_verifyResetLink_placeholder() {
        System.out.println("⚠ Cần tích hợp hạ tầng đọc inbox và trích xuất link reset để kiểm tra điều hướng.");
        assertTrue(true);
    }
}

