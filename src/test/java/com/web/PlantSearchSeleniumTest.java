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
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.testng.Assert.*;

public class PlantSearchSeleniumTest {

    private static WebDriver driver;

    private static final String BASE_URL = "http://localhost:8080";

    private static final String VALID_PLANT_NAME = "Nhân sâm";
    private static final String PARTIAL_PLANT_NAME = "sâm";
    private static final String VALID_SCIENTIFIC_NAME = "Panax ginseng";
    private static final String NON_EXISTENT_NAME = "CâyKhôngTồnTại123456";
    private static final String PLANT_NAME_WITH_SPECIAL_CHARS = "Cây@#$%";
    private static final String PLANT_NAME_WITH_SPACES = "  Nhân sâm  ";

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

    @Test(description = "TC01: Tìm kiếm với tên cây dược liệu hợp lệ (tên đầy đủ)")
    public void testSearchWithValidFullPlantName() {
        String searchUrl = BASE_URL + "/plant?search=" + VALID_PLANT_NAME;
        driver.get(searchUrl);
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(webDriver -> {
            try {
                webDriver.findElement(By.id("param"));
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/plant"), 
                "URL phải chứa /plant. URL hiện tại: " + currentUrl);
        assertTrue(currentUrl.contains("search="), 
                "URL phải chứa tham số search. URL hiện tại: " + currentUrl);
        
        WebElement inputAfterSearch = driver.findElement(By.id("param"));
        String inputValue = inputAfterSearch.getAttribute("value");
        assertTrue(inputValue != null && inputValue.contains(VALID_PLANT_NAME.trim()),
                "Input phải chứa tên cây từ URL parameter. Giá trị hiện tại: '" + inputValue + "'");

        WebElement listData = driver.findElement(By.id("listData"));
        String listDataText = listData.getText();
        
        assertTrue(listDataText != null && !listDataText.isEmpty(),
                "Kết quả tìm kiếm phải được hiển thị");
        
        assertTrue(listDataText.toLowerCase().contains(VALID_PLANT_NAME.toLowerCase()),
                "Kết quả phải chứa tên cây tìm kiếm. Nội dung: " + listDataText);
        
        driver.get(BASE_URL + "/plant");
        wait.until(webDriver -> {
            try {
                webDriver.findElement(By.id("param"));
                return true;
            } catch (Exception e) {
                return false;
            }
        });
        
        WebElement searchInput = driver.findElement(By.id("param"));
        searchInput.clear();
        searchInput.sendKeys(VALID_PLANT_NAME);
        
        searchInput.sendKeys(" ");
        searchInput.sendKeys(Keys.BACK_SPACE);
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        listData = driver.findElement(By.id("listData"));
        listDataText = listData.getText();
        assertTrue(listDataText.toLowerCase().contains(VALID_PLANT_NAME.toLowerCase()),
                "Kết quả phải chứa tên cây khi nhập trực tiếp vào input. Nội dung: " + listDataText);
    }

    @Test(description = "TC02: Tìm kiếm với tên một phần (partial match)")
    public void testSearchWithPartialName() {
        driver.get(BASE_URL + "/plant");
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(webDriver -> {
            try {
                webDriver.findElement(By.id("param"));
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        WebElement searchInput = driver.findElement(By.id("param"));
        searchInput.clear();
        searchInput.sendKeys(PARTIAL_PLANT_NAME);
        searchInput.sendKeys(Keys.ENTER);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement listData = driver.findElement(By.id("listData"));
        String listDataText = listData.getText();
        
        assertTrue(listDataText != null && !listDataText.isEmpty(),
                "Kết quả tìm kiếm phải được hiển thị khi tìm với tên một phần");
        
        assertTrue(listDataText.toLowerCase().contains(PARTIAL_PLANT_NAME.toLowerCase()),
                "Kết quả phải chứa từ khóa tìm kiếm. Nội dung: " + listDataText);
    }

    @Test(description = "TC03: Tìm kiếm với tên khoa học")
    public void testSearchWithScientificName() {
        driver.get(BASE_URL + "/plant");
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(webDriver -> {
            try {
                webDriver.findElement(By.id("param"));
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        WebElement searchInput = driver.findElement(By.id("param"));
        searchInput.clear();
        searchInput.sendKeys(VALID_SCIENTIFIC_NAME);
        searchInput.sendKeys(Keys.ENTER);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement listData = driver.findElement(By.id("listData"));
        String listDataText = listData.getText();
        
        assertTrue(listDataText != null && !listDataText.isEmpty(),
                "Kết quả tìm kiếm phải được hiển thị khi tìm với tên khoa học");
        
        assertTrue(listDataText.toLowerCase().contains(VALID_SCIENTIFIC_NAME.toLowerCase()),
                "Kết quả phải chứa tên khoa học tìm kiếm. Nội dung: " + listDataText);
    }

    @Test(description = "TC04: Tìm kiếm với tên không tồn tại")
    public void testSearchWithNonExistentName() {
        driver.get(BASE_URL + "/plant");
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(webDriver -> {
            try {
                webDriver.findElement(By.id("param"));
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        WebElement searchInput = driver.findElement(By.id("param"));
        searchInput.clear();
        searchInput.sendKeys(NON_EXISTENT_NAME);
        searchInput.sendKeys(Keys.ENTER);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement listData = driver.findElement(By.id("listData"));
        String listDataText = listData.getText();
        
        boolean hasNoResultsMessage = listDataText.toLowerCase().contains("không tìm thấy") ||
                                     listDataText.toLowerCase().contains("không có kết quả") ||
                                     listDataText.toLowerCase().contains("no results") ||
                                     listDataText.trim().isEmpty();
        
        List<WebElement> plantCards = listData.findElements(By.cssSelector(".card"));
        boolean isEmpty = plantCards.isEmpty() || plantCards.size() == 0;
        
        assertTrue(hasNoResultsMessage || isEmpty,
                "Khi tìm kiếm với tên không tồn tại, phải hiển thị thông báo 'Không tìm thấy' hoặc danh sách rỗng. " +
                "Nội dung hiện tại: '" + listDataText + "', Số lượng kết quả: " + plantCards.size());
    }

    @Test(description = "TC05: Tìm kiếm với chuỗi rỗng")
    public void testSearchWithEmptyString() {
        driver.get(BASE_URL + "/plant");
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(webDriver -> {
            try {
                webDriver.findElement(By.id("param"));
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        String initialUrl = driver.getCurrentUrl();
        
        WebElement searchInput = driver.findElement(By.id("param"));
        searchInput.clear();
        searchInput.sendKeys(Keys.ENTER);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = driver.getCurrentUrl();
        
        boolean urlNotChanged = currentUrl.equals(initialUrl);
        boolean noSearchParam = !currentUrl.contains("search=");
        
        assertTrue(urlNotChanged || noSearchParam,
                "Khi nhập chuỗi rỗng, không nên redirect hoặc thêm tham số search. " +
                "URL ban đầu: " + initialUrl + ", URL hiện tại: " + currentUrl);
    }

    @Test(description = "TC06: Tìm kiếm không phân biệt hoa thường")
    public void testSearchCaseInsensitive() {
        driver.get(BASE_URL + "/plant");
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(webDriver -> {
            try {
                webDriver.findElement(By.id("param"));
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        String searchTermUpper = VALID_PLANT_NAME.toUpperCase();
        String searchTermLower = VALID_PLANT_NAME.toLowerCase();
        String searchTermMixed = "NhÂn SÂm";

        WebElement searchInput = driver.findElement(By.id("param"));
        searchInput.clear();
        searchInput.sendKeys(searchTermUpper);
        searchInput.sendKeys(Keys.ENTER);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement listData = driver.findElement(By.id("listData"));
        String listDataText = listData.getText();
        
        assertTrue(listDataText != null && !listDataText.isEmpty(),
                "Kết quả tìm kiếm phải được hiển thị khi tìm với chữ hoa");
        
        assertTrue(listDataText.toLowerCase().contains(VALID_PLANT_NAME.toLowerCase()),
                "Kết quả phải chứa tên cây tìm kiếm (không phân biệt hoa thường). " +
                "Tìm với: '" + searchTermUpper + "', Nội dung: " + listDataText);

        searchInput.clear();
        searchInput.sendKeys(searchTermLower);
        searchInput.sendKeys(Keys.ENTER);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        listData = driver.findElement(By.id("listData"));
        listDataText = listData.getText();
        
        assertTrue(listDataText.toLowerCase().contains(VALID_PLANT_NAME.toLowerCase()),
                "Kết quả phải chứa tên cây tìm kiếm khi tìm với chữ thường. " +
                "Tìm với: '" + searchTermLower + "', Nội dung: " + listDataText);

        searchInput.clear();
        searchInput.sendKeys(searchTermMixed);
        searchInput.sendKeys(Keys.ENTER);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        listData = driver.findElement(By.id("listData"));
        listDataText = listData.getText();
        
        assertTrue(listDataText.toLowerCase().contains(VALID_PLANT_NAME.toLowerCase()),
                "Kết quả phải chứa tên cây tìm kiếm khi tìm với chữ hoa thường lẫn lộn. " +
                "Tìm với: '" + searchTermMixed + "', Nội dung: " + listDataText);
    }

    @Test(description = "TC07: Tìm kiếm với ký tự đặc biệt")
    public void testSearchWithSpecialCharacters() {
        driver.get(BASE_URL + "/plant");
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(webDriver -> {
            try {
                webDriver.findElement(By.id("param"));
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        WebElement searchInput = driver.findElement(By.id("param"));
        searchInput.clear();
        searchInput.sendKeys(PLANT_NAME_WITH_SPECIAL_CHARS);
        searchInput.sendKeys(Keys.ENTER);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/plant"), 
                "Hệ thống không nên bị lỗi khi tìm kiếm với ký tự đặc biệt. URL: " + currentUrl);
        
        String pageSource = driver.getPageSource();
        assertFalse(pageSource.contains("error") && pageSource.contains("500") ||
                   pageSource.contains("Exception") ||
                   pageSource.contains("Internal Server Error"),
                "Hệ thống không nên hiển thị lỗi khi tìm kiếm với ký tự đặc biệt");
        
        WebElement inputAfterSearch = driver.findElement(By.id("param"));
        assertTrue(inputAfterSearch.isDisplayed() && inputAfterSearch.isEnabled(),
                "Input tìm kiếm vẫn phải hoạt động bình thường sau khi tìm với ký tự đặc biệt");
    }

    @Test(description = "TC08: Tìm kiếm với khoảng trắng thừa")
    public void testSearchWithExtraWhitespace() {
        driver.get(BASE_URL + "/plant");
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(webDriver -> {
            try {
                webDriver.findElement(By.id("param"));
                return true;
            } catch (Exception e) {
                return false;
            }
        });

        WebElement searchInput = driver.findElement(By.id("param"));
        searchInput.clear();
        searchInput.sendKeys(PLANT_NAME_WITH_SPACES);
        searchInput.sendKeys(Keys.ENTER);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        WebElement listData = driver.findElement(By.id("listData"));
        String listDataText = listData.getText();
        
        assertTrue(listDataText != null,
                "Hệ thống phải xử lý được khoảng trắng thừa và hiển thị kết quả");
        
        WebElement inputAfterSearch = driver.findElement(By.id("param"));
        String inputValue = inputAfterSearch.getAttribute("value");
        
        String trimmedName = VALID_PLANT_NAME.trim();
        boolean hasResults = listDataText.toLowerCase().contains(trimmedName.toLowerCase()) ||
                            !listDataText.trim().isEmpty();
        
        assertTrue(hasResults,
                "Hệ thống phải tự động trim khoảng trắng và tìm kiếm đúng. " +
                "Tìm với: '" + PLANT_NAME_WITH_SPACES + "', " +
                "Giá trị input sau search: '" + inputValue + "', " +
                "Kết quả: " + listDataText);
    }
}

