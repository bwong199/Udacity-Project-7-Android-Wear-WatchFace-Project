package com.androidweardocs.wearabledatamap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class DataMapActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    GoogleApiClient googleClient;
    String weatherMain;
    String weatherDescription;
    String weatherId;
    DataMap dataMap;
    String WEARABLE_DATA_PATH;

    String temp_min;
    String temp_max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_map);

        // Build a new GoogleApiClient for the the Wearable API
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        Timer timer = new Timer ();
        TimerTask hourlyTask = new TimerTask () {
            @Override
            public void run () {
                try {
                    DownloadTask task = new DownloadTask();
                    task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + "Calgary" + ",cad&appid=3ca4cb682481154e17368d817de04cb4");

                }catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
                }
            }
        };

// schedule the task to run starting now and then every hour...
        timer.schedule (hourlyTask, 0l, 1000*60*60);


    }

    // Connect to the data layer when the Activity starts
    @Override
    protected void onStart() {
        super.onStart();
        googleClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        WEARABLE_DATA_PATH = "/wearable_data";

        // Create a DataMap object and send it to the data layer
        dataMap = new DataMap();
        dataMap.putLong("time", new Date().getTime());
//        dataMap.putString("hole", "1");
//        dataMap.putString("front", "250");
//        dataMap.putString("middle", "260");
//        dataMap.putString("back", "270");
        //Requires a new thread to avoid blocking the UI

    }

    // Disconnect from the data layer when the Activity stops
    @Override
    protected void onStop() {
        if (null != googleClient && googleClient.isConnected()) {
            googleClient.disconnect();
        }
        super.onStop();
    }

    // Placeholders for required connection callbacks
    @Override
    public void onConnectionSuspended(int cause) { }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class SendToDataLayerThread extends Thread {
        String path;
        DataMap dataMap;

        // Constructor for sending data objects to the data layer
        SendToDataLayerThread(String p, DataMap data) {
            path = p;
            dataMap = data;
        }

        public void run() {
            // Construct a DataRequest and send over the data layer
            PutDataMapRequest putDMR = PutDataMapRequest.create(path);
            putDMR.getDataMap().putAll(dataMap);
            PutDataRequest request = putDMR.asPutDataRequest();
            DataApi.DataItemResult result = Wearable.DataApi.putDataItem(googleClient, request).await();
            if (result.getStatus().isSuccess()) {
                Log.v("myTag", "DataMap: " + dataMap + " sent successfully to data layer ");
            } else {
                // Log an error
                Log.v("myTag", "ERROR: failed to send DataMap to data layer");
            }
        }
    }


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;



            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                String message = "";

                JSONObject jsonObject = new JSONObject(result);

                String weatherInfo = jsonObject.getString("weather") + jsonObject.getString("main");;

                Log.i("weatherinfo", weatherInfo);

                double temp_minDou;
                double temp_maxDou;



                temp_minDou =  Double.parseDouble(jsonObject.getJSONObject("main").getString("temp_min"))  - 273.15;
                temp_maxDou = Double.parseDouble(jsonObject.getJSONObject("main").getString("temp_max")) -273.15 ;

                Integer tempMinInt = (int)temp_minDou;

                Integer tempMaxInt = (int)temp_maxDou;


                temp_min = String.valueOf(tempMinInt);
                temp_max = String.valueOf(tempMaxInt);

                Log.i("minTemp", temp_min);
                Log.i("maxTemp", temp_max);


                JSONArray arr = new JSONArray(weatherInfo);

                for (int i = 0; i < arr.length(); i++) {

                    JSONObject jsonPart = arr.getJSONObject(i);
                    String main = "";
                    String description = "";


                   weatherMain = jsonPart.getString("main");
                    weatherDescription = jsonPart.getString("description");
                    weatherId = jsonPart.getString("id");

                    Log.i("weatherId", weatherId);

                    dataMap.putString("weathermain", weatherMain);
                    dataMap.putString("weatherdescription", weatherDescription);
                    dataMap.putString("minTemp", temp_min);
                    dataMap.putString("maxTemp", temp_max);
                    dataMap.putString("id", weatherId);

                    new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap).start();

                    if(main!= "" && description != ""){
                        message += main + ": " + description + "\r\n";

                    }





                }


//                if(message != ""){
//                    resultTextView.setText(message);
//                } else {
//                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
//                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
            }



        }
    }
}
