package com.androidweardocs.wearabledatamap;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DataMapActivity extends WearableActivity {

    private TextView mDateTV;
    private TextView mTimeTV;
    private TextView mMinTempTV;
    private TextView mMaxTempTV;
    private TextView mTextView;
    private ImageView mImageView;
    private String time;
    private Date dNow;
    private SimpleDateFormat ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_map);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mDateTV = (TextView) stub.findViewById(R.id.dateTV);
                mTimeTV = (TextView) stub.findViewById(R.id.timeTV);
                mMinTempTV = (TextView) stub.findViewById(R.id.minTemp);
                mMaxTempTV = (TextView) stub.findViewById(R.id.maxTemp);
                mImageView = (ImageView) stub.findViewById(R.id.imageView);

            }
        });

        setAmbientEnabled();

        // Register the local broadcast receiver
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        dNow = new Date( );
        ft =  new SimpleDateFormat ("E, MMM d, yyyy");
        Log.i("DateToday", ft.format(dNow));

        GregorianCalendar gcalendar = new GregorianCalendar();

        time = gcalendar.get(Calendar.HOUR) + ":" + gcalendar.get(Calendar.MINUTE) + ":" + gcalendar.get(Calendar.SECOND);

        Log.i("DateToday", time);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle data = intent.getBundleExtra("datamap");
            // Display received data in UI

            NumberFormat formatter = new DecimalFormat("#0.0");

            String display = "Received from the data Layer\n" +
//                    "Hole: " + data.getString("hole") + "\n" +
//                    "Front: " + data.getString("front") + "\n" +
//                    "Middle: "+ data.getString("middle") + "\n" +
//                    "Back: " + data.getString("back") + "\n" +
                    "WeatherMain: " + data.getString("weathermain") + "\n" +
                    "WeatherDescription: " +  data.getString("weatherdescription") + "\n" +
                    "Min Temp: " +  data.getString("minTemp") + "\n" +
                    "Max Temp: " +  data.getString("maxTemp") + "\n" +
                    "Weather Id: " + data.getString("id") ;

            int weatherImage = getArtResourceForWeatherCondition(Integer.parseInt(data.getString("id")));

            Log.i("weatherImage", String.valueOf(weatherImage));

            Picasso.with(getApplicationContext()).load(weatherImage).into(mImageView);

//            mTextView.setText(display);

            mTimeTV.setText(String.valueOf(time));
            mDateTV.setText(String.valueOf(ft.format(dNow))
                    );
            mMinTempTV.setText(data.getString("minTemp") + "°" );
            mMaxTempTV.setText(data.getString("maxTemp") + "°");
        }
    }

    public static int getArtResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.art_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.art_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.art_rain;
        } else if (weatherId == 511) {
            return R.drawable.art_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.art_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.art_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.art_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.art_storm;
        } else if (weatherId == 800) {
            return R.drawable.art_clear;
        } else if (weatherId == 801) {
            return R.drawable.art_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.art_clouds;
        }
        return -1;
    }



}

