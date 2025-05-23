/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.weather.liveweatherforecast;

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
