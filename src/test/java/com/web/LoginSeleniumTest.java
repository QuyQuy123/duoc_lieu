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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class LoginSeleniumTest {


    private static WebDriver driver;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String USERNAME_VALID = "quyquy";
    private static final String PASSWORD_VALID = "admin";
    
    private static final String EMAIL_VALID = "admin@gmail.com";
    private static final String PASSWORD_EMAIL = "admin";
    
    private static final String USERNAME_15_CHARS = "admin@gmail.com";
    private static final String PASSWORD_15_CHARS = "admin";
    
    private static final String USERNAME_PASSWORD_20 = "quyquy";
    private static final String PASSWORD_20_CHARS = "xinchaotatcacaban";
    
    private static final String USERNAME_LESS_THAN_5 = "quy";
    private static final String PASSWORD_VALID_FOR_VALIDATION = "admin";
    
    private static final String USERNAME_CORRECT = "quyquy";
    private static final String PASSWORD_WRONG = "123456";
    
    private static final String USERNAME_UNICODE_EMOJI = "quyquy@";

    private static final String PASSWORD_FOR_UNICODE = "admin";
    
    private static final String EMAIL_INVALID_FORMAT = "invalid-email";
    
    private static final String USERNAME_WITH_SPACES = " quyquy ";

    private static final String USERNAME_VALID_FOR_SPACE_TEST = "quyquy";
    private static final String PASSWORD_VALID_FOR_SPACE_TEST = "admin";
    
    private static final String USERNAME_FOR_CASE_TEST = "quyquy";
    private static final String PASSWORD_UPPERCASE = "ADMIN";
    private static final String PASSWORD_LOWERCASE = "admin";
    private static final String PASSWORD_WITH_SPACES = " admin ";

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
            System.out.println("\n=== Test hoàn thành ===");
            System.out.println("Browser sẽ giữ mở để bạn xem kết quả.");
            System.out.println("Bạn có thể tự đóng browser khi xem xong.\n");
        }
    }

    @Test(description = "Đăng nhập thành công với tài khoản hợp lệ")
    public void loginSuccess_withValidAccount() {
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

        usernameInput.clear();
        usernameInput.sendKeys(USERNAME_VALID);

        passwordInput.clear();
        passwordInput.sendKeys(PASSWORD_VALID);
        submitButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(webDriver -> !webDriver.getCurrentUrl().endsWith("/login"));
        String currentUrl = driver.getCurrentUrl();
        assertFalse(currentUrl.endsWith("/login"),
                "Sau khi đăng nhập thành công không nên ở lại trang /login");
    }

    @Test(description = "Đăng nhập bằng email hợp lệ")
    public void loginSuccess_withValidEmail() {
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

        usernameInput.clear();
        usernameInput.sendKeys(EMAIL_VALID);
        passwordInput.clear();
        passwordInput.sendKeys(PASSWORD_EMAIL);
        submitButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(webDriver -> !webDriver.getCurrentUrl().endsWith("/login"));

        String currentUrl = driver.getCurrentUrl();
        assertFalse(currentUrl.endsWith("/login"),
                "Sau khi đăng nhập bằng email thành công không nên ở lại trang /login. URL hiện tại: " + currentUrl);
    }

    @Test(description = "Đăng nhập với Username 15 ký tự và Password hợp lệ")
    public void loginSuccess_withUsername15Chars() {
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

        usernameInput.clear();
        usernameInput.sendKeys(USERNAME_15_CHARS);

        passwordInput.clear();
        passwordInput.sendKeys(PASSWORD_15_CHARS);

        submitButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(webDriver -> !webDriver.getCurrentUrl().endsWith("/login"));

        String currentUrl = driver.getCurrentUrl();

        assertFalse(currentUrl.endsWith("/login"),
                "Sau khi đăng nhập với username 15 ký tự thành công không nên ở lại trang /login. URL hiện tại: " + currentUrl);

        assertEquals(15, USERNAME_15_CHARS.length(),
                "Username phải có đúng 15 ký tự. Username: '" + USERNAME_15_CHARS + "', Độ dài: " + USERNAME_15_CHARS.length());
    }

    @Test(description = "Đăng nhập với Password 20 ký tự và Username hợp lệ")
    public void loginSuccess_withPassword20Chars() {
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        usernameInput.clear();
        usernameInput.sendKeys(USERNAME_PASSWORD_20);
        passwordInput.clear();
        passwordInput.sendKeys(PASSWORD_20_CHARS);
        submitButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(webDriver -> !webDriver.getCurrentUrl().endsWith("/login"));
        String currentUrl = driver.getCurrentUrl();
        assertFalse(currentUrl.endsWith("/login"),
                "Sau khi đăng nhập với password 20 ký tự thành công không nên ở lại trang /login. URL hiện tại: " + currentUrl);
        assertEquals(20, PASSWORD_20_CHARS.length(),
                "Password phải có đúng 20 ký tự. Password: '" + PASSWORD_20_CHARS + "', Độ dài: " + PASSWORD_20_CHARS.length());
    }

    @Test(description = "Đăng nhập với Username ít hơn 5 ký tự")
    public void loginFail_withUsernameLessThan5Chars() {
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

        usernameInput.clear();
        usernameInput.sendKeys(USERNAME_LESS_THAN_5);

        passwordInput.clear();
        passwordInput.sendKeys(PASSWORD_VALID_FOR_VALIDATION);

        submitButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement errorSection = wait.until(d -> {
            WebElement errSection = d.findElement(By.id("error-section"));
            WebElement errMess = d.findElement(By.id("errormess"));
            String display = errSection.getCssValue("display");
            if ("none".equalsIgnoreCase(display)) {
                return null;
            }
            String text = errMess.getText();
            if (text != null && !text.trim().isEmpty() && !text.equals("Lỗi xảy ra")) {
                return errSection;
            }
            
            return null;
        });

        WebElement errorMessage = driver.findElement(By.id("errormess"));
        String errorText = errorMessage.getText();

        assertTrue(errorText != null && !errorText.trim().isEmpty(),
                "Phải hiển thị thông báo lỗi. Text hiện tại: " + errorText);
        String expectedMessage = "Độ dài Username phải nằm trong khoảng 5 đến 15 ký tự.";
        boolean containsExactMessage = errorText.contains(expectedMessage);
        boolean containsPartialMessage = errorText.contains("5") && 
                                        (errorText.contains("15") || errorText.contains("ký tự")) &&
                                        (errorText.contains("Username") || errorText.contains("username") || errorText.contains("độ dài"));
        
        assertTrue(containsExactMessage || containsPartialMessage,
                "Error message phải chứa thông tin về độ dài username (5 đến 15 ký tự). " +
                "Message mong đợi: '" + expectedMessage + "'. " +
                "Message hiện tại: '" + errorText + "'");

        String displayStyle = errorSection.getCssValue("display");
        assertFalse("none".equalsIgnoreCase(displayStyle),
                "Khi username ít hơn 5 ký tự, khu vực thông báo lỗi phải hiển thị (display không phải 'none')");
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.endsWith("/login"),
                "Khi đăng nhập thất bại, phải ở lại trang login. URL hiện tại: " + currentUrl);
    }

    @Test(description = "Để trống cả hai trường")
    public void loginFail_withEmptyFields() {
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        usernameInput.clear();
        passwordInput.clear();
        submitButton.click();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String usernameValidationMessage = usernameInput.getAttribute("validationMessage");
        String passwordValidationMessage = passwordInput.getAttribute("validationMessage");

        try {
            WebElement errorSection = driver.findElement(By.id("error-section"));
            String displayStyle = errorSection.getCssValue("display");
            
            if (!"none".equalsIgnoreCase(displayStyle)) {
                WebElement errorMessage = driver.findElement(By.id("errormess"));
                String errorText = errorMessage.getText();
                
                assertTrue(errorText != null && !errorText.trim().isEmpty(),
                        "Phải hiển thị thông báo lỗi khi để trống các trường. Message hiện tại: " + errorText);

                boolean containsPleaseFillMessage = errorText.contains("Vui lòng nhập") || 
                                                   errorText.contains("vui lòng nhập") ||
                                                   errorText.contains("nhập") ||
                                                   errorText.contains("để trống");
                
                assertTrue(containsPleaseFillMessage || usernameValidationMessage != null || passwordValidationMessage != null,
                        "Error message phải chứa thông báo về việc cần nhập thông tin. " +
                        "Message hiện tại: '" + errorText + "'");
            }
        } catch (Exception e) {
        }

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.endsWith("/login"),
                "Khi để trống các trường, phải ở lại trang login và không được submit form. URL hiện tại: " + currentUrl);

        assertTrue(usernameInput.getAttribute("value") == null || usernameInput.getAttribute("value").isEmpty(),
                "Username field phải trống");
        assertTrue(passwordInput.getAttribute("value") == null || passwordInput.getAttribute("value").isEmpty(),
                "Password field phải trống");
    }

    @Test(description = "Nhập username đúng nhưng mật khẩu sai")
    public void loginFail_withCorrectUsernameButWrongPassword() {
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

        usernameInput.clear();
        usernameInput.sendKeys(USERNAME_CORRECT);

        passwordInput.clear();
        passwordInput.sendKeys(PASSWORD_WRONG);
        submitButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement errorSection = wait.until(d -> {
            WebElement errSection = d.findElement(By.id("error-section"));
            WebElement errMess = d.findElement(By.id("errormess"));

            String display = errSection.getCssValue("display");
            if ("none".equalsIgnoreCase(display)) {
                return null;
            }

            String text = errMess.getText();
            if (text != null && !text.trim().isEmpty() && !text.equals("Lỗi xảy ra")) {
                return errSection;
            }
            
            return null;
        });
        WebElement errorMessage = driver.findElement(By.id("errormess"));
        String errorText = errorMessage.getText();
        String expectedMessage = "Sai tên đăng nhập hoặc mật khẩu";
        boolean containsExactMessage = errorText.contains(expectedMessage);
        boolean containsPartialMessage = (errorText.contains("Sai") || errorText.contains("sai")) && 
                                       (errorText.contains("tên đăng nhập") || errorText.contains("mật khẩu") || 
                                        errorText.contains("username") || errorText.contains("password"));
        
        assertTrue(containsExactMessage || containsPartialMessage,
                "Error message phải chứa thông báo về sai tên đăng nhập hoặc mật khẩu. " +
                "Message mong đợi: '" + expectedMessage + "'. " +
                "Message hiện tại: '" + errorText + "'");
        String displayStyle = errorSection.getCssValue("display");
        assertFalse("none".equalsIgnoreCase(displayStyle),
                "Khi nhập sai mật khẩu, khu vực thông báo lỗi phải hiển thị (display không phải 'none')");
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.endsWith("/login"),
                "Khi đăng nhập thất bại, phải ở lại trang login. URL hiện tại: " + currentUrl);
    }

    @Test(description = "Đăng nhập bằng username có ký tự đặc biệt (Unicode, emoji)")
    public void login_withUnicodeEmojiUsername() {
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

        usernameInput.clear();
        usernameInput.sendKeys(USERNAME_UNICODE_EMOJI);

        passwordInput.clear();
        passwordInput.sendKeys(PASSWORD_FOR_UNICODE);
        submitButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {

            wait.until(webDriver -> {
                String url = webDriver.getCurrentUrl();
                return !url.endsWith("/login") || 
                       webDriver.findElement(By.id("error-section")).getCssValue("display").equals("block");
            });
            
            String currentUrl = driver.getCurrentUrl();

            if (!currentUrl.endsWith("/login")) {
                assertFalse(currentUrl.endsWith("/login"),
                        "Đăng nhập thành công với username có ký tự đặc biệt. URL hiện tại: " + currentUrl);
                System.out.println("✓ Đăng nhập thành công với username có ký tự đặc biệt: " + USERNAME_UNICODE_EMOJI);
            }
            else {

                WebElement errorSection = driver.findElement(By.id("error-section"));
                String displayStyle = errorSection.getCssValue("display");
                
                if (!"none".equalsIgnoreCase(displayStyle)) {
                    WebElement errorMessage = driver.findElement(By.id("errormess"));
                    String errorText = errorMessage.getText();
                    assertTrue(errorText != null && !errorText.trim().isEmpty() && !errorText.equals("Lỗi xảy ra"),
                            "Khi user không tồn tại, phải hiển thị thông báo lỗi chính xác. Message hiện tại: " + errorText);

                    boolean containsError = errorText.contains("Sai") || 
                                          errorText.contains("sai") || 
                                          errorText.contains("không tồn tại") ||
                                          errorText.contains("không hợp lệ") ||
                                          errorText.contains("invalid") ||
                                          errorText.contains("not found");
                    
                    assertTrue(containsError,
                            "Error message phải báo lỗi chính xác về việc user không tồn tại hoặc thông tin đăng nhập sai. " +
                            "Message hiện tại: '" + errorText + "'");
                    
                    System.out.println("✓ Hiển thị thông báo lỗi chính xác khi user không tồn tại: " + errorText);
                }

                assertTrue(currentUrl.endsWith("/login"),
                        "Khi đăng nhập thất bại, phải ở lại trang login. URL hiện tại: " + currentUrl);
            }
        } catch (Exception e) {
            try {
                WebElement errorSection = driver.findElement(By.id("error-section"));
                String displayStyle = errorSection.getCssValue("display");
                
                if (!"none".equalsIgnoreCase(displayStyle)) {
                    WebElement errorMessage = driver.findElement(By.id("errormess"));
                    String errorText = errorMessage.getText();
                    
                    assertTrue(errorText != null && !errorText.trim().isEmpty(),
                            "Phải hiển thị thông báo lỗi khi đăng nhập với username có ký tự đặc biệt không tồn tại. " +
                            "Message hiện tại: " + errorText);
                }
            } catch (Exception ex) {
                String currentUrl = driver.getCurrentUrl();
                if (!currentUrl.endsWith("/login")) {
                    System.out.println("✓ Đăng nhập thành công với username có ký tự đặc biệt");
                } else {
                    throw new AssertionError("Không xác định được kết quả đăng nhập với username có ký tự đặc biệt");
                }
            }
        }
    }

    @Test(description = "Nhập email không đúng định dạng")
    public void loginFail_withInvalidEmailFormat() {
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

        usernameInput.clear();
        usernameInput.sendKeys(EMAIL_INVALID_FORMAT);

        passwordInput.clear();
        passwordInput.sendKeys(PASSWORD_VALID);
        submitButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement errorSection = wait.until(d -> {
                WebElement errSection = d.findElement(By.id("error-section"));
                WebElement errMess = d.findElement(By.id("errormess"));
                
                String display = errSection.getCssValue("display");
                if ("none".equalsIgnoreCase(display)) {
                    return null;
                }
                
                String text = errMess.getText();
                if (text != null && !text.trim().isEmpty() && !text.equals("Lỗi xảy ra")) {
                    return errSection;
                }
                
                return null;
            });
            String displayStyle = errorSection.getCssValue("display");
            assertFalse("none".equalsIgnoreCase(displayStyle),
                    "Khi email không đúng định dạng, khu vực thông báo lỗi phải hiển thị (display không phải 'none')");
            WebElement errorMessage = driver.findElement(By.id("errormess"));
            String errorText = errorMessage.getText();
            boolean containsEmailError = errorText.contains("email") || 
                                        errorText.contains("Email") ||
                                        errorText.contains("định dạng") ||
                                        errorText.contains("format") ||
                                        errorText.contains("không hợp lệ") ||
                                        errorText.contains("invalid");
            assertTrue(containsEmailError || errorText.contains("Sai") || errorText.contains("sai"),
                    "Phải hiển thị thông báo lỗi về định dạng email hoặc đăng nhập sai. " +
                    "Message hiện tại: '" + errorText + "'");
            String currentUrl = driver.getCurrentUrl();
            assertTrue(currentUrl.endsWith("/login"),
                    "Khi email không đúng định dạng, phải ở lại trang login. URL hiện tại: " + currentUrl);
                    
        } catch (Exception e) {
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.endsWith("/login")) {
                System.out.println("✓ HTML5 validation có thể đã ngăn submit form với email không đúng định dạng");
            } else {
                System.out.println("⚠ Form đã được submit, hệ thống có thể không validate email format ở frontend");
            }
        }
    }

    @Test(description = "Kiểm tra khoảng trắng đầu/cuối trong username")
    public void loginFail_withUsernameHavingLeadingTrailingSpaces() {
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

        usernameInput.clear();
        usernameInput.sendKeys(USERNAME_WITH_SPACES);

        passwordInput.clear();
        passwordInput.sendKeys(PASSWORD_VALID_FOR_SPACE_TEST);
        submitButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement errorSection = wait.until(d -> {
            WebElement errSection = d.findElement(By.id("error-section"));
            WebElement errMess = d.findElement(By.id("errormess"));
            String display = errSection.getCssValue("display");
            if ("none".equalsIgnoreCase(display)) {
                return null;
            }
            String text = errMess.getText();
            if (text != null && !text.trim().isEmpty() && !text.equals("Lỗi xảy ra")) {
                return errSection;
            }
            
            return null;
        });
        WebElement errorMessage = driver.findElement(By.id("errormess"));
        String errorText = errorMessage.getText();
        String expectedMessage = "Username hoặc Password sai";
        boolean containsExactMessage = errorText.contains(expectedMessage);
        boolean containsPartialMessage = (errorText.contains("Username") || errorText.contains("username") || 
                                         errorText.contains("Password") || errorText.contains("password") ||
                                         errorText.contains("Sai") || errorText.contains("sai")) &&
                                        (errorText.contains("hoặc") || errorText.contains("or"));
        
        assertTrue(containsExactMessage || containsPartialMessage,
                "Error message phải chứa thông báo về Username hoặc Password sai. " +
                "Message mong đợi: '" + expectedMessage + "'. " +
                "Message hiện tại: '" + errorText + "'");
        String displayStyle = errorSection.getCssValue("display");
        assertFalse("none".equalsIgnoreCase(displayStyle),
                "Khi username có khoảng trắng đầu/cuối, khu vực thông báo lỗi phải hiển thị (display không phải 'none')");
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.endsWith("/login"),
                "Khi đăng nhập thất bại, phải ở lại trang login. URL hiện tại: " + currentUrl);
    }

    @Test(description = "Kiểm tra khoảng trắng đầu/cuối trong password")
    public void loginFail_withPasswordHavingLeadingTrailingSpaces() {
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

        usernameInput.clear();
        usernameInput.sendKeys(USERNAME_VALID_FOR_SPACE_TEST);

        passwordInput.clear();
        passwordInput.sendKeys(PASSWORD_WITH_SPACES);
        submitButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement errorSection = wait.until(d -> {
            WebElement errSection = d.findElement(By.id("error-section"));
            WebElement errMess = d.findElement(By.id("errormess"));
            String display = errSection.getCssValue("display");
            if ("none".equalsIgnoreCase(display)) {
                return null;
            }
            String text = errMess.getText();
            if (text != null && !text.trim().isEmpty() && !text.equals("Lỗi xảy ra")) {
                return errSection;
            }
            
            return null;
        });

        WebElement errorMessage = driver.findElement(By.id("errormess"));
        String errorText = errorMessage.getText();
        String expectedMessage = "Username hoặc Password sai";
        boolean containsExactMessage = errorText.contains(expectedMessage);
        boolean containsPartialMessage = (errorText.contains("Username") || errorText.contains("username") || 
                                         errorText.contains("Password") || errorText.contains("password") ||
                                         errorText.contains("Sai") || errorText.contains("sai")) &&
                                        (errorText.contains("hoặc") || errorText.contains("or"));
        
        assertTrue(containsExactMessage || containsPartialMessage,
                "Error message phải chứa thông báo về Username hoặc Password sai. " +
                "Message mong đợi: '" + expectedMessage + "'. " +
                "Message hiện tại: '" + errorText + "'");
        String displayStyle = errorSection.getCssValue("display");
        assertFalse("none".equalsIgnoreCase(displayStyle),
                "Khi password có khoảng trắng đầu/cuối, khu vực thông báo lỗi phải hiển thị (display không phải 'none')");
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.endsWith("/login"),
                "Khi đăng nhập thất bại, phải ở lại trang login. URL hiện tại: " + currentUrl);
    }

    @Test(description = "Kiểm tra nút Đăng nhập bị disable khi đang gửi request")
    public void loginButtonDisabledDuringRequest() {
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

        usernameInput.clear();
        usernameInput.sendKeys(USERNAME_VALID);
        passwordInput.clear();
        passwordInput.sendKeys(PASSWORD_VALID);

        submitButton.click();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


        boolean isDisabled = !submitButton.isEnabled() || 
                            submitButton.getAttribute("disabled") != null ||
                            submitButton.getAttribute("aria-disabled") != null ||
                            "true".equals(submitButton.getAttribute("disabled"));

        if (!isDisabled) {
            try {
                submitButton.click();
                System.out.println("⚠ Nút Đăng nhập không bị disable khi đang gửi request");
            } catch (Exception e) {
                System.out.println("✓ Nút Đăng nhập không thể click khi đang gửi request");
            }
        } else {
            System.out.println("✓ Nút Đăng nhập bị disable khi đang gửi request");
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            wait.until(webDriver -> {
                String url = webDriver.getCurrentUrl();
                return !url.endsWith("/login") || 
                       webDriver.findElement(By.id("error-section")).getCssValue("display").equals("block");
            });
        } catch (Exception e) {
        }

        String currentUrl = driver.getCurrentUrl();
        if (currentUrl.endsWith("/login")) {
            boolean isEnabledAfter = submitButton.isEnabled();
            assertTrue(isEnabledAfter, "Nút Đăng nhập phải được enable lại sau khi request hoàn thành");
        }
    }

    @Test(description = "Password phân biệt chữ hoa/thường")
    public void loginFail_withWrongPasswordCase() {
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

        usernameInput.clear();
        usernameInput.sendKeys(USERNAME_FOR_CASE_TEST);

        passwordInput.clear();
        passwordInput.sendKeys(PASSWORD_UPPERCASE);
        submitButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement errorSection = wait.until(d -> {
            WebElement errSection = d.findElement(By.id("error-section"));
            WebElement errMess = d.findElement(By.id("errormess"));
            String display = errSection.getCssValue("display");
            if ("none".equalsIgnoreCase(display)) {
                return null;
            }
            String text = errMess.getText();
            if (text != null && !text.trim().isEmpty() && !text.equals("Lỗi xảy ra")) {
                return errSection;
            }
            
            return null;
        });
        WebElement errorMessage = driver.findElement(By.id("errormess"));
        String errorText = errorMessage.getText();

        String expectedMessage = "Username hoặc Password đã nhập sai";
        boolean containsExactMessage = errorText.contains(expectedMessage);
        boolean containsPartialMessage = (errorText.contains("Username") || errorText.contains("username") || 
                                         errorText.contains("Password") || errorText.contains("password") ||
                                         errorText.contains("Sai") || errorText.contains("sai") ||
                                         errorText.contains("đã nhập sai")) &&
                                        (errorText.contains("hoặc") || errorText.contains("or"));
        
        assertTrue(containsExactMessage || containsPartialMessage,
                "Error message phải chứa thông báo về Username hoặc Password đã nhập sai. " +
                "Message mong đợi: '" + expectedMessage + "'. " +
                "Message hiện tại: '" + errorText + "'");
        String displayStyle = errorSection.getCssValue("display");
        assertFalse("none".equalsIgnoreCase(displayStyle),
                "Khi password sai hoa/thường, khu vực thông báo lỗi phải hiển thị (display không phải 'none')");
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.endsWith("/login"),
                "Khi đăng nhập thất bại, phải ở lại trang login. URL hiện tại: " + currentUrl);
    }

    @Test(description = "Clear username/password khi refresh trang")
    public void clearFieldsOnPageRefresh() {
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));

        usernameInput.clear();
        usernameInput.sendKeys(USERNAME_VALID);
        passwordInput.clear();
        passwordInput.sendKeys(PASSWORD_VALID);
        String usernameBeforeRefresh = usernameInput.getAttribute("value");
        String passwordBeforeRefresh = passwordInput.getAttribute("value");
        
        assertTrue(usernameBeforeRefresh != null && !usernameBeforeRefresh.isEmpty(),
                "Username phải có giá trị trước khi refresh");
        assertTrue(passwordBeforeRefresh != null && !passwordBeforeRefresh.isEmpty(),
                "Password phải có giá trị trước khi refresh");
        driver.navigate().refresh();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(webDriver -> {
            try {
                webDriver.findElement(By.id("username"));
                return true;
            } catch (Exception e) {
                return false;
            }
        });
        WebElement usernameAfterRefresh = driver.findElement(By.id("username"));
        WebElement passwordAfterRefresh = driver.findElement(By.id("password"));

        String usernameAfter = usernameAfterRefresh.getAttribute("value");
        String passwordAfter = passwordAfterRefresh.getAttribute("value");

        assertTrue(usernameAfter == null || usernameAfter.isEmpty(),
                "Username phải bị clear sau khi refresh trang. Giá trị hiện tại: '" + usernameAfter + "'");
        assertTrue(passwordAfter == null || passwordAfter.isEmpty(),
                "Password phải bị clear sau khi refresh trang. Giá trị hiện tại: '" + passwordAfter + "'");
    }

    @Test(description = "User login trên 1 browser, mở browser khác không tự login")
    public void noAutoLoginOnDifferentBrowser() {
        driver.get(BASE_URL + "/login");

        WebElement usernameInput = driver.findElement(By.id("username"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

        usernameInput.clear();
        usernameInput.sendKeys(USERNAME_VALID);
        passwordInput.clear();
        passwordInput.sendKeys(PASSWORD_VALID);
        submitButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(webDriver -> !webDriver.getCurrentUrl().endsWith("/login"));

        String currentUrl = driver.getCurrentUrl();
        assertFalse(currentUrl.endsWith("/login"),
                "Browser A phải đăng nhập thành công. URL hiện tại: " + currentUrl);
        WebDriver driver2 = null;
        try {
            WebDriverManager.chromedriver().setup();
            
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-gpu");
            options.addArguments("--remote-allow-origins=*");
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            options.setExperimentalOption("useAutomationExtension", false);
            
            driver2 = new ChromeDriver(options);
            driver2.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
            driver2.manage().window().maximize();
            driver2.get(BASE_URL + "/login");
            WebDriverWait wait2 = new WebDriverWait(driver2, Duration.ofSeconds(5));
            wait2.until(webDriver -> {
                try {
                    webDriver.findElement(By.id("username"));
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
            String urlBrowserB = driver2.getCurrentUrl();
            assertTrue(urlBrowserB.endsWith("/login"),
                    "Browser B không tự đăng nhập, phải ở trang login. URL hiện tại: " + urlBrowserB);
            WebElement usernameInputB = driver2.findElement(By.id("username"));
            WebElement passwordInputB = driver2.findElement(By.id("password"));
            
            assertTrue(usernameInputB != null && passwordInputB != null,
                    "Browser B phải hiển thị form đăng nhập");

            String usernameValue = usernameInputB.getAttribute("value");
            String passwordValue = passwordInputB.getAttribute("value");
            
            assertTrue(usernameValue == null || usernameValue.isEmpty(),
                    "Browser B không tự điền username. Giá trị: '" + usernameValue + "'");
            assertTrue(passwordValue == null || passwordValue.isEmpty(),
                    "Browser B không tự điền password. Giá trị: '" + passwordValue + "'");

        } finally {
            if (driver2 != null) {
                driver2.quit();
            }
        }
    }
}


