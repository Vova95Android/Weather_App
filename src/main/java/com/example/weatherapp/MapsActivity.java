package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 10;
    private GoogleMap mMap;
    private Marker Marker;
    private String position;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //Получение актуальных координат(Getting the actual coordinates)
        Bundle i=getIntent().getExtras();
        position=i.getString(Static.LatLng,"(47.5016, 35.0818)");
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng pos = new LatLng(Double.parseDouble(position.substring(position.indexOf("(")+1,position.indexOf(","))), Double.parseDouble(position.substring(position.indexOf(",")+1,position.indexOf(")"))));
        Marker = mMap.addMarker(new MarkerOptions()
                .position(pos)
                .title("Укажите координаты")
                .draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {}
            @Override
            public void onMarkerDrag(Marker marker) {}
            @Override
            public void onMarkerDragEnd(Marker marker) {
                position = marker.getPosition().toString();
            }
        });
    }
    //Сохранить выбранные координаты(Save selected coordinates)
    public void saveLatLng(View view) {
        Intent i = new Intent(MapsActivity.this, MainActivity.class);
        i.putExtra(Static.LatLng, position);
        i.putExtra(Static.City_name, "0");
        startActivity(i);
        finish();
    }

    public void onBack(View view) {
        Intent i = new Intent(MapsActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // разрешение было предоставлено (permission was granted)

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            if (location != null) {
                                LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                                position="("+ location.getLatitude()+"," + location.getLongitude() +")";
                                Marker.setPosition(pos);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                            }
                        });
            } else {
                // разрешение не было предоставлено (permission was not granted)
                Toast.makeText(this, "Please allow the use gps", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //Получение координат GPS(Getting GPS coordinates)
    public void getPos(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                        position="("+ location.getLatitude()+"," + location.getLongitude() +")";
                        Marker.setPosition(pos);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
                    }
                });
    }
}
