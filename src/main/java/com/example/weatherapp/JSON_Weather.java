package com.example.weatherapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSON_Weather {
    //Переменные для считывания из JSON (Variables to read from JSON)
    // Актуальная погода (Actual weather)
    private int temp;
    private int temp_feels;
    private int pressure;
    private int humidity;
    private int clouds;
    private int wind_speed;
    private int wind_deg;
    private String description;
    private int id;
    //Прогноз погоды(Weather forecast)
    private int temp_day;
    private int temp_morn;
    private int temp_eve;
    private int temp_night;
    private int temp_feels_day;
    private int temp_feels_morn;
    private int temp_feels_eve;
    private int temp_feels_night;
    private int temp_min;
    private int temp_max;

    //Открытые методы для получения переменных (Public methods for getting variables)
    public int getTemp() { return temp; }
    public int getTemp_feels() {
        return temp_feels;
    }
    public int getPressure() {
        return pressure;
    }
    public int getHumidity() { return humidity; }
    public int getClouds() {
        return clouds;
    }
    public int getWind_speed(){
        return wind_speed;
    }
    public int getWind_deg(){ return wind_deg; }
    public String getDescription(){ return description; }
    public int getId(){ return id; }
    public int getTemp_day(){ return temp_day; }
    public int getTemp_morn(){ return temp_morn; }
    public int getTemp_eve(){ return temp_eve; }
    public int getTemp_night(){ return temp_night; }
    public int getTemp_min(){ return temp_min; }
    public int getTemp_max(){ return temp_max; }
    public int getTemp_feels_day(){ return temp_feels_day; }
    public int getTemp_feels_morn(){ return temp_feels_morn; }
    public int getTemp_feels_eve(){ return temp_feels_eve; }
    public int getTemp_feels_night(){ return temp_feels_night; }
//Получение JSON актуальной погоды(Getting JSON of the current weather)
public  JSONObject getActualWeather(JSONObject jsonObject) throws JSONException { return jsonObject.getJSONObject("current"); }
//Получение JSON погоды по часам(Getting weather JSON by hour)
public  JSONArray getHourlyWeather(JSONObject jsonObject) throws JSONException { return jsonObject.getJSONArray("hourly"); }
//Получение JSON погоды по дням(Getting JSON weather by day)
public  JSONArray getDaysWeather(JSONObject jsonObject) throws JSONException { return jsonObject.getJSONArray("daily"); }
    //Получение объекта актуальной погоды или по часам(Receiving an object of the current weather or by the hour)
    public  JSON_Weather getWeatherThisDayFromJson(JSONObject jsonObject){
        JSON_Weather p = new JSON_Weather();

        try{
            p.temp = jsonObject.getInt("temp");
            p.temp_feels = jsonObject.getInt("feels_like");
            p.pressure = jsonObject.getInt("pressure");
            p.humidity = jsonObject.getInt("humidity");
            p.clouds = jsonObject.getInt("clouds");
            p.wind_speed = jsonObject.getInt("wind_speed");
            p.wind_deg = jsonObject.getInt("wind_deg");
            p.id=jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
        }catch(JSONException e){
            e.printStackTrace();
            return null;
        }

        return p;
    }
    //Получение объекта погоды по дням(Getting a weather object by day)
    public  JSON_Weather getWeatherNextDaysFromJson(JSONObject jsonObject) {
        JSON_Weather p = new JSON_Weather();
            try {
                p.temp_day = jsonObject.getJSONObject("temp").getInt("day");
                p.temp_morn = jsonObject.getJSONObject("temp").getInt("morn");
                p.temp_min=jsonObject.getJSONObject("temp").getInt("min");
                p.temp_max=jsonObject.getJSONObject("temp").getInt("max");
                p.temp_eve = jsonObject.getJSONObject("temp").getInt("eve");
                p.temp_night = jsonObject.getJSONObject("temp").getInt("night");
                p.temp_feels_day = jsonObject.getJSONObject("feels_like").getInt("day");
                p.temp_feels_morn = jsonObject.getJSONObject("feels_like").getInt("morn");
                p.temp_feels_eve = jsonObject.getJSONObject("feels_like").getInt("eve");
                p.temp_feels_night = jsonObject.getJSONObject("feels_like").getInt("night");
                p.pressure = jsonObject.getInt("pressure");
                p.humidity = jsonObject.getInt("humidity");
                p.clouds = jsonObject.getInt("clouds");
                p.wind_speed = jsonObject.getInt("wind_speed");
                p.wind_deg = jsonObject.getInt("wind_deg");
                p.id= jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        return p;
    }
}
