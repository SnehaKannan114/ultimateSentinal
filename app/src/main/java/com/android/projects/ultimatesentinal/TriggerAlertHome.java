package com.android.projects.ultimatesentinal;

/**
 * Created by snehakannan on 27/11/18.
 */
import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class TriggerAlertHome extends AppCompatActivity implements LocationListener {
    private TextView latituteField;
    private TextView longitudeField;
    private LocationManager locationManager;
    private String provider;
    private String recoveryPH;
    Location location;
    Button mapMe;

    SharedPreferences prefs;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trigger_alert);
        prefs = getSharedPreferences(getApplicationContext().getString(R.string.app_name), Context.MODE_PRIVATE);

        recoveryPH = "9999999999";
        recoveryPH = prefs.getString("RecoveryNumber", "");
        Toast.makeText(getApplicationContext(), "Sending coordinates to " + recoveryPH , Toast.LENGTH_LONG).show();

        latituteField = (TextView) findViewById(R.id.TextView02);
        longitudeField = (TextView) findViewById(R.id.TextView04);

        mapMe = (Button)findViewById(R.id.mapMe);
        mapMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PhoneOnMap.class);
                i.putExtra("Latitude", Integer.parseInt(latituteField.getText().toString()));
                i.putExtra("Longitude", Integer.parseInt(longitudeField.getText().toString()));
                startActivity(i);
            }
        });

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if(!(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED))
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            latituteField.setText("Location not available");
            longitudeField.setText("Location not available");
        }
    }

    /* Request updates at startup*/
    @Override
    protected void onResume() {
        super.onResume();
        if(!(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED))
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        int lat = (int) (location.getLatitude());
        int lng = (int) (location.getLongitude());
        latituteField.setText(String.valueOf(lat));
        longitudeField.setText(String.valueOf(lng));
        String message = "Alert! You have sent recovery code to phone. Your " + Build.MANUFACTURER + " " + Build.MODEL +
                " is at latitude: " + lat + " - longitude : " + lng;
        SmsManager smsManager = SmsManager.getDefault();
        //Toast.makeText(getApplicationContext(), recoveryPH , Toast.LENGTH_LONG).show();
        smsManager.sendTextMessage(recoveryPH, null, message, null, null);
        startAlarmManager();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }
    public void startAlarmManager()
    {
        Intent startIntent = new Intent(getApplicationContext(), RingtonePlayingService.class);
        startService(startIntent);

        /*Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent startIntent = new Intent(getApplicationContext(), RingtonePlayingService.class);
                startService(startIntent);
                }
        };
        new Thread(runnable).start();*/
    }
}