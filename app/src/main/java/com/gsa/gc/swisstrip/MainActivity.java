package com.gsa.gc.swisstrip;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author gianc
 * @version 12.11.2021
 *
 * MainActivity
 */
public class MainActivity extends AppCompatActivity {

    public ListView listView;
    public String[] departureDestination;
    public String[] departureTime;
    public String[] departurePlatform;
    public String[] arrivalDestination;
    public String[] arrivalTime;
    public String[] arrivalPlatform;
    public String[] travelTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView time = findViewById(R.id.textTime);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                // Dialog für den TimePicker
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        time.setText(hourOfDay + ":" + minute);
                    }
                },hour,minute,true);
                timePickerDialog.show();
            }
        });

        TextView date = findViewById(R.id.textDate);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                // Dialog für den DatePicker
                DatePickerDialog pickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                    }
                }, year,month,day);
                pickerDialog.show();
            }
        });

        Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            List<Connection> connections = null;
            List<List<Connection>> listOfAllConnections = null;

            @Override
            public void onClick(View v) {
                // Von
                EditText from = findViewById(R.id.textForm);
                String fromString = from.getText().toString();

                // Bis
                EditText to = findViewById(R.id.textTo);
                String toString = to.getText().toString();

                // Zeit
                TextView time = findViewById(R.id.textTime);
                String timeString = time.getText().toString();

                // Datum
                TextView date = findViewById(R.id.textDate);
                String dateString = date.getText().toString();

                listView = findViewById(R.id.listView);
                ConnectionService connectionService = new ConnectionService();

                try{
                    Future<List<List<Connection>>> future = connectionService.getAllConnections(fromString,toString,timeString,dateString);
                    listOfAllConnections = future.get();
                } catch (ExecutionException e){
                    e.printStackTrace();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }

                departureDestination = new String[listOfAllConnections.size()];
                departurePlatform = new String[listOfAllConnections.size()];
                departureDestination = new String[listOfAllConnections.size()];
                travelTime = new String[listOfAllConnections.size()];
                arrivalDestination = new String[listOfAllConnections.size()];
                arrivalTime = new String[listOfAllConnections.size()];
                arrivalPlatform = new String[listOfAllConnections.size()];


                for (int i = 0; i < listOfAllConnections.size(); i++){
                    connections = listOfAllConnections.get(i);

                    Connection firstConnection = connections.get(0);
                    departureDestination[i] = firstConnection.getDepartureDestination().getName();
                    departurePlatform[i] = firstConnection.getDeparturePlatform();
                    departureTime[i] = firstConnection.getDepartureDate();

                    Connection lastConnection = null;
                    for (int j = 0; j < connections.size();j++){
                        lastConnection = connections.get(j) ;
                    }

                    arrivalDestination[i] = lastConnection.getArrivalDestination().getName();
                    arrivalPlatform[i] = lastConnection.getArrivalPlatform();
                    arrivalTime[i] = lastConnection.getArrivalDate();
                }
                MyAdapter myAdapter = new MyAdapter(MainActivity.this,departureDestination,departureTime,departurePlatform,arrivalDestination,arrivalTime,arrivalPlatform,travelTime);
                listView.setAdapter(myAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0){
                            launchActivity(listOfAllConnections.get(position));
                        }
                        if (position == 1){
                            launchActivity(listOfAllConnections.get(position));
                        }
                        if (position == 2){
                            launchActivity(listOfAllConnections.get(position));
                        }
                        if (position == 3){
                            launchActivity(listOfAllConnections.get(position));
                        }
                    }
                });

            }
        });

        Button weatherButton = findViewById(R.id.weatherButton);

        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,activity_Weather.class));
            }
        });
    }

    public void launchActivity(List<Connection> connections){
        Gson gson = new Gson();
        String json = gson.toJson(connections);

        Intent intent = new Intent(this,ConnectionView.class);
        intent.putExtra("connection",json);
        startActivity(intent);
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
}