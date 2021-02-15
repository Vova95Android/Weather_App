package com.example.weatherapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.Nullable;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class City_activity extends Activity {

    private static final String TAG = "1";
   private String[] cities = new String[5];
private String[] cities_id=new String[100];
private boolean button=false;
    PlacesClient placesClient;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_layout);
        // Инициализация SDK
        Places.initialize(getApplicationContext(), getString(R.string.api_key));
        placesClient = Places.createClient(City_activity.this);
        AutoCompleteTextView city_name= findViewById(R.id.autoCompleteTextView_city);
        city_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Прлучение выбранного города
            final String placeId = cities_id[position];
            final List<com.google.android.libraries.places.api.model.Place.Field> placeFields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, Place.Field.LAT_LNG);
            final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    Place place= response.getPlace();
                    if (place.getLatLng()!=null) {
                        Intent intent=new Intent(City_activity.this,MainActivity.class);
                        intent.putExtra(Static.LatLng,place.getLatLng().toString());
                        intent.putExtra(Static.City_name,cities[position]);
                        startActivity(intent);
                        button=true;
                        finish();
                    }

                    else {
                        Intent intent=new Intent(City_activity.this,MapsActivity.class);
                        intent.putExtra(Static.LatLng,"NULL");
                        startActivity(intent);
                    }

                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        final ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + exception.getMessage());
                        final int statusCode = apiException.getStatusCode();
                        // TODO: Handle error with given status code.
                    }
                });
            }
        });
        city_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length()>0) {
                    // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
                    // and once again when the user makes a selection (for example when calling fetchPlace()).
                    AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

                    // Билдер для поиска совпадений городов
                    FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                            .setSessionToken(token)
                            .setQuery(s.toString())
                            .setTypeFilter(TypeFilter.CITIES)
                            .build();
                    placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                        //Ответ о найденных городах
                        int ithem = 0;
                        for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                            cities[ithem] = prediction.getFullText(null).toString();
                            cities_id[ithem]= prediction.getPlaceId();
                            ithem++;
                        }
                        List<String> citiesList = Arrays.asList(cities);
                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<>(City_activity.this, R.layout.support_simple_spinner_dropdown_item, citiesList);
                        city_name.setAdapter(adapter);

                    })
                            .addOnFailureListener((exception) -> {
                                if (exception instanceof ApiException) {
                                    ApiException apiException = (ApiException) exception;
                                    String[] cities = {"Не найдено"};
                                    ArrayAdapter<String> adapter =
                                            new ArrayAdapter<>(City_activity.this, R.layout.support_simple_spinner_dropdown_item, cities);
                                    city_name.setAdapter(adapter);
                                }
                            });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    public void onBack(View view) {
        Intent intent=new Intent(City_activity.this,MainActivity.class);
        startActivity(intent);
        button=true;
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!button) {
            Intent intent = new Intent(City_activity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
