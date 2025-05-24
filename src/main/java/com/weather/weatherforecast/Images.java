/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.weather.weatherforecast;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author akira
 */
public class Images {

    public static final String BG_PATH = "/images/bg.jpg";
    public static final String CLOUDY_PATH = "/images/Cloudy.png";
    public static final String MISTY_PATH = "/images/Misty.png";
    public static final String RAINY_PATH = "/images/Rainy.png";
    public static final String SNOWY_PATH = "/images/Snowy.png";
    public static final String SUNNY_PATH = "/images/Sunny.png";
    public static final String THUNDERSTORM_PATH = "/images/Thunderstorm.png";
    
    public static void scaleImage(URL url, JLabel label) {
        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage();
        Image imgScale = img.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(imgScale);
        label.setIcon(scaledIcon);
    }

    public static URL getResource(String path) {
        return Images.class.getResource(path);
    }
}
