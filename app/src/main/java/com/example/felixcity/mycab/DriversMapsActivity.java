package com.example.felixcity.mycab;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class DriversMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;

    private DatabaseReference DriverAvailabilityRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private Button LogoutDriverButton;
    private Button SettingsDriverButton;

    private Boolean currentLogoutDriverStatus = false ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_maps);

        DriverAvailabilityRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        LogoutDriverButton = (Button) findViewById(R.id.driver_logout_btn);
        SettingsDriverButton = (Button) findViewById(R.id.driver_settings_btn);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        LogoutDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentLogoutDriverStatus = true ;
                DisconnectTheDriver();

                mAuth.signOut();
                DriversLogout();
            }
        });

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        builGoogleApiClient();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
           if(getApplicationContext() !=null)
           {
               lastLocation = location;

               LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
               mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
               mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

               String userID= FirebaseAuth.getInstance().getCurrentUser().getUid();

               DatabaseReference DriverAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
               GeoFire geoFireAvailability = new GeoFire(DriverAvailabilityRef );

                DatabaseReference DriverworkingRef = FirebaseDatabase.getInstance().getReference().child("Drivers Working");
                GeoFire geoFireWorking = new GeoFire(DriverworkingRef);

               geoFireAvailability.setLocation(userID,new GeoLocation(location.getLatitude(),location.getLongitude()));
               geoFireWorking.setLocation(userID,new GeoLocation(location.getLatitude(),location.getLongitude()));
           }

    }

    protected synchronized void builGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();

       if(!currentLogoutDriverStatus)
       {
           DisconnectTheDriver();
       }
    }

    private void DisconnectTheDriver()
    {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DriverAvailabilityRef = FirebaseDatabase.getInstance().getReference().child("Drivers Available");
        GeoFire geoFire = new GeoFire(DriverAvailabilityRef);
        geoFire.removeLocation(userId);
    }

    private void DriversLogout()
    {
        Intent WelcomeIntent = new Intent(DriversMapsActivity.this,WelcomeActivity.class);
        WelcomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(WelcomeIntent);

        finish();
    }

}
