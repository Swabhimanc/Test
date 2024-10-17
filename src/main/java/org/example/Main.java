package org.example;

import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Main {

    // Method to make HTTP GET request and return response as String
    public static String sendGetRequest(String city) throws Exception {
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString());
//            String url = "https://osm23-geocode.dista.ai/search.php?q=" + encodedCity + "&format=json&addressdetails=1&limit=1&polygon_text=1";
            String url = "https://osm23-geocode.dista.ai/search.php?q=pune&polygon_geojson=1&viewbox=";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (Exception e) {
            return "";
        }
    }

    // Method to write data to CSV
    public static void writeToCSV(String[] data, String filePath) throws Exception {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, true))) {
            writer.writeNext(data);
        }
    }

    // Method to read cities from Excel and process them
    public static void readCitiesFromExcelAndProcess(String excelFilePath, String csvFilePath) throws Exception {
        try {
            FileInputStream fis = new FileInputStream(new File(excelFilePath));
            Workbook workbook = new XSSFWorkbook(fis);

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                String city = row.getCell(0).getStringCellValue();
                try {
                    city = row.getCell(0).getStringCellValue(); // Assuming city is in the first column
//                    System.out.println("Processing city: " + city);

                    // Make API request for the city
                    String response = sendGetRequest(city);

                    // Parse JSON response
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    // Extract "geotext" value
                    String geoText = jsonObject.getString("geotext");

                    // Write city and geoText to CSV
                    String[] data = {city, geoText};
                    writeToCSV(data, csvFilePath);
                } catch (Exception e) {
                    System.out.println("Unable to process for city: " + city);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            // Path to input Excel file and output CSV file
            String excelFilePath = System.getProperty("user.dir") + "/src/main/resources/Yum Store - Cities.xlsx";
            String csvFilePath = System.getProperty("user.dir") + "/src/main/resources/response2.csv";

            // Create the CSV file with headers
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
                String[] headers = {"City", "GeoText"};
                writer.writeNext(headers);
            }

            // Process cities and write results to CSV
//            readCitiesFromExcelAndProcess(excelFilePath, csvFilePath);

            System.out.println(sendGetRequest("pune"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
