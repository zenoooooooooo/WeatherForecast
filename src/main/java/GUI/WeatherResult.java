/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUI;

import com.weather.liveweatherforecast.Configuration;
import com.weather.liveweatherforecast.Images;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author akira
 */
public class WeatherResult extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(WeatherResult.class.getName());

    /**
     * Creates new form WeatherResult
     *
     * @param city
     * @param country
     * @param id
     */
    public WeatherResult(String city, String country, int id) {
        initComponents();
        setLocationRelativeTo(null);
        setResizable(false);
        URL bgUrl = Images.getResource(Images.BG_PATH);
        Images.scaleImage(bgUrl, bg);
        jLabel1.setText("Weather Forecast - 📍" + city + ", " + country);
        fetchWeatherForecast(city, country, id);
    }

    private void fetchWeatherForecast(String cityName, String country, int id) {
        String apiKey = Configuration.getWeatherApiKey();
        String baseUrl = "https://api.openweathermap.org/data/2.5/weather";

        try {
            String inputCountry = cityName;
            String inputCity = country;

            System.out.println(inputCountry);
            System.out.println(inputCity);
            String content = new String(Files.readAllBytes(Paths.get("src/main/resources/docs/city.list.json")));
            
            JSONArray cities = new JSONArray(content);

            for (int i = 0; i < cities.length(); i++) {
                JSONObject city = cities.getJSONObject(i);
                if (city.getString("name").equals(inputCity) && city.getString("country").equals(inputCountry)) {
                    System.out.println(city);
                    id = city.getInt("id");
                    break;
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
            fillLabels(responseBody);
            fetchAI(responseBody.toString());
        } catch (IOException | InterruptedException | URISyntaxException | JSONException e) {
            System.out.println(e);
        }
    }

    private void fillLabels(JSONObject responseBody) throws MalformedURLException {
        JSONObject main = responseBody.getJSONObject("main");
        System.out.println(responseBody);
        double temperature = main.getDouble("temp");
        double feelsLike = main.getDouble("feels_like");
        int pressure = main.getInt("pressure");
        
        int humidityVal = main.getInt("humidity");

        JSONArray weatherArr = responseBody.getJSONArray("weather");
        JSONObject weather = weatherArr.getJSONObject(0);
        String condition = weather.getString("description");
        String status = weather.getString("main");
        String icon = weather.getString("icon");

        URL iconUrl = new URL("https://openweathermap.org/img/wn/" + icon + "@2x.png");

        JSONObject wind = responseBody.getJSONObject("wind");
        double windSpeedVal = wind.getDouble("speed");

        JSONObject coord = responseBody.getJSONObject("coord");
        double lon = coord.getDouble("lon");
        double lat = coord.getDouble("lat");
        
        JSONObject clouds = responseBody.getJSONObject("clouds");
        int all = clouds.getInt("all");
        
        
        
        temperatureLabel.setText(String.format("%.2f°C", temperature));
        humidityLabel.setText(String.format("💧%d%%", humidityVal));
        conditionLabel.setText(capitalizeFirstLetter(condition));
        windSpeedLabel.setText(String.format("💨%.2f m/s", windSpeedVal));
        statusLabel.setText(status);
        coordsLabel.setText("Lat: " + lat + " | Lon: " + lon);
        feelsLikeLabel.setText(String.format("%.2f°C", feelsLike));
        cloudLabel.setText(all + "%");
        pressureLabel.setText(pressure + " hPa");
        Images.scaleImage(iconUrl, iconLabel);

    }

    private void fetchAI(String prompt) {
        String summary = "";

        String apiKey = Configuration.getAIApiKey();
        String baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

        try {
            HttpClient client = HttpClient.newHttpClient();

            String escapedPrompt = JSONObject.quote("Summarize this weather forecast nicely with a greetings first, for a user of all age and digital literacy: " + prompt);

            String requestBody = String.format("""
            {
                "contents": [
                    {
                        "parts": [
                            {
                                "text": %s
                            }
                        ]
                    }
                ]
            }
            """, escapedPrompt);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject responseBody = new JSONObject(response.body());
            System.out.println("AI Response: " + response.body());
            JSONArray candidates = responseBody.getJSONArray("candidates");
            if (candidates.length() > 0) {
                JSONObject firstCandidate = candidates.getJSONObject(0);
                JSONObject content = firstCandidate.getJSONObject("content");
                JSONArray parts = content.getJSONArray("parts");
                if (parts.length() > 0) {
                    summary = parts.getJSONObject(0).getString("text");
                }
            }
            AISummary.setText(summary);
        } catch (IOException | InterruptedException | JSONException e) {
            System.out.println("Error fetching AI content: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private String capitalizeFirstLetter(String input) {
        String[] words = input.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (w.length() > 0) {
                sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        character = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        AISummary = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        temperatureLabel = new javax.swing.JLabel();
        humidityLabel = new javax.swing.JLabel();
        conditionLabel = new javax.swing.JLabel();
        windSpeedLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        pressureLabel = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        cloudLabel = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        feelsLikeLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        iconLabel = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        coordsLabel = new javax.swing.JLabel();
        bg = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(0, 0, 102));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Monospaced", 1, 15)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Weather Forecast");

        jButton1.setBackground(new java.awt.Color(51, 51, 255));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("RETURN");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                .addGap(277, 277, 277))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 700, 50));

        jPanel2.setBackground(new java.awt.Color(0, 0, 102));

        character.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 51, 153)));

        jScrollPane2.setBackground(new java.awt.Color(0, 0, 102));
        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(255, 255, 255)), "AI Summary", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Liberation Sans", 0, 15), new java.awt.Color(255, 255, 255))); // NOI18N
        jScrollPane2.setForeground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setToolTipText("");
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        AISummary.setBackground(new java.awt.Color(0, 0, 102));
        AISummary.setColumns(20);
        AISummary.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        AISummary.setForeground(new java.awt.Color(255, 255, 51));
        AISummary.setLineWrap(true);
        AISummary.setRows(5);
        AISummary.setText("\n");
        AISummary.setBorder(null);
        jScrollPane2.setViewportView(AISummary);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(character, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(character, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 280, 700, 120));

        jPanel4.setBackground(new java.awt.Color(0, 51, 153));
        jPanel4.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 102)));
        jPanel4.setFocusCycleRoot(true);

        jPanel5.setBackground(new java.awt.Color(0, 51, 102));

        jLabel6.setFont(new java.awt.Font("Monospaced", 1, 15)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Weather Information:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(0, 6, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(0, 6, Short.MAX_VALUE))
        );

        temperatureLabel.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        temperatureLabel.setForeground(new java.awt.Color(255, 255, 51));
        temperatureLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        temperatureLabel.setText("temp");
        temperatureLabel.setToolTipText("");

        humidityLabel.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        humidityLabel.setForeground(new java.awt.Color(255, 255, 51));
        humidityLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        humidityLabel.setText("hum");
        humidityLabel.setToolTipText("");

        conditionLabel.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        conditionLabel.setForeground(new java.awt.Color(255, 255, 51));
        conditionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        conditionLabel.setText("cond");
        conditionLabel.setToolTipText("");

        windSpeedLabel.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        windSpeedLabel.setForeground(new java.awt.Color(255, 255, 51));
        windSpeedLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        windSpeedLabel.setText("ws");
        windSpeedLabel.setToolTipText("");

        jLabel7.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel7.setText("Temperature:");
        jLabel7.setToolTipText("");

        jLabel8.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel8.setText("Humidity:");
        jLabel8.setToolTipText("");

        jLabel9.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel9.setText("Condition:");
        jLabel9.setToolTipText("");

        jLabel10.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel10.setText("Wind Speed:");
        jLabel10.setToolTipText("");

        jLabel11.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel11.setText("Pressure:");
        jLabel11.setToolTipText("");

        pressureLabel.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        pressureLabel.setForeground(new java.awt.Color(255, 255, 51));
        pressureLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pressureLabel.setText("pressure");
        pressureLabel.setToolTipText("");

        jLabel12.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel12.setText("Cloud Cover:");
        jLabel12.setToolTipText("");

        cloudLabel.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        cloudLabel.setForeground(new java.awt.Color(255, 255, 51));
        cloudLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cloudLabel.setText("cloud");
        cloudLabel.setToolTipText("");

        jLabel13.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel13.setText("Feels Like:");
        jLabel13.setToolTipText("");

        feelsLikeLabel.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        feelsLikeLabel.setForeground(new java.awt.Color(255, 255, 51));
        feelsLikeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        feelsLikeLabel.setText("feelsLike");
        feelsLikeLabel.setToolTipText("");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(windSpeedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(conditionLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(humidityLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(temperatureLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pressureLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cloudLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                    .addComponent(feelsLikeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(temperatureLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(feelsLikeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(humidityLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(conditionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(windSpeedLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(pressureLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(cloudLabel))
                .addContainerGap())
        );

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 310, 200));

        jPanel3.setBackground(new java.awt.Color(0, 51, 153));
        jPanel3.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 102)));

        statusLabel.setFont(new java.awt.Font("Monospaced", 1, 15)); // NOI18N
        statusLabel.setForeground(new java.awt.Color(255, 255, 52));
        statusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        statusLabel.setText("Status");
        statusLabel.setToolTipText("");

        coordsLabel.setFont(new java.awt.Font("Monospaced", 1, 12)); // NOI18N
        coordsLabel.setForeground(new java.awt.Color(204, 204, 204));
        coordsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        coordsLabel.setText("Lat and Lon");
        coordsLabel.setToolTipText("");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(iconLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(statusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addContainerGap(73, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(coordsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(iconLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(coordsLabel)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 60, 270, 200));
        getContentPane().add(bg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 700, 350));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Dashboard d = new Dashboard();
        d.show();
        this.hide();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new WeatherResult("Cainta", "PH", 12345).setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea AISummary;
    private javax.swing.JLabel bg;
    private javax.swing.JLabel character;
    private javax.swing.JLabel cloudLabel;
    private javax.swing.JLabel conditionLabel;
    private javax.swing.JLabel coordsLabel;
    private javax.swing.JLabel feelsLikeLabel;
    private javax.swing.JLabel humidityLabel;
    private javax.swing.JLabel iconLabel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel pressureLabel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel temperatureLabel;
    private javax.swing.JLabel windSpeedLabel;
    // End of variables declaration//GEN-END:variables
}
