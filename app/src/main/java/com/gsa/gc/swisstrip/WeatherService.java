package com.gsa.gc.swisstrip;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author gianc
 * @version 18.11.2021
 *
 * Wetter Service
 */
public class WeatherService {
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public WeatherService() throws IOException {
    }

    public void getLastCity(ArrayList<Connection> connections){
        int size = connections.size();
        Connection lastConnection = connections.get(size);
        getWeather(lastConnection.getArrivalDestination());
    }

    public Future<Weather> getWeather(City city) {
        return executorService.submit(() -> {
            String url = getUrl(city);
            Log.e("lkdjgldfkg", url);
            HttpURLConnection apiConnection = getApiConnection(url);
            return createWeather(apiConnection, city);
        });
    }

    public String getUrl(City city){
        double xPos = city.getxPos();
        double yPos = city.getyPos();

        return "https://api.openweathermap.org/data/2.5/weather?lat=" + xPos + "&lon=" + yPos + "&appid=f15635c569c4a58137e4977960782cfc";
    }

    public HttpURLConnection getApiConnection(String urlString) throws IOException {
        URL url = new URL(urlString);

        return null;
    }

    public Weather createWeather(HttpURLConnection httpConnection, City city) throws IOException, JSONException, ParseException {
        int respondeCode = httpConnection.getResponseCode();

        BufferedReader input = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
        String inputLine = input.readLine();
        JSONObject object = new JSONObject(inputLine);
        JSONArray weatherArray = object.getJSONArray("weather");
        JSONObject weatherObject = weatherArray.getJSONObject(0);


        Weather weather = new Weather();
        weather.setCity(city);
        weather.setId(weatherObject.getInt("id"));
        weather.setWeather(weatherObject.getString("main"));
        weather.setDescription(weatherObject.getString("description"));

        JSONObject mainObject = object.getJSONObject("main");
        weather.setTemperatur(mainObject.getDouble("temp"));

        JSONObject windObject = object.getJSONObject("wind");
        weather.setWindspeed(windObject.getDouble("speed"));

        if (object.isNull("rain")){
            weather.setRainPerHour(0);
        } else {
            JSONObject rainObject = object.getJSONObject("rain");
            weather.setRainPerHour(rainObject.getDouble("1h"));
        }
        Log.e("test", weather.toString());
        return weather;
    }
}
