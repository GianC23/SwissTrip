package com.gsa.gc.swisstrip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author gianc
 * @version 12.11.21
 *
 * Service für die Connection
 */
public class ConnectionService {
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ConnectionService(){

    }

    public Future<List<List<Connection>>> getAllConnections(String from,String to, String date,String time){
        return executorService.submit(() -> {
            String url = getUrl(from, to, date, time);
            HttpURLConnection httpURLConnection = getAPIConnection(url);
            return createConnection(httpURLConnection);
        });
    }

    public String getUrl(String from, String to, String date, String time) throws ParseException{
        String url = "";

        if(date == null){
            if (time == null){
                url = "https://transport.opendata.ch/v1/connections?from=" + from + "&to=" + to;
            } else{
                url = "https://transport.opendata.ch/v1/connections?from=" + from +"&to=" + to + "&time" + time;
            }
        } else if (time == null){
            url = "https://transport.opendata.ch/v1/connections?from=" + from + "&to=" + to + "&date" + date;
        } else {
            url = "https://transport.opendata.ch/v1/connections?from=" + from + "&to=" + to + "&date" + date + "&time" + time;
        }
        return url;
    }

    public HttpURLConnection getAPIConnection(String urlString) throws IOException{
        URL url = new URL(urlString);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        return httpURLConnection;
    }

    public List<List<Connection>> createConnection(HttpURLConnection httpURLConnection)throws IOException, JSONException,ParseException {

        BufferedReader input = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        String inputLine = input.readLine();
        JSONObject object = new JSONObject(inputLine);
        JSONArray tConnections = object.getJSONArray("connections");

        List<List<Connection>> connections = new ArrayList<>();

        for(int i = 0; i < 4 && i < tConnections.length(); i++){

            List<Connection> listOfConnection = new ArrayList<>();

            JSONObject trainConnection = tConnections.getJSONObject(i);
            JSONArray sections = trainConnection.getJSONArray("sections");

            for(int j = 0; j < sections.length();j++){
                Connection connection = new Connection();

                JSONObject section = sections.getJSONObject(j);
                JSONObject departure = section.getJSONObject("departure");

                // Abfahrt
                if (!departure.isNull("departure")){
                    connection.setDepartureDate(parseTimeFormat(departure.getString("departure")));
                }
                if (departure.isNull("platform")){
                    connection.setDeparturePlatform("not available");
                } else {
                    connection.setDeparturePlatform(departure.getString("platform"));
                }

                JSONObject departureStation = departure.getJSONObject("station");

                if (departureStation != null){
                    JSONObject coordinate = departureStation.getJSONObject("station");
                    connection.setDepartureDestination(new City
                            (departureStation .getInt("id"),departureStation.getString("name"),coordinate.getDouble("x")
                                    ,coordinate.getDouble("y")));
                }

                // Ankunft
                JSONObject arrival = section.getJSONObject("arrival");

                if (!arrival.isNull("arrival")){
                    connection.setArrivalDate(parseTimeFormat(arrival.getString("arrival")));
                }
                if (arrival.isNull("platform")){
                    connection.setArrivalPlatform("not available");
                } else {
                    connection.setArrivalPlatform(arrival.getString("platform"));
                }

                JSONObject arrivalStation = arrival.getJSONObject("station");

                if (arrivalStation != null){
                    JSONObject coordinate = arrivalStation.getJSONObject("coordinate");
                    connection.setArrivalDestination(new City
                            (arrivalStation .getInt("id"),arrivalStation.getString("name"),coordinate.getDouble("x")
                                    ,coordinate.getDouble("y")));
                }

                connection.setPosition(j);
                listOfConnection.add(connection);
            }

            connections.add(listOfConnection);
        }
        return connections;
    }

    public String parseTimeFormat(String date)throws ParseException{
        String pattern = "yyyy-MM-dd'HH:mm:ss'";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GTM"));
        Date dateFormatFull = simpleDateFormat.parse(date);

        String timePattern = "HH:mm";
        SimpleDateFormat timeFormatter = new SimpleDateFormat(timePattern);
        return timeFormatter.format(dateFormatFull);
    }
}
