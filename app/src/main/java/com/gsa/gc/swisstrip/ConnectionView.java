package com.gsa.gc.swisstrip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author gianc
 * @version 18.11.2021
 *
 * Connection View
 */
public class ConnectionView extends AppCompatActivity {

    public ListView listView;
    public String[] departureDestination;
    public String[] departureTime;
    public String[] departurePlatform;
    public String[] arrivalDestination;
    public String[] arrivalTime;
    public String[] arrivalPlatform;
    public String[] travelTime;

    public String[] weatherView;
    public String[] destination;
    public String[] mainWeather;
    public String[] description;
    public String[] temperature;
    public String[] wind;
    public String[] rainHour;
    ListView weatherList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_board);

        Intent intent = getIntent();
        String json = intent.getStringExtra("connection");

        Gson gson = new Gson();
        List<Connection> listOfConnections = gson.fromJson(json, new TypeToken<List<Connection>>() {
        }.getType());
        Connection connection = null;
        listView = findViewById(R.id.listView);

        departureDestination = new String[listOfConnections.size()];
        departureTime = new String[listOfConnections.size()];
        departurePlatform = new String[listOfConnections.size()];
        arrivalDestination = new String[listOfConnections.size()];
        arrivalTime = new String[listOfConnections.size()];
        arrivalPlatform = new String[listOfConnections.size()];
        travelTime = new String[listOfConnections.size()];

        Connection lastConnection = null;

        for (int i = 0; i < listOfConnections.size(); i++) {
            connection = listOfConnections.get(i);

            // Departure
            departureDestination[i] = connection.getDepartureDestination().getName();
            departureTime[i] = connection.getDepartureDate();
            departurePlatform[i] = connection.getDeparturePlatform();

            // Arrival
            arrivalDestination[i] = connection.getArrivalDestination().getName();
            arrivalTime[i] = connection.getArrivalDate();
            arrivalPlatform[i] = connection.getArrivalPlatform();
            lastConnection = connection;
        }
        ConnectionView.MyAdapter myAdapter = new ConnectionView.MyAdapter(ConnectionView.this,departureDestination, departureTime, departurePlatform, arrivalDestination, arrivalTime, arrivalPlatform, travelTime);
        listView.setAdapter(myAdapter);

        destination = new String[1];
        mainWeather = new String[1];
        description = new String[1];
        temperature = new String[1];
        wind = new String[1];
        rainHour = new String[1];
        weatherList = findViewById(R.id.weatherList);

        WeatherService weatherService = null;
        try {
            weatherService = new WeatherService();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Weather weather = null;
        try {
            weather = weatherService.getWeather(lastConnection.getArrivalDestination()).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }
        class MyAdapter extends ArrayAdapter<String>{
            Context context;
            String aDepartureDestination[];
            String aDepartureTime[];
            String aDeparturePlatform[];
            String aArrivalDestination[];
            String aArrivalTime[];
            String aArrivalPlatform[];
            String aTravelTime[];

            MyAdapter(Context context,String departureDestination[], String departureTime[], String departurePlatform[], String arrivalDestination[], String arrivalTime[], String arrivalPlatform[], String travelTime[]){
                super(context,R.layout.row,R.id.departureDestination,departureDestination);
                this.context = context;
                this.aDepartureDestination = departureDestination;
                this.aDepartureTime = departureTime;
                this.aDeparturePlatform = departurePlatform;
                this.aArrivalDestination = arrivalDestination;
                this.aArrivalTime = arrivalTime;
                this.aArrivalPlatform = arrivalPlatform;
                this.aTravelTime = travelTime;
            }

            @NotNull
            @Override
            public View getView(int position, @Nullable View convertView, @NotNull ViewGroup parent){
                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View row = layoutInflater.inflate(R.layout.row,parent,false);
                TextView _departureDestination = row.findViewById(R.id.departureDestination);
                TextView _departureTime = row.findViewById(R.id.departureTime);
                TextView _departurePlatform = row.findViewById(R.id.departurePlatform);
                TextView _travelTime = row.findViewById(R.id.travelTime);
                TextView _arrivalDestination = row.findViewById(R.id.arrivalDestination);
                TextView _arrivalTime = row.findViewById(R.id.arrivalTime);
                TextView _arrivalPlatform = row.findViewById(R.id.arrivalPlatform);

                // Text setzten
                _departureDestination.setText(aDepartureDestination[position]);
                _departureTime.setText(aDepartureTime[position]);
                _departurePlatform.setText(aDeparturePlatform[position]);
                _travelTime.setText(aTravelTime[position]);
                _arrivalDestination.setText(aArrivalDestination[position]);
                _arrivalTime.setText(aArrivalTime[position]);
                _arrivalPlatform.setText(aArrivalPlatform[position]);

                return row;
            }
        }
        class WeatherAdapter extends ArrayAdapter<String>{
            Context context;
            String aDestination[];
            String aWeather[];
            String aDescription[];
            String aTemperatur[];
            String aWind[];
            String aRainPerHour[];

            WeatherAdapter(Context context,String destination[], String mainWeather[],String description[], String temperatur[], String wind[], String rainPerHour[]) {
                super(context, R.layout.weather, R.id.destination, destination);
                this.context=context;
                this.aDestination = destination;
                this.aWeather = mainWeather;
                this.aDescription = description;
                this.aTemperatur = temperatur;
                this.aWind = wind;
                this.aRainPerHour = rainPerHour;
            }

            @NotNull
            @Override
            public View getView(int position,@Nullable View convertView, @NonNull ViewGroup parent) {
                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View weather = layoutInflater.inflate(R.layout.weather, parent, false);

                TextView _destination = weather.findViewById(R.id.destination);
                TextView _weather = weather.findViewById(R.id.weather);
                TextView _description = weather.findViewById(R.id.description);
                TextView _temperature = weather.findViewById(R.id.temeratur);
                TextView _wind = weather.findViewById(R.id.wind);
                TextView _rainPerHour = weather.findViewById(R.id.rainPerHour);

                _destination.setText(destination[position]);
                _weather.setText(aWeather[position]);
                _description.setText(description[position]);
                _temperature.setText(temperature[position]);
                _wind.setText(wind[position]);
                _rainPerHour.setText(rainHour[position]);

                return weather;
            }
        }
}
