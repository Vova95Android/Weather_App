package com.example.weatherapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetWeather {

    //https://api.openweathermap.org/data/2.5/onecall?lat=47.5016&lon=35.0818&exclude=alerts,minutely&appid=2df8ef9f1f84d296670453486e84b504&units=metric&lang=ru
    @GET("onecall?.json")
    Call<String> weather(@Query("lat") String lat, @Query("lon") String lon, @Query("exclude") String exclude, @Query("appid") String appid, @Query("units") String units, @Query("lang") String lang);

}
