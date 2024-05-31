import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

//retrieve weather data from api
public class WeatherApp {
    public static JSONObject getWeatherData(String locationName){
        //get location using api
        JSONArray locationData = getLocationData(locationName);

        //extract latitude and longitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        //build api request url with location coordinates
        String URLString = "https://api.open-meteo.com/v1/forecast?"
                            + "latitude=" + latitude + "&longitude=" + longitude
                            + "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";
        try{
            //call api
            HttpURLConnection conn = fetchApiResponse(URLString);

            //check response status
            if(conn.getResponseCode() != 200){
                System.out.println("Error");
                return null;
            }

            //store result json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                //read json and store into string builder
                resultJson.append(scanner.nextLine());
            }

            //close scanner
            scanner.close();

            //close connection
            conn.disconnect();

            //parse through data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            //retrieve hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            //get current hour data
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            //get temprature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            //get weather code
            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

            //get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            //get windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            //build weather json data object that we are going to access in out frontend
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windSpeed", windspeed);

            return weatherData;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray getLocationData(String locationName){
        //replace any whitespace location name
        locationName = locationName.replaceAll(" ","+");

        //build api url with location parameter
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name="
                + locationName + "&count=10&language=en&format=json";

        try{
            //call api and get a response
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check response status
            if(conn.getResponseCode() != 200){
                System.out.println("Error");
            }else{
                //store api results
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                //read and store the result into stringbuilder
                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }

                //close scanner
                scanner.close();

                //close url connection
                conn.disconnect();

                //parse json string into json object
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                //get list of location data from api
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            //attempt to create a connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //set request method to get
            conn.setRequestMethod("GET");

            //connect to api
            conn.connect();
            return conn;
        }catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();


        //iterate through time list and see which is our current time
        for(int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime(){
        //get current time
        LocalDateTime currentDateTime = LocalDateTime.now();

        //format data to be 2024-09-02T00:00
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        //format and print current date
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

    private static String convertWeatherCode(long weatherCode){
        String weatherCondition = "";
        if(weatherCode == 0L){
            weatherCondition = "Clear";
        }else if(weatherCode > 0L && weatherCode <= 3L){
            // cloudy
            weatherCondition = "Cloudy";
        }else if((weatherCode >= 51L && weatherCode <= 67L)
                || (weatherCode >= 80L && weatherCode <= 99L)){
            // rain
            weatherCondition = "Rain";
        }else if(weatherCode>= 71L && weatherCode <= 77L){
            // snow
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }
}
