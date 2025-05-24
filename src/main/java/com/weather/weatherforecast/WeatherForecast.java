/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.weather.weatherforecast;

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
public class WeatherForecast {

    public static void main(String[] args) {

        Dashboard d = new Dashboard();
        d.show();

    }
}
