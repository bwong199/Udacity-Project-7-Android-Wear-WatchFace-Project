This is Project 7 of 9 in the Android Nanodegree Program. This project focuses on Android Wearables/SmartWatches. 

The mandates of the project are as follow: 

App works on both round and square face watches.
App displays the current time.
App displays the high and low temperatures.
App displays a graphic that summarizes the day’s weather (e.g., a sunny image, rainy image, cloudy image, etc.)

I used the GPS location service to grab the user's current latitude and longitude and make a HTTP request to the OpenWeatherMap API using the latitude and longitude as query paramters to get the weather of the geolocation. 

I then use a DataMap to bundle the weather information and send it to the wearable device. 

I used this tutorial to learn how to transmit data between a mobile device and a wearable device - http://android-wear-docs.readthedocs.org/en/latest/sync.html. 
