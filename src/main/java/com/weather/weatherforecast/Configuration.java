/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.weather.weatherforecast;

import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author akira
 */
public class Configuration {
    private static Properties properties = new Properties();

    static {
        try (InputStream input = Configuration.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
            } else {
                properties.load(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getWeatherApiKey() {
        return properties.getProperty("weather.api.key");
    }
    
    public static String getAIApiKey() {
        return properties.getProperty("ai.api.key");
    }
}
