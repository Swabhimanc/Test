package org.example;

import com.opencsv.CSVWriter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ImageURLGetter {


    //google-chrome --remote-debugging-port=9222 --user-data-dir="/tmp/chrome-debug"

    public static void main(String[] args) throws InterruptedException {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", "localhost:9222");
//        options.addArguments("--headless");
//        options.addArguments("--no-sandbox");
//        options.addArguments("--disable-dev-shm-usage");
//        options.addArguments("--disable-gpu");
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.get("https://app.leonardo.ai/?_gl=1*13m2l16*_gcl_au*Mzg1NDIwMDA3LjE3MjYzNDM4NTE.*_ga*MTQwNzA0OTk3Ny4xNzI2MzQzODUx*_ga_9SZY51046C*MTcyNzU0NzY2OC42LjAuMTcyNzU0NzY2OC42MC4wLjA.");

            Thread.sleep(100000);
            driver.findElement(By.xpath("//input[@type='checkbox'")).click();
            List<WebElement> images = driver.findElements(By.tagName("img"));
            for(WebElement image : images) {
                String imageUrl = image.getAttribute("src");
                String[] data = {"image", imageUrl};
                writeToCSV(data, System.getProperty("user.dir") + "/src/main/resources/imageUrls.csv");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            driver.quit();
        }
    }

    public static void writeToCSV(String[] data, String filePath) throws Exception {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, true))) {
            writer.writeNext(data);
        }
    }
}
