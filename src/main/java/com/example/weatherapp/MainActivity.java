package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity {
    private String[] day_week={"ВС","ПН","ВТ","СР","ЧТ","ПТ","СБ"};
    public static final String APP_PREFERENCES = "save_sity";
    private SharedPreferences save_city;
    private JSONObject mainObject;
    private JSON_Weather actual=new JSON_Weather();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        save_city=getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Bundle i=getIntent().getExtras();
        //Получение координат(Getting coordinates)
        if ((i!=null)&&(i.containsKey(Static.LatLng))&&(i.containsKey(Static.City_name))){
            TextView t=findViewById(R.id.textView_cityName);
            if (Objects.equals(i.getString(Static.City_name), "0")) t.setText(Objects.requireNonNull(i.getString(Static.LatLng)).substring(Objects.requireNonNull(i.getString(Static.LatLng)).indexOf("(")+1, Objects.requireNonNull(i.getString(Static.LatLng)).indexOf(")")));
            else t.setText(i.getString(Static.City_name));
            SharedPreferences.Editor editor=save_city.edit();
            editor.putString(Static.LatLng,i.getString(Static.LatLng));
            editor.putString(Static.City_name,i.getString(Static.City_name));
            editor.apply();
        }
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        DateTimeFormatter formatter1= DateTimeFormatter.ofPattern("dd MMM");
        LocalDate time1 = LocalDate.now();
        String date1=time1.format(formatter1);
        TextView t_date=findViewById(R.id.textView_date);
        t_date.setText(day_week[dayOfWeek-1]+", "+date1);
        if (!save_city.getString(Static.City_name,"0").equals("0")){TextView t=findViewById(R.id.textView_cityName); t.setText(save_city.getString(Static.City_name,"0"));}
        Retrofit retrofit = new Retrofit.Builder()                      //Создаие билдера для GET запросов (Creating a builder for GET requests)
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        Call<String> messages;
        GetWeather getWeather = retrofit.create(GetWeather.class);
        String temp=save_city.getString(Static.LatLng,"0");
        String lat;
        String lng;
        if (save_city.getString(Static.LatLng, "0").equals("0")) {lat="0";lng="0";}
        else{
            lat = temp.substring(temp.indexOf("(") + 1, temp.indexOf(","));
            lng = temp.substring(temp.indexOf(",") + 1, temp.indexOf(")"));
        }
        messages=getWeather.weather(lat,lng,"alerts,minutely",getString(R.string.api_wheather_id),"metric","ru");
        messages.enqueue(new Callback<String>() {       //Обратный вызов запроса (Request callback)
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try{
                    assert response.body() != null;
                    mainObject = new JSONObject(response.body());
                    //Получение массива сообщений json (Get the posts json array)
                    DateTimeFormatter formatter= DateTimeFormatter.ofPattern("HH");
                    LocalTime time = LocalTime.now();
                    String date=time.format(formatter);
                    //Получение актуальной погоды(Getting current weather)
                    int hour=Integer.parseInt(date);
                    JSONObject actual_json=actual.getActualWeather(mainObject);
                    JSON_Weather actual_weather=actual.getWeatherThisDayFromJson(actual_json);
                    TextView t=findViewById(R.id.textView_actual_temp);
                    t.setText(actual_weather.getTemp() +"°");
                    t=findViewById(R.id.textView_actual_wind_speed);
                    t.setText(actual_weather.getWind_speed()+"м/сек");
                    t=findViewById(R.id.textView_actualHum);
                    t.setText(actual_weather.getHumidity()+"%");
                    ImageView i=findViewById(R.id.imageView_main);
                    if(actual_weather.getId()<300) {if((hour<18)&&(hour>5)) i.setImageDrawable(getDrawable(R.drawable.ic_white_day_thunder)); else i.setImageDrawable(getDrawable(R.drawable.ic_white_night_thunder));}
                    else if (actual_weather.getId()<500) {if((hour<18)&&(hour>5)) i.setImageDrawable(getDrawable(R.drawable.ic_white_day_rain)); else i.setImageDrawable(getDrawable(R.drawable.ic_white_night_rain));}
                    else if (actual_weather.getId()<700) {if((hour<18)&&(hour>5)) i.setImageDrawable(getDrawable(R.drawable.ic_white_day_shower)); else i.setImageDrawable(getDrawable(R.drawable.ic_white_night_shower));}
                    else if (actual_weather.getId()==800) {if((hour<18)&&(hour>5)) i.setImageDrawable(getDrawable(R.drawable.ic_white_day_bright)); else i.setImageDrawable(getDrawable(R.drawable.ic_white_night_bright));}
                    else if (actual_weather.getId()<900) {if((hour<18)&&(hour>5)) i.setImageDrawable(getDrawable(R.drawable.ic_white_day_cloudy)); else i.setImageDrawable(getDrawable(R.drawable.ic_white_night_cloudy));}
                    ImageView deg=findViewById(R.id.imageView_actual_win_deg);
                    if (actual_weather.getWind_deg()<45) {deg.setImageDrawable(getDrawable(R.drawable.ic_icon_wind_e));}
                    else if (actual_weather.getWind_deg()<90) {deg.setImageDrawable(getDrawable(R.drawable.ic_icon_wind_se));}
                    else if (actual_weather.getWind_deg()<135) {deg.setImageDrawable(getDrawable(R.drawable.ic_icon_wind_s));}
                    else if (actual_weather.getWind_deg()<180) {deg.setImageDrawable(getDrawable(R.drawable.ic_icon_wind_ws));}
                    else if (actual_weather.getWind_deg()<225) {deg.setImageDrawable(getDrawable(R.drawable.ic_icon_wind_w));}
                    else if (actual_weather.getWind_deg()<270) {deg.setImageDrawable(getDrawable(R.drawable.ic_icon_wind_wn));}
                    else if (actual_weather.getWind_deg()<315) {deg.setImageDrawable(getDrawable(R.drawable.ic_icon_wind_n));}
                    else if (actual_weather.getWind_deg()<360) {deg.setImageDrawable(getDrawable(R.drawable.ic_icon_wind_ne));}


                    setDay_weather(0);


                } catch(JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }


    //Получение прогноза погоды(Getting weather forecast)
    void setDay_weather(int numb) throws JSONException {
        DateTimeFormatter formatter= DateTimeFormatter.ofPattern("HH");
        LocalTime time = LocalTime.now();
        String date=time.format(formatter);
        //Получение прогноза погоды по часам(Getting weather forecast by the hour)
        int hour=Integer.valueOf(date);
        LinearLayout layout_weather_hour=findViewById(R.id.layout_day_temp);
        layout_weather_hour.removeAllViews();
        layout_weather_hour.setBackgroundColor(getColor(R.color.colorBackgraund2));
        if (numb==0){
            JSONArray hour_json=actual.getHourlyWeather(mainObject);
            for (int count_hour=0;count_hour<24-hour;count_hour++){
                JSON_Weather weather_hour=actual.getWeatherThisDayFromJson(hour_json.getJSONObject(count_hour));
                TextView temp_hour=new TextView(MainActivity.this);
                temp_hour.setTextColor(getColor(R.color.colorText));
                temp_hour.setText(weather_hour.getTemp() +"°");
                temp_hour.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                TextView time_hour=new TextView(MainActivity.this);
                time_hour.setTextColor(getColor(R.color.colorText));
                time_hour.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                time_hour.setText(Integer.toString(hour + count_hour));
                ImageView i_count=new ImageView(MainActivity.this);
                if(weather_hour.getId()<300) {if((hour+count_hour<18)&&(hour+count_hour>5)) i_count.setImageDrawable(getDrawable(R.drawable.ic_white_day_thunder)); else i_count.setImageDrawable(getDrawable(R.drawable.ic_white_night_thunder));}
                else if (weather_hour.getId()<500) {if((hour+count_hour<18)&&(hour+count_hour>5)) i_count.setImageDrawable(getDrawable(R.drawable.ic_white_day_rain)); else i_count.setImageDrawable(getDrawable(R.drawable.ic_white_night_rain));}
                else if (weather_hour.getId()<700) {if((hour+count_hour<18)&&(hour+count_hour>5)) i_count.setImageDrawable(getDrawable(R.drawable.ic_white_day_shower)); else i_count.setImageDrawable(getDrawable(R.drawable.ic_white_night_shower));}
                else if (weather_hour.getId()==800) {if((hour+count_hour<18)&&(hour+count_hour>5)) i_count.setImageDrawable(getDrawable(R.drawable.ic_white_day_bright)); else i_count.setImageDrawable(getDrawable(R.drawable.ic_white_night_bright));}
                else if (weather_hour.getId()<900) {if((hour+count_hour<18)&&(hour+count_hour>5)) i_count.setImageDrawable(getDrawable(R.drawable.ic_white_day_cloudy)); else i_count.setImageDrawable(getDrawable(R.drawable.ic_white_night_cloudy));}
                LinearLayout layout=new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(time_hour);
                layout.addView(i_count);
                layout.addView(temp_hour);
                layout_weather_hour.addView(layout);
            }
        }
        else {
            JSONArray hour_json = actual.getDaysWeather(mainObject);
            JSON_Weather weather_hour = actual.getWeatherNextDaysFromJson(hour_json.getJSONObject(numb));
            for (int count_hour = 0; count_hour < 4; count_hour++) {
                TextView temp_hour = new TextView(MainActivity.this);
                temp_hour.setTextColor(getColor(R.color.colorText));
                temp_hour.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                TextView time_hour = new TextView(MainActivity.this);
                time_hour.setTextColor(getColor(R.color.colorText));
                time_hour.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                switch (count_hour){
                    case 0:{time_hour.setText(Integer.toString(2)); temp_hour.setText(weather_hour.getTemp_night() + "°");}break;
                    case 1:{time_hour.setText(Integer.toString(8)); temp_hour.setText(weather_hour.getTemp_morn() + "°");}break;
                    case 2:{time_hour.setText(Integer.toString(14)); temp_hour.setText(weather_hour.getTemp_day() + "°");}break;
                    case 3:{time_hour.setText(Integer.toString(20)); temp_hour.setText(weather_hour.getTemp_eve() + "°");}break;
                }
                ImageView i_count = new ImageView(MainActivity.this);
                if (weather_hour.getId() < 300) {
                    if ((hour + count_hour < 18) && (hour + count_hour > 5))
                        i_count.setImageDrawable(getDrawable(R.drawable.ic_white_day_thunder));
                    else i_count.setImageDrawable(getDrawable(R.drawable.ic_white_night_thunder));
                } else if (weather_hour.getId() < 500) {
                    if ((hour + count_hour < 18) && (hour + count_hour > 5))
                        i_count.setImageDrawable(getDrawable(R.drawable.ic_white_day_rain));
                    else i_count.setImageDrawable(getDrawable(R.drawable.ic_white_night_rain));
                } else if (weather_hour.getId() < 700) {
                    if ((hour + count_hour < 18) && (hour + count_hour > 5))
                        i_count.setImageDrawable(getDrawable(R.drawable.ic_white_day_shower));
                    else i_count.setImageDrawable(getDrawable(R.drawable.ic_white_night_shower));
                } else if (weather_hour.getId() == 800) {
                    if ((hour + count_hour < 18) && (hour + count_hour > 5))
                        i_count.setImageDrawable(getDrawable(R.drawable.ic_white_day_bright));
                    else i_count.setImageDrawable(getDrawable(R.drawable.ic_white_night_bright));
                } else if (weather_hour.getId() < 900) {
                    if ((hour + count_hour < 18) && (hour + count_hour > 5))
                        i_count.setImageDrawable(getDrawable(R.drawable.ic_white_day_cloudy));
                    else i_count.setImageDrawable(getDrawable(R.drawable.ic_white_night_cloudy));
                }
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(time_hour);
                layout.addView(i_count);
                layout.addView(temp_hour);
                layout_weather_hour.addView(layout);
            }
        }
        //Получение прогноза погоды по дням(Getting weather forecast by day)
        LinearLayout layout_7days=findViewById(R.id.layout_7day_temp);
        layout_7days.removeAllViews();
        JSONArray days_json=actual.getDaysWeather(mainObject);
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        for (int count_days=0;count_days<7;count_days++){
            JSON_Weather weather_days=actual.getWeatherNextDaysFromJson(days_json.getJSONObject(count_days));
            TextView t_week_day=new TextView(MainActivity.this);
            TextView t_temp_days=new TextView(MainActivity.this);
            LinearLayout layout=new LinearLayout(MainActivity.this);



            t_week_day.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            t_week_day.setWidth(100);
            t_week_day.setPadding(0,10,0,0);
            if(dayOfWeek-1+count_days<7) t_week_day.setText(day_week[dayOfWeek-1+count_days]);
            else  t_week_day.setText(day_week[count_days-dayOfWeek-1]);
            if (count_days==numb) t_week_day.setTextColor(getColor(R.color.colorBackgraund2));
            else t_week_day.setTextColor(getColor(R.color.colorTextBlack));

            t_temp_days.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            t_temp_days.setWidth(300);
            t_temp_days.setPadding(0,10,0,0);
            t_temp_days.setText(weather_days.getTemp_max()+"°/"+weather_days.getTemp_min()+"°");
            if (count_days==numb) t_temp_days.setTextColor(getColor(R.color.colorBackgraund2));
            else t_temp_days.setTextColor(getColor(R.color.colorTextBlack));

            ImageView i_count=new ImageView(MainActivity.this);
            if(weather_days.getId()<300) {if (count_days!=numb) i_count.setImageDrawable(getDrawable(R.drawable.ic_black_day_thunder)); else i_count.setImageDrawable(getDrawable(R.drawable.ic_blue_day_thunder)); }
            else if (weather_days.getId()<500) {if (count_days!=numb) i_count.setImageDrawable(getDrawable(R.drawable.ic_black_day_rain)); else i_count.setImageDrawable(getDrawable(R.drawable.ic_blue_day_rain));}
            else if (weather_days.getId()<700) {if (count_days!=numb) i_count.setImageDrawable(getDrawable(R.drawable.ic_black_day_shower)); else i_count.setImageDrawable(getDrawable(R.drawable.ic_blue_day_shower));}
            else if (weather_days.getId()==800) {if (count_days!=numb) i_count.setImageDrawable(getDrawable(R.drawable.ic_black_day_bright)); else i_count.setImageDrawable(getDrawable(R.drawable.ic_blue_day_bright));}
            else if (weather_days.getId()<900) {if (count_days!=numb) i_count.setImageDrawable(getDrawable(R.drawable.ic_black_day_cloudy)); else i_count.setImageDrawable(getDrawable(R.drawable.ic_blue_day_cloudy));}
            layout.setOrientation(LinearLayout.HORIZONTAL);
            int finalCount_days = count_days;
            //Слушатель нажатия на день
            layout.setOnClickListener(v -> {
                try {
                    setDay_weather(finalCount_days);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
            layout.addView(t_week_day);
            layout.addView(t_temp_days);
            layout.addView(i_count);
            layout_7days.addView(layout);
        }
    }

    //Получение города(Getting a city)
    public void getNewCity(View view) {
        Intent i =new Intent(MainActivity.this,City_activity.class);
        startActivity(i);
        finish();
    }

    //Получение координат(Getting coordinates)
    public void getLocation(View view) {
        Intent i =new Intent(MainActivity.this,MapsActivity.class);
        i.putExtra(Static.LatLng,save_city.getString(Static.LatLng,"(47.5016, 35.0818)"));
        startActivity(i);
        finish();
    }
}
