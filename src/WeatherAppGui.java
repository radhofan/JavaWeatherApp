import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;


    public WeatherAppGui(){
        //set up GUI and title
        super("Weather App");

        //configure gui to end the programs proccess once it has been closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //set the size of gui
        setSize(450,650);

        //load our gui at the center of the screen
        setLocationRelativeTo(null);

        //make layout manager null to manually position components within the gui
        setLayout(null);

        //prevent any resize
        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents(){
        //Search field
        JTextField searchTextField = new JTextField();

        //set the location and size of our component
        searchTextField.setBounds(15, 15, 351, 45);

        //set the font and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);

        //weather image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);

        //tempareture text
        JLabel temparatureText = new JLabel("10 C");
        temparatureText.setBounds(0,350,450,48);
        temparatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        //center the text
        temparatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temparatureText);

        // weather condition description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        //humidity image
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        //humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b></html>");
        humidityText .setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        //windspeed image
        JLabel windSpeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windSpeedImage.setBounds(220, 500, 74, 66);
        add(windSpeedImage);

        //windspeed text
        JLabel windSpeedText = new JLabel("<html><b>Windspeed</b> 15Km</html>");
        windSpeedText.setBounds(310, 500, 85, 55);
        windSpeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windSpeedText);

        //search button
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        //change the cursor to hand when hovering over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //get location form user
                String userInput = searchTextField.getText();

                //validate input and remove whitespace
                if(userInput.replaceAll("\\s","").length() <= 0){
                    return;
                }

                //retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);

                //update gui

                //update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");

                //switch to current condition
                switch(weatherCondition){
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.pngImage"));
                        break;
                }

                //update temperature text
                double temperature = (double) weatherData.get("temperature");
                temparatureText.setText(temperature + " C");

                // update weather condition text
                weatherConditionDesc.setText(weatherCondition);

                // update humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                // update windspeed text
                double windSpeed = (double) weatherData.get("windSpeed");
                windSpeedText.setText("<html><b>Windspeed</b> " + windSpeed + "km/h</html>");
            }
        });
        add(searchButton);
    }

    private ImageIcon loadImage(String resourcePath){
        try{
            //read the image file from path
            BufferedImage image = ImageIO.read(new File(resourcePath));

            //return image icon
            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Cant find image");
        return null;
    }
}
