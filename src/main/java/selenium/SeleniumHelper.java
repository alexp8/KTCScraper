package selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SeleniumHelper {
    private static final Logger logger = LogManager.getLogger();

    private static WebDriver driver;

    public static void takeScreenshot() {

        driver = SeleniumHelper.getDriver();

        // take screenshot
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = LocalDateTime.now().format(formatter);

        File destination = Paths.get("target", "screenshot-" + timestamp + ".png").toFile();
        logger.warn("Taking screenshot to '{}'", destination.toString());
        try {
            FileUtils.copyFile(screenshot, destination);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    public static WebDriver getDriver() {

        if (driver == null) {

            WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");

            driver = new ChromeDriver(options);
        }

        return driver;
    }

    public static void tear() {
        if (driver != null) {
//            driver.close();
            driver.quit();
            driver = null;
        }
    }




}
