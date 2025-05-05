/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.liveweatherforecast;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Scanner;

/**
 *
 * @author akira
 */
public class LiveWeatherForecast {

    public static void main(String[] args) {

        String apiKey = "c418a8ff94553adc7546ec97d53f6296";
        String baseUrl = "https://api.openweathermap.org/data/2.5/weather";
        int id = 0;

        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter Country:");
            String inputCountry = sc.nextLine();

            System.out.println("Enter City:");
            String inputCity = sc.nextLine();
            
            String content = new String(Files.readAllBytes(Paths.get("src/main/java/com/mycompany/liveweatherforecast/city.list.json")));
            
            JSONArray cities = new JSONArray(content);

            for (int i = 0; i < cities.length(); i++) {
                JSONObject city = cities.getJSONObject(i);
                if (city.getString("name").equals(inputCity) && city.getString("country").equals(inputCountry)) {
                    System.out.println(city);
                    id = city.getInt("id");
                }

            }

            String url = String.format("%s?id=%s&APPID=%s&units=metric", baseUrl, id, apiKey);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
