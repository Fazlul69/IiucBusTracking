package com.example.fazlulhoque.iiucbustracking.Student;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.example.fazlulhoque.iiucbustracking.Home;
import com.example.fazlulhoque.iiucbustracking.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.fazlulhoque.iiucbustracking.Driver.DriverMapsActivity.REQUEST_LOCATION_CODE;

public class StudentMapActivity extends FragmentActivity implements OnMapReadyCallback, android.location.LocationListener {

    private GoogleMap mMap;
    //GoogleApiClient mGoogleApiClient;

    //private LocationRequest mLocationRequest;
    //private Marker CurrentLocationMarker;
    //public static final int REQUEST_LOCATION_CODE=99;
    private Location mLastLocation;
    private LocationManager mLocationManager;
    private Button mLogout,distance;
    private ToggleButton mRequest;
    private LatLng pickupLocation;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private static final int RP_ACCESS_LOCATION = 1;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    private static final long MIN_TIME_BW_UPDATES = 500;
    private Boolean isLoggingout = false;

    private List<LatLng> latLngs;

    HashMap<String, LatLng> hashMap;

    String getName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_map);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        latLngs = new ArrayList<>();
        hashMap = new HashMap<>();

        mLogout = (Button) findViewById(R.id.sLogout);
        distance=(Button)findViewById(R.id.distance);
        mRequest = (ToggleButton) findViewById(R.id.request);
        mRequest.setText("Show Me");
        getDriversLocation();

        getName=getIntent().getExtras().getString("gender");

        distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(StudentMapActivity.this,MapDistance.class);
                startActivity(intent);
                finish();
                return;

            }
        });

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLoggingout=true;
                disconnectDriver();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(StudentMapActivity.this, Home.class);
                startActivity(intent);
                finish();
                return;

            }
        });

        mRequest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    //show user
                    compoundButton.setChecked(b);
                    mRequest.setTextOn("Hide Me");
                    getLocation();

                } else {
                    //hide user
                    compoundButton.setChecked(b);
                    mRequest.setTextOff("Show Me");
                    if (ActivityCompat.checkSelfPermission(StudentMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(StudentMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }else{
                        mLocationManager.removeUpdates(StudentMapActivity.this);
                    }
                    mLocationManager.removeUpdates(StudentMapActivity.this);
                    String  userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref= FirebaseDatabase.getInstance().getReference("studentRequest").child(userId);
                    ref.removeValue();
                }
            }
        });

    }

    private  int radius=1;
    private  Boolean driverFound=false;
    private String driverFoundID;
    private void getCloseatDriver()
    {
        DatabaseReference driverLocation=FirebaseDatabase.getInstance().getReference().child("DriverAvailable");
        GeoFire geoFire=new GeoFire(driverLocation);
        GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude,pickupLocation.longitude),radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!driverFound)
                {
                    driverFound=true;
                    driverFoundID=key;
                    getDriversLocation();
                    mRequest.setText("Looking for Driver location......");
                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(!driverFound)
                {
                    radius++;
                    getCloseatDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });



    }

    private void getDriversLocation() {
        DatabaseReference driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driverAvailable");

        driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mMap.clear();
                    hashMap.clear();
                    for (DataSnapshot eachDriver: dataSnapshot.getChildren()){
                        String driverId= eachDriver.getKey();
                        List<Object> map=(List<Object>) eachDriver.child("l").getValue();
                        Log.d("mapResultdrID",driverId);
                        Log.d("mapResult",map.get(0).toString());
                        double locationLat=0;
                        double locationLng=0;
                        if(map.get(0) !=null)
                        {
                            locationLat =Double.parseDouble(map.get(0).toString());
                        }

                        if(map.get(1) !=null)
                        {
                            locationLng=Double.parseDouble(map.get(1).toString());
                        }
                        LatLng driverLatlng=new LatLng(locationLat,locationLng);
                        latLngs.add(driverLatlng);
                        hashMap.put(driverId,driverLatlng);
                        mMap.addMarker(new MarkerOptions().position(new LatLng(driverLatlng.latitude,driverLatlng.longitude)).title(driverId));
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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



        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            mMap.setMyLocationEnabled(true);
        }


    }

    public boolean checkLocationPermission()
    {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE );
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE );
            }
            return false;
        }
        else
            return true;

    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d("locUpdate", String.valueOf(location.getLatitude()));
        mLastLocation = location;
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("studentRequest");
        String  userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        GeoFire geoFire=new GeoFire(ref);
        geoFire.setLocation(userId,new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()));
        LatLng pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(pickupLocation).title("Pickup Here");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pickupLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void getLocation() {
        mLocationManager = (LocationManager) StudentMapActivity.this.getSystemService(LOCATION_SERVICE);
        isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location location;

        if (!isGPSEnabled && !isNetworkEnabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(StudentMapActivity.this);
            alertDialog.setTitle("GPS Settings");
            alertDialog.setMessage("PS is not enabled. Do you want to go to settings menu?");
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    StudentMapActivity.this.startActivity(intent);
                }
            });

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            alertDialog.show();
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                        ActivityCompat.shouldShowRequestPermissionRationale(this,
                                android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    String[] perm = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
                    ActivityCompat.requestPermissions(this, perm,
                            RP_ACCESS_LOCATION);
                } else {
                    String[] perm = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
                    ActivityCompat.requestPermissions(this, perm,
                            RP_ACCESS_LOCATION);
                }
            } else {
                if (isNetworkEnabled) {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, (android.location.LocationListener) StudentMapActivity.this);

                    if (mLocationManager != null) {
                        location = mLocationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if(location!=null){
                            onLocationChanged(location);
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (mLastLocation== null) {
                        mLocationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, (android.location.LocationListener) StudentMapActivity.this);

                        if (mLocationManager != null) {
                            location = mLocationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if(location!=null){
                                onLocationChanged(location);
                            }
                        }
                    }
                }
            }
        }
    }


    private void disconnectDriver() {


        if (ActivityCompat.checkSelfPermission(StudentMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(StudentMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }else{
            mLocationManager.removeUpdates(StudentMapActivity.this);

        }

        mLocationManager.removeUpdates(StudentMapActivity.this);
        String  userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("studentRequest").child(userId);
        ref.removeValue();


    }



    @Override
    protected void onStop() {
        super.onStop();
        if(!isLoggingout)
        {
            disconnectDriver();
        }


    }
}
