package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.TimeUnit;

public class MapAutomation {
    public static void main(String[] args) {
        // Initialize WebDriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        WebDriver driver = new ChromeDriver(options);


        try {
            // Set implicit wait
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

            // Navigate to the URL
            driver.get("https://osm23-geocode.dista.ai/search.php?q=pune&polygon_geojson=1&viewbox=");

            // Click on the "show map bounds" button
            WebElement showMapBoundsButton = driver.findElement(By.cssSelector(".my-custom-control.leaflet-bar.btn.btn-sm.btn-default.leaflet-control"));
            showMapBoundsButton.click();

            // Get the 'viewbox' text from the map position element
            WebElement mapPositionElement = driver.findElement(By.id("map-position-inner"));
//            Thread.sleep(10000);
            String mapPositionText = mapPositionElement.getText();

            // Extract the viewbox text
            String viewbox = extractViewbox(mapPositionText);
            System.out.println("Viewbox: " + viewbox);

        } finally {
            driver.quit();
        }
    }

    // Method to extract viewbox value from the text
    public static String extractViewbox(String mapPositionText) {
        String[] lines = mapPositionText.split("\n");
        for (String line : lines) {
            if (line.contains("viewbox:")) {
                return line.split(":")[1].trim();
            }
        }
        return null;
    }
}

