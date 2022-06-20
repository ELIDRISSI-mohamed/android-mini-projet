package com.example.myapplication;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class LocationActivity extends AppCompatActivity implements SensorEventListener {

    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView action;
    private ImageView activityIcon;

    double magnitudeP = 0, mPrevious=0;
    double magnitudeD, mDelta;
    float x, y, z;
    String uri = "@drawable-hdpi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        /**************** Activity *****/
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        action = (TextView) findViewById(R.id.idTextActivity);

        //activityIcon = findViewById(R.id.activityIcon);

        /**************** Get location in maps *****/
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(LocationActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(LocationActivity.this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }


    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    //clicked menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch ((item.getItemId())){
            case R.id.idEditProfile:
                startActivity(new Intent(LocationActivity.this, EditProfileActivity.class));
                return true;
            case R.id.idLogout:
                startActivity(new Intent(LocationActivity.this, MainActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Get current location
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if(location != null){
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            MarkerOptions options = new MarkerOptions().position(latLng).title("I am there");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                            googleMap.addMarker(options);
                        }
                    });
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    //Get actions
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            getAccelerometer(sensorEvent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener((SensorEventListener) this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                sensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void getAccelerometer(SensorEvent event){
        float[] values = event.values;
        x = values[0];
        y = values[1];
        z = values[2];

        int count = 0;

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        // activité : Sauter
        double d = Math.round(Math.sqrt(Math.pow(2, x) + Math.pow(2, y) + Math.pow(2, z)) - 2);
        float threshold_sauter =preferences.getFloat("sauter", 10);
        // activité : Marcher
        double Magnitude = Math.sqrt(Math.pow(2, x) + Math.pow(2, y) + Math.pow(2, z));
        magnitudeD = Magnitude - magnitudeP;
        magnitudeP = Magnitude;
        float threshold_marcher =preferences.getFloat("marcher", 5);
        // activité : Assis
        double m = Math.sqrt(Math.pow(2, x) + Math.pow(2, y) + Math.pow(2, z));
        mDelta = m - mPrevious;
        mPrevious = m;
        float threshold_assis =preferences.getFloat("assis", 1);

        if(d != 0 && d<=threshold_sauter){
            count=1;
        }else if(magnitudeD > threshold_marcher){
            count=2;
        }else if(mDelta > threshold_assis){
            count=3;
        }

        if (count == 1){
            action.setText("Activity : Jumped");
            action.invalidate();
        }else if (count == 2){
            action.setText("Activity : Running");
            action.invalidate();
        }else if(count == 3){
            action.setText("Activity : Standing");
            action.invalidate();
        }else if(count == 0 && z<4){
            action.setText("Activity : Standing");
            action.invalidate();
        }

    }
}
