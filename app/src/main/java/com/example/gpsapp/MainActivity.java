package com.example.gpsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public  static final int PERMISSIONS_MULTIPLE_REQUEST_CODE = 123;

    private TextView gpsLocation;
    private ImageView directionArrow;
    private LinearLayout enteredLocations;
    private Location liveLoc;
    private Location prevLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gpsLocation = findViewById(R.id.gps_location_text);
        directionArrow = findViewById(R.id.direction_arrow);
        enteredLocations = findViewById(R.id.entered_locations);

        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_MULTIPLE_REQUEST_CODE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_MULTIPLE_REQUEST_CODE);
        } else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
        }

        liveLoc = new Location("dummyprovider");
        prevLoc = new Location("dummyprovider");
    }

    public void addLocation(View view){
        EditText latitudeEntry = findViewById(R.id.latitude_entry);
        EditText longitudeEntry = findViewById(R.id.longitude_entry);

        Location location = new Location("dummyprovider");
        location.setLatitude(Double.parseDouble(latitudeEntry.getText().toString()));
        location.setLongitude(Double.parseDouble(longitudeEntry.getText().toString()));

        float distanceInMeters = liveLoc.distanceTo(location); // dystans z obencej pozycji do nowej
        float bearing = liveLoc.bearingTo(location); // pobieranie kierunku z obecnej pozycji do nowej

        //Tworzenie nowego LinealLayout
        LinearLayout innerLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutForInner = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        innerLayout.setLayoutParams(layoutForInner);
        innerLayout.setOrientation(LinearLayout.HORIZONTAL);

        //Tworzenie nowego TextView ktory bedzie pokazywal dystans do nowej lokalizacji
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        TextView distanceToEnteredLocation = new TextView(this);
        distanceToEnteredLocation.setText("Distance: " + distanceInMeters + "m");
        distanceToEnteredLocation.setLayoutParams(params);

        //Tworzenie nowego ImageView ktory bedzie pokazywal strzalke z kierunkiem do nowej lokalizacji
        ImageView directionToEnteredLocation = new ImageView(this);
        directionToEnteredLocation.setImageDrawable(this.getDrawable(R.drawable.arrow));
        directionToEnteredLocation.setRotation(bearing);
        directionToEnteredLocation.setLayoutParams(params);

        //Dodanie TextView i ImageView do nowego LinealLayout
        innerLayout.addView(distanceToEnteredLocation);
        innerLayout.addView(directionToEnteredLocation);

        //Dodanie nowego linearLayout do obecnego aby na ekranie pojawił się item.
        enteredLocations.addView(innerLayout);
    }

    LocationListener locationListener=new LocationListener() {

        @Override
        public void onLocationChanged(android.location.Location location) {
            prevLoc.setLatitude(liveLoc.getLatitude());
            prevLoc.setLongitude(liveLoc.getLongitude());
            liveLoc.setLatitude(location.getLatitude());
            liveLoc.setLongitude(location.getLongitude());

            // obliczanie predkosci
            float elapsedTimeInSeconds = 1; // sprawdzamy pozycje co sekunde
            float distanceInMeters = prevLoc.distanceTo(liveLoc); // dystans z poprzedniej pozycji do nowej
            liveLoc.setSpeed(distanceInMeters / elapsedTimeInSeconds);

            // pobieranie kierunku z poprzedniej pozycji do nowej
            float bearing = prevLoc.bearingTo(liveLoc);
            directionArrow.setRotation(bearing);

            String msg = String.format("New Latitude: %.2f\nNew Longitude: %.2f\nMovement speed: %.2f",
                    liveLoc.getLatitude(), liveLoc.getLongitude(), liveLoc.getSpeed());

            gpsLocation.setText(msg);
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
    };

}