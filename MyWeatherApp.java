package Application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MyWeatherApp {
    private static final String API_KEY = "20d226bf7eb2b3582b4af1962ea53000";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Enter a location: ");
            String location = sc.nextLine();
            fetchWeather(location);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void fetchWeather(String location) {
        try {
            String end = BASE_URL + "?q=" + location + "&appid=" + API_KEY + "&units=metric";
            URL url = new URL(end);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                parseWeatherData(response.toString());
            } else {
                System.err.println("Error: Unable to fetch weather for " + location + ". Please verify location.");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void parseWeatherData(String data) {
        try {
            String city = extractValue(data, "\"name\":\"", "\"");
            String lonStr = extractValue(data, "\"lon\":", ",");
            String latStr = extractValue(data, "\"lat\":", "}");
            String tempStr = extractValue(data, "\"temp\":", ",");
            String minTempStr = extractValue(data, "\"temp_min\":", ",");
            String maxTempStr = extractValue(data, "\"temp_max\":", ",");
            String pressureStr = extractValue(data, "\"pressure\":", ",");
            String humidityStr = extractValue(data, "\"humidity\":", ",");
            String windSpeedStr = extractValue(data, "\"speed\":", ",");
            String description = extractValue(data, "\"description\":\"", "\"");

            double longitude = Double.parseDouble(lonStr);
            double latitude = Double.parseDouble(latStr);
            double temp = Double.parseDouble(tempStr);
            double minTemp = Double.parseDouble(minTempStr);
            double maxTemp = Double.parseDouble(maxTempStr);
            double pressure = Double.parseDouble(pressureStr);
            double windSpeed = Double.parseDouble(windSpeedStr);
            int humidity = Integer.parseInt(humidityStr);

            printWeatherDetails(city, longitude, latitude, temp, minTemp, maxTemp, pressure, humidity, windSpeed, description);
        } catch (Exception e) {
            System.err.println("Error: Unable to parse weather data.");
        }
    }

    private static String extractValue(String json, String startDelimiter, String endDelimiter) {
        int startIndex = json.indexOf(startDelimiter) + startDelimiter.length();
        int endIndex = json.indexOf(endDelimiter, startIndex);
        return json.substring(startIndex, endIndex);
    }

    private static void printWeatherDetails(String city, double longitude, double latitude, double temp, double minTemp,
                                            double maxTemp, double pressure, int humidity, double windSpeed, String description) {
        System.out.println("\n-----------------------------------------------");
        System.out.println("Weather Details for: " + city);
        System.out.println("-----------------------------------------------");
        System.out.printf("Location: %s (%f°N, %f°E)\n", city, latitude, longitude);
        System.out.println("Temperature: " + temp + "°C");
        System.out.println("Min Temp: " + minTemp + "°C");
        System.out.println("Max Temp: " + maxTemp + "°C");
        System.out.println("Pressure: " + pressure + " hPa");
        System.out.println("Humidity: " + humidity + "%");
        System.out.println("Wind Speed: " + windSpeed + " m/s");
        System.out.println("Weather: " + description);
        System.out.println("-----------------------------------------------");
    }
}
