package org.example;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ImageDownloader {

    // Method to download an image from a URL and save it to a specified folder
    private static final int MAX_RETRY_COUNT = 3;
    // Wait time between retries (in milliseconds)
    private static final int RETRY_WAIT_TIME = 3000;
    private static final int NUM_THREADS = 50;

    public static void downloadImage(String imageUrl, String destinationFolder, String fileName) {
        Logger logger = Logger.getLogger("Logger");
        int attempt = 0;
        boolean success = false;

        while (attempt < MAX_RETRY_COUNT && !success) {
            try {
                attempt++;
                logger.info("Attempt " + attempt + ": Downloading " + imageUrl);

                // Open connection to the URL
                URL url = new URL(imageUrl);
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(5000);  // Set timeout for connection
                connection.setReadTimeout(5000);     // Set timeout for reading the data

                int contentLength = connection.getContentLength(); // Get the size of the file

                if (contentLength < 0) {
                    throw new Exception("Failed to get content length, retrying...");
                }

                // Open input stream to read data from the URL
                try (InputStream inputStream = connection.getInputStream();
                     FileOutputStream outputStream = new FileOutputStream(new File(destinationFolder + File.separator + fileName))) {

                    byte[] buffer = new byte[1024];
                    int bytesRead = -1;
                    int totalBytesRead = 0;

                    System.out.println("Downloading...");

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;

                        // Calculate progress percentage
                        int progressPercentage = (int) ((totalBytesRead * 100L) / contentLength);

                        // Display progress
                        System.out.print("\rProgress: " + progressPercentage + "%");
                    }

                    System.out.println("\nDownload complete: " + fileName);
                    success = true;  // Mark as success after download completes
                }

            } catch (Exception e) {
                logger.warning("Error downloading image: " + imageUrl + " on attempt " + attempt);
                e.printStackTrace();

                // Check if retries are remaining
                if (attempt < MAX_RETRY_COUNT) {
                    logger.info("Retrying in " + (RETRY_WAIT_TIME / 1000) + " seconds...");
                    try {
                        Thread.sleep(RETRY_WAIT_TIME);  // Wait before retrying
                    } catch (InterruptedException ie) {
                        logger.warning("Interrupted while waiting to retry.");
                        Thread.currentThread().interrupt();
                    }
                } else {
                    logger.severe("Failed to download image after " + MAX_RETRY_COUNT + " attempts: " + imageUrl);
                }
            }
        }
    }

    public static void downloadImagesConcurrently(List<String[]> imageUrls, String destinationFolder) {
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS); // Create a thread pool

        // Submit tasks to download images concurrently
        for (int i = 0; i < imageUrls.size(); i++) {
            String[] record = imageUrls.get(i);
            String imageUrl = record[0];  // Assuming image URL is in the first column
            String fileName = "image_" + (i + 1) + ".jpg";  // Generate file names

            // Submit download task to the thread pool
            executor.submit(() -> {
                downloadImage(imageUrl, destinationFolder, fileName);
            });
        }

        // Gracefully shut down the executor after all tasks are submitted
        executor.shutdown();

        try {
            // Wait for all tasks to finish or timeout after 60 minutes
            executor.awaitTermination(60, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Method to read the CSV file and get the URLs of images
    public static List<String[]> downloadImagesFromCSV(String csvFilePath, String destinationFolder) {
        try (Reader reader = Files.newBufferedReader(Paths.get(csvFilePath));
             CSVReader csvReader = new CSVReader(reader)) {
            List<String[]> records = csvReader.readAll();
            return records;
        } catch (Exception e) {
            System.err.println("Error reading CSV file: " + csvFilePath);
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        // Path to the CSV file
        String csvFilePath = System.getProperty("user.dir") + "/src/main/resources/imageUrls.csv";

        // Destination folder to save the downloaded images
        String destinationFolder = System.getProperty("user.dir") + "/downloaded";

        // Ensure the destination folder exists
        new File(destinationFolder).mkdirs();

        // Start downloading images
        List<String[]> imageUrls = downloadImagesFromCSV(csvFilePath, destinationFolder);
        downloadImagesConcurrently(imageUrls, destinationFolder);
    }
}