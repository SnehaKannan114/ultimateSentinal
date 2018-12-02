package com.android.projects.ultimatesentinal;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PhoneOnMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_on_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        int lat, lon;
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            myLocation = new LatLng(15, 151);
            Toast.makeText(getApplicationContext(), "Sending Defaults" , Toast.LENGTH_LONG).show();
        }
        else {
            Intent mIntent = getIntent();
            int intValue = mIntent.getIntExtra("intVariableName", 0);

            lat = mIntent.getIntExtra("Latitude", 0);
            lon = mIntent.getIntExtra("Longitude", 0);

            //lat = extras.getString("Latitude");
            //lon = extras.getString("Longitude");
            myLocation = new LatLng(lat, lon);
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        //myLocation = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
    }
}