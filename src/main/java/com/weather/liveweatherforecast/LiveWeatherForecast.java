/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.weather.liveweatherforecast;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;
import GUI.Dashboard;

/**
 *
 * @author akira
 */
public class LiveWeatherForecast {

    public static void main(String[] args) {

        Dashboard d = new Dashboard();
        d.show();

        
                String apiKey = Configuration.getApiKey();
                String baseUrl = "https://api.openweathermap.org/data/2.5/weather";
                int id = 0;
        
                try {
        
                    String inputCountry = "PH";
        
                    String inputCity = "Cainta";
        
                    System.out.println(inputCountry);
                    System.out.println(inputCity);
                    String content = new String(Files.readAllBytes(Paths.get("src/main/resources/docs/city.list.json")));
        
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
        
                    JSONObject responseBody = new JSONObject(response.body());
        
//                    jLabel2.setText(responseBody.getJSONArray("weather").toString());
                    System.out.println(responseBody);
                } catch (Exception e) {
                    System.out.println(e);
                }
    }
}
