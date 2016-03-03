package com.androidweardocs.wearabledatamap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class DataMapActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleApiClient googleClient;
    String weatherMain;
    String weatherDescription;
    String weatherId;
    DataMap dataMap;
    String WEARABLE_DATA_PATH;

    String temp_min;
    String temp_max;

    LocationManager locationManager;
    String provider;

    private Date dNow;
    private SimpleDateFormat ft;
    DateFormat timeFormat;

    TextView latTV;
    TextView lngTV;
    TextView accuracyTV;
    TextView speedTV;
    TextView bearingTV;
    TextView altitudeTV;
    TextView addressTV;
    TextView forecastTV;
    TextView minTempTV;
    TextView maxTempTV;

    Double lat;
    Double lng;
    Double alt;
    Float bearing;
    Float speed;
    Float accuracy;

    Location location;


    PutDataMapRequest putDataMapReq;


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

        latTV = (TextView) findViewById(R.id.lat);
        lngTV = (TextView) findViewById(R.id.lng);
        accuracyTV = (TextView) findViewById(R.id.accuracy);
        speedTV = (TextView) findViewById(R.id.speed);
        bearingTV = (TextView) findViewById(R.id.bearing);
        altitudeTV = (TextView) findViewById(R.id.altitude);
        addressTV = (TextView) findViewById(R.id.address);

        forecastTV = (TextView) findViewById(R.id.forecastTV);
        minTempTV = (TextView) findViewById(R.id.minTempTV);
        maxTempTV = (TextView) findViewById(R.id.maxTempTV);


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0.0f, this);

        provider = locationManager.getBestProvider(new Criteria(), false);


        location = locationManager.getLastKnownLocation(provider);

        onLocationChanged(location);


//        Timer timer = new Timer();
//
//        timer.schedule(new TimerTask() {
//            public void run() {
//
//                onLocationChanged(location);
//
//                DownloadTask task = new DownloadTask();
//
//                String query = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat+"&lon=" + lng+"&appid=44db6a862fba0b067b1930da0d769e98";
////                    task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + "Calgary" + ",cad&appid=3ca4cb682481154e17368d817de04cb4");
//                task.execute("http://api.openweathermap.org/data/2.5/weather?lat=" + lat+"&lon=" + lng+"&appid=44db6a862fba0b067b1930da0d769e98");
//                Log.i("URL", query);
//            }
//        }, 0, 60 * 1000 * 60);

    }

    private void updateWeather() {
        try {
            onLocationChanged(location);

            DownloadTask task = new DownloadTask();

            String query = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lng + "&appid=44db6a862fba0b067b1930da0d769e98";
//                    task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + "Calgary" + ",cad&appid=3ca4cb682481154e17368d817de04cb4");
            task.execute("http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lng + "&appid=44db6a862fba0b067b1930da0d769e98");
            Log.i("URL", query);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
        }
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


        String message = "Hello wearable\n Via the data layer";
        //Requires a new thread to avoid blocking the UI

        googleClient.connect();

        Log.i("myTag", "onConnected");





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
    public void onConnectionSuspended(int cause) {
        Log.i("myTag", "on Connection Suspended");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.i("myTag", "on Connection Failed");
    }




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
        if (id == R.id.refresh) {

            updateWeather();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            alt = location.getAltitude();
            bearing = location.getBearing();
            speed = location.getSpeed();
            accuracy = location.getAccuracy();


            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());


            List<Address> listAddresses = null;
            try {
                listAddresses = geocoder.getFromLocation(lat, lng, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (listAddresses != null && listAddresses.size() > 0) {

                Log.i("PlaceInfo", listAddresses.get(0).toString());

                String addressHolder = "";

                for (int i = 0; i <= listAddresses.get(0).getMaxAddressLineIndex(); i++) {

                    addressHolder += listAddresses.get(0).getAddressLine(i) + "\n";

                }

                addressTV.setText("Address:\n" + addressHolder);

            }

            NumberFormat formatter = new DecimalFormat("#0.00");
            lat = Double.valueOf(formatter.format(lat));
            lng = Double.valueOf(formatter.format(lng));

            latTV.setText("Latitude: " + lat.toString());
            lngTV.setText("Longitude: " + lng.toString());
            altitudeTV.setText("Altitude: " + alt.toString() + "m");
            bearingTV.setText("Bearing: " + bearing.toString());
            speedTV.setText("Speed: " + speed.toString() + "m/s");
            accuracyTV.setText("Accuracy: " + accuracy.toString() + "m");


            Log.i("Latitude", String.valueOf(lat));
            Log.i("Longitude", String.valueOf(lng));
            Log.i("altitude", String.valueOf(alt));
            Log.i("bearing", String.valueOf(bearing));
            Log.i("speed", String.valueOf(speed));
            Log.i("accuracy", String.valueOf(accuracy));
        } else {
            Toast.makeText(getApplicationContext(), "Location cannot be found", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onPause() {
        super.onPause();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
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

                String weatherInfo = jsonObject.getString("weather") + jsonObject.getString("main");
                ;

                Log.i("weatherinfo", weatherInfo);

                double temp_minDou;
                double temp_maxDou;


                temp_minDou = Double.parseDouble(jsonObject.getJSONObject("main").getString("temp_min")) - 273.15;
                temp_maxDou = Double.parseDouble(jsonObject.getJSONObject("main").getString("temp_max")) - 273.15;

                Integer tempMinInt = (int) temp_minDou;

                Integer tempMaxInt = (int) temp_maxDou;


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

                    forecastTV.setText("Forecast: " + weatherDescription);
                    minTempTV.setText("Low: " + temp_min+ "°");
                    maxTempTV.setText("High: " + temp_max+ "°");

                    //send data via DataLayer
//                    new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap).start();

                    //Using Put DataMap Request
                    putDataMapReq = PutDataMapRequest.create("/data");
                    Log.i("weatherDescription", weatherDescription);
                    putDataMapReq.getDataMap().putString("weatherDescription", weatherDescription);

                    putDataMapReq.getDataMap().putLong("current_time", Long.parseLong(Long.toString(System.currentTimeMillis())));



                    PutDataRequest putDataReq = putDataMapReq.asPutDataRequest().setUrgent();
                    Wearable.DataApi.putDataItem(googleClient, putDataReq);

                    Wearable.DataApi.putDataItem(googleClient, putDataReq)
                            .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                                @Override
                                public void onResult(DataApi.DataItemResult dataItemResult) {
                                    Log.d("myTag Mobile", "putDataItem status: "
                                            + dataItemResult.getStatus().toString());
                                }
                            });

//                    putDataMapReq = PutDataMapRequest.create("/data").setUrgent();
//                    PendingResult<DataApi.DataItemResult> pendingResult =
//                            Wearable.DataApi.putDataItem(googleClient, putDataReq);





                    if (main != "" && description != "") {
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
