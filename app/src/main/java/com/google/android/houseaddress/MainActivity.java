package com.google.android.houseaddress;

import static com.google.android.houseaddress.constant.Constant.CREATE_TRIP_DATA;
import static com.google.android.houseaddress.constant.Constant.FROM_MAP;
import static com.google.android.houseaddress.constant.Constant.VIEW_HOUSE;
import static com.google.android.houseaddress.network.Static.DRIVING;
import static com.google.android.houseaddress.network.Static.FIND_LOCATION;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.houseaddress.activity.NewTripActivity;
import com.google.android.houseaddress.activity.FindLocationActivity;
import com.google.android.houseaddress.activity.TripActivity;
import com.google.android.houseaddress.houseAddress.Address;
import com.google.android.houseaddress.houseAddress.HouseAddress;
import com.google.android.houseaddress.network.Repository;
import com.google.android.houseaddress.network.directions.Directions;
import com.google.android.houseaddress.network.directions.Segments;
import com.google.android.houseaddress.network.directions.Steps;
import com.google.android.houseaddress.utility.GO;
import com.google.android.houseaddress.utility.Geocode;
import com.google.android.houseaddress.utils.LocationInfo;
import com.google.android.houseaddress.utils.Trip;
import com.google.android.houseaddress.viewModel.MainViewModel;
import com.google.android.houseaddress.viewModel.MyViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends FragmentActivity implements
        OnMapReadyCallback, View.OnClickListener, View.OnTouchListener {
    private static final int LOCATION_SETTINGS_REQUEST = 100;
    private static final String TAG = "MainActivity";
    FusedLocationProviderClient fusedLocationClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    BottomNavigationView bottomNavigationView;
    Trip trip = new Trip();
    SupportMapFragment mapFragment;
    List<LocationInfo> tripLocationList = new ArrayList<>();
    List<Address> addressList = new ArrayList<>();
    Location myCurrentLocation;
    boolean touch;
    boolean entered = false;
    int one = 1;

    List<String> locationData = new ArrayList<>();
    LinearLayout leftLayout, rightLayout;
    MainViewModel viewModel;
    MyViewModel myViewModel;

    double latitude, longitude;
    double infoLatitude, infoLongitude;
    double infoAltitude, infoBearing;
    String infoTime;
    View mapFragmentView;
    ImageButton handBtn;
    boolean proceed = true;


    TextView viewLat, viewLong, viewAltitude, viewBearing;
    Button viewBtn;
    TextView viewLatR, viewLongR, viewAltitudeR, viewBearingR;
    Button viewBtnR;
    Drawable filledHand, defaultHand, moveHand;
    boolean firstDetection = false;


    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize the MapView
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.category_map);
        mapFragment.getMapAsync(this);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        requestPermissions();
        promptUser();
        getLastKnownLocation();
        gettingLastKnownLocation();
        setLocationCallback();
        startLocationUpdates();

        bottomNavigation();
        getLiveDirection();
        liveLoading();

        defaultHand = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_hand, null);
        filledHand = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_hand_fill, null);
        moveHand = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_move, null);
        receivingIntent();
        liveFollow();
    }

    void receivingIntent() {
        viewAltitude = findViewById(R.id.altitude);
        viewBearing = findViewById(R.id.bearing);
        viewBtn = findViewById(R.id.add_trip);
        viewLat = findViewById(R.id.latitude);
        viewLong = findViewById(R.id.longitude);
        viewAltitudeR = findViewById(R.id.altitude_right);
        viewBearingR = findViewById(R.id.bearing_right);
        viewBtnR = findViewById(R.id.add_trip_right);
        viewLatR = findViewById(R.id.latitude_right);
        viewLongR = findViewById(R.id.longitude_right);
        leftLayout = findViewById(R.id.left_view);
        rightLayout = findViewById(R.id.right_view);
        handBtn = findViewById(R.id.hand_btn);

        viewBtn.setOnClickListener(this);
        viewBtnR.setOnClickListener(this);
        handBtn.setOnClickListener(this);


        if (getIntent().hasExtra(CREATE_TRIP_DATA)) {
            handBtn.setImageDrawable(defaultHand);
            Trip trip1 = getIntent().getParcelableExtra(CREATE_TRIP_DATA);
            trip.setTitle(trip1.getTitle());
            trip.setDescription(trip1.getDescription());

        } else if (getIntent().hasExtra(FIND_LOCATION)) {
            Log.d(TAG, "receivingIntent: entered search");
            handBtn.setImageDrawable(moveHand);
        }
        else if (getIntent().hasExtra(VIEW_HOUSE)){
            bottomNavigationView.setVisibility(View.GONE);
            handBtn.setVisibility(View.GONE);
            LocationInfo info = getIntent().getParcelableExtra(VIEW_HOUSE);
            if (mMap != null) {
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                        new LatLng(info.getLatitude(), info.getLongitude()), 20f);
                mMap.animateCamera(update);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(info.getLatitude(),info.getLongitude()));
                mMap.addMarker(markerOptions);
            }


        }
        else {
            handBtn.setImageDrawable(defaultHand);
        }

    }

    void bottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.trip:
                    // Handle home item selection
                    if (proceed) {
                        handBtn.setImageDrawable(defaultHand);
                        Intent tripIntent = new Intent(MainActivity.this, TripActivity.class);
                        startActivity(tripIntent);
                    }
                    break;
                case R.id.home:
                    // Handle search item selection

                    break;
                case R.id.search:
                    // Handle profile item selection
                    if (proceed) {
                        handBtn.setImageDrawable(defaultHand);
                        Intent searchIntent = new Intent(MainActivity.this, FindLocationActivity.class);
                        startActivity(searchIntent);
                    }

                    break;
            }
            return true;
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mapFragmentView = mapFragment.getView();
        mapFragmentView.setOnTouchListener(MainActivity.this);
        if (getIntent().hasExtra(FIND_LOCATION)) {
            locationData = (List<String>) getIntent().getSerializableExtra(FIND_LOCATION);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom
                    (new LatLng(Double.parseDouble(locationData.get(1)), Double.parseDouble(locationData.get(2))),
                            10f);
            mMap.animateCamera(update);
        }

        receivingIntent();
    }

    void promptUser() {

        int locationInterval = 5000;
        int locationFastestInterval = 1000;
        int locationMaxWaitTime = 1000;
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, locationInterval)
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(locationFastestInterval)
                .setMaxUpdateDelayMillis(locationMaxWaitTime)
                .build();


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {


            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        MainActivity.this,
                                        LOCATION_SETTINGS_REQUEST);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void checkingLocationsEnabled(Boolean location, String locationName) {
        if (location != null && location) {
            Log.d(TAG, "MainActivity: camera enabled");
        } else {
            requestPermissions(new String[]{locationName}, LOCATION_SETTINGS_REQUEST);
        }
    }

    void requestPermissions() {
        ActivityResultLauncher<String[]> someActivityResultLauncher1 =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            Boolean fineLocation = result.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean courseLocation = result.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false);
                            Boolean backgroundLocation = result.getOrDefault(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION, false);

                            checkingLocationsEnabled(fineLocation, android.Manifest.permission.ACCESS_FINE_LOCATION);
                            checkingLocationsEnabled(courseLocation, android.Manifest.permission.ACCESS_COARSE_LOCATION);
                            //   checkingLocationsEnabled(backgroundLocation, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                        }
                    }
                });
        ActivityResultLauncher<Intent> someActivityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                            }
                        });

        someActivityResultLauncher1.launch(new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                //     Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            Log.d(TAG, "last known location, provider: %s, location: %s" + l +
                    l);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                Log.d(TAG, "found best last known location: %s" + l);
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    void setLocationCallback() {
        // Log.d(TAG, "setLocationCallback: 365456454545654");
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d(TAG, "onLocationResult: location results was null");
                    return;
                }
                Log.d(TAG, "onLocationResult: location results was not null");
                for (Location location : locationResult.getLocations()) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    myCurrentLocation = location;
                    Log.d(TAG, "onLocationResult: location results was not null " + latitude);

                    if (mMap != null && handBtn.getDrawable() == filledHand &&
                    viewModel.follow.getValue() != null && viewModel.follow.getValue() && !getIntent().hasExtra(VIEW_HOUSE)) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
                    }
                    if (!firstDetection) {
                        firstDetection = true;

                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLocation,
                                10f);
                        if (mMap != null) {
                            mMap.animateCamera(update);
                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,
                                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            mMap.setMyLocationEnabled(true);
                            mMap.isMyLocationEnabled();
                        }
                    }

                }
            }
        };

    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    void gettingLastKnownLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {

                if (getIntent().hasExtra(CREATE_TRIP_DATA)) {

                }
                else if(getIntent().hasExtra(FIND_LOCATION)){

                }
                else{

                }
//
                myCurrentLocation = location;

                Log.d(TAG, "gettingLastKnownLocation: location " + location);
            } else {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                Log.d(TAG, "gettingLastKnownLocation: location was null");
            }
        });


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mapFragmentView != null && handBtn.getDrawable() == filledHand) {
            mapFragmentView.dispatchTouchEvent(ev);
        } else {
            Log.d(TAG, "dispatchTouchEvent: view was null");
        }
        return super.dispatchTouchEvent(ev);
    }

    private void findPath(String mode, String start, String end) {
        myViewModel.repository.loading.setValue(true);
        myViewModel.getOpenDirections(mode, start, end);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hand_btn: {
                if (proceed) {
                    leftLayout.setVisibility(View.GONE);
                    rightLayout.setVisibility(View.GONE);
                    if (myCurrentLocation != null && handBtn.getDrawable() != moveHand) {
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude()),
                                17f);
                        mMap.animateCamera(update);
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                    }

                    LatLng netLocation;

                    if (handBtn.getDrawable() == defaultHand) {
                        handBtn.setImageDrawable(filledHand);
                        mMap.setMyLocationEnabled(true);
                        mMap.isMyLocationEnabled();
                        viewModel.follow.setValue(true);
                    }
                    else if (handBtn.getDrawable() == filledHand) {
                        handBtn.setImageDrawable(defaultHand);
                    } else {
                        handBtn.setImageDrawable(defaultHand);
                        netLocation = new LatLng(Double.parseDouble(locationData.get(1)), Double.parseDouble(locationData.get(2)));

                        String destination = locationData.get(2) + "," + locationData.get(1);
                        String begin = myCurrentLocation.getLongitude() + "," + myCurrentLocation.getLatitude();
                        findPath(DRIVING, begin, destination);
                    }


                }
                else{
                    Toast.makeText(this, "please wait", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case R.id.add_trip_right:
            case R.id.add_trip: {
                Log.d(TAG, "onClick: btn clicked");
                if (proceed) {
                    myViewModel.repository.loading.setValue(true);

                    LocationInfo info = new LocationInfo();

                    info.setLatitude(infoLatitude);
                    info.setLongitude(infoLongitude);
                    info.setTime(currentTime());
                    if (myCurrentLocation != null) {
                        info.setAltitude(myCurrentLocation.getAltitude());
                        info.setBearing(myCurrentLocation.getBearing());
                    }
                    String name = infoLatitude + "," + infoLongitude;
                    Log.d(TAG, "onClick: trapss1 " + info.getLatitude());
                    getAddress(infoLatitude, infoLongitude, info);

                    Log.d(TAG, "onClick: trapss2 " + info.getLatitude());


                }
            }
            break;

        }
    }

    private void getAddress(double latitude, double longitude, LocationInfo info){
        Repository.getInstance().getHouseAddress(latitude, longitude).enqueue(new Callback<HouseAddress>() {
            @Override
            public void onResponse(Call<HouseAddress> call, Response<HouseAddress> response) {
                if (response.body() != null) {
                   Address address = response.body().getFeatures().get(0).getProperties().getAddress();
                   addressList.add(address);
                    Log.e(TAG, "onResponse: adrress respondes "+address);

                    tripLocationList.add(info);
                   // viewModel.liveInfo.setValue();
                    myViewModel.repository.loading.setValue(false);
                }
                else {
                    myViewModel.repository.loading.setValue(false);
                    Log.d(TAG, "onResponse: checking erorr "+response.errorBody().toString());
                    Log.d(TAG, "onResponse: checking erorr "+response.code());
                }
            }

            @Override
            public void onFailure(Call<HouseAddress> call, Throwable t) {
                myViewModel.repository.loading.setValue(false);
                Log.e(TAG, "onFailure: retrofit failed "+t.getMessage() );
            }
        });

    }
    Double round(double number){
        number *= 1000000;
        number = Math.round(number);
        number /= 1000000;
        return  number;
    }
//

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (proceed) {
            one ++;
            viewModel.follow.setValue(false);
            touch = true;
            float x = event.getX();
            float y = event.getY();

            // Convert the screen coordinates to LatLng
            LatLng latLng = mMap.getProjection().fromScreenLocation(new Point((int) x, (int) y));

            infoLatitude = latLng.latitude;
            infoLongitude = latLng.longitude;

            int screenWidth = v.getWidth();
            float touchX = event.getX();

            if (touchX < screenWidth / 2) {
                // Left side of the screen touched
                Log.d(TAG, "onTouch: left clicked");

                leftLayout.setVisibility(View.GONE);
                rightLayout.setVisibility(View.VISIBLE);

                viewLatR.setText("Latitude: " + round(latLng.latitude));
                viewLongR.setText("Latitude: " + round(latLng.longitude));
                if (myCurrentLocation != null) {
                    viewBearingR.setText("Bearing: " + round(myCurrentLocation.getBearing()));
                    viewAltitudeR.setText("Altitude: " + round(myCurrentLocation.getAltitude()));
                    viewBearingR.setVisibility(View.VISIBLE);
                    viewAltitudeR.setVisibility(View.VISIBLE);
                } else {
                    viewBearingR.setVisibility(View.GONE);
                    viewAltitudeR.setVisibility(View.GONE);
                }

            } else {
                // Right side of the screen touched
                Log.d(TAG, "onTouch: right clicked");
                leftLayout.setVisibility(View.VISIBLE);
                rightLayout.setVisibility(View.GONE);

                viewLat.setText("Latitude: " + round(latLng.latitude));
                viewLong.setText("Latitude: " + round(latLng.longitude));
                if (myCurrentLocation != null) {
                    viewBearing.setText("Bearing: " + round(myCurrentLocation.getBearing()));
                    viewAltitude.setText("Altitude: " + round(myCurrentLocation.getAltitude()));
                    viewBearing.setVisibility(View.VISIBLE);
                    viewAltitude.setVisibility(View.VISIBLE);
                } else {
                    viewBearing.setVisibility(View.GONE);
                    viewAltitude.setVisibility(View.GONE);
                }
            }
            if (handBtn.getDrawable() == filledHand) {
                viewModel.liveLatLng.setValue(latLng);
            } else {
                Log.d(TAG, "onMapClick: not allowed");
            }

            if (handBtn.getDrawable() == filledHand){
                update(one);
            }
        }
        return false;
    }
    void update(int num){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (num == one) {
                    viewModel.follow.setValue(true);
                }
            }
        },5000);
    }
    private void insertTrip(Trip trip){
        viewModel.getInsert(trip).observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                Log.d(TAG, "onChanged: addressList Saved "+addressList.size());
                tripLocationList.clear();
                addressList.clear();
            }
        });
    }
    private void showStartDialog() {

        Log.d(TAG, "onClick: trapss "+tripLocationList.size());
        new AlertDialog.Builder(this)
                .setTitle("Trip")
                .setMessage(getString(R.string.save_desc))
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handBtn.setImageDrawable(defaultHand);
                        leftLayout.setVisibility(View.GONE);
                        rightLayout.setVisibility(View.GONE);
                        tripLocationList.clear();
                        addressList.clear();
                        dialog.dismiss();

                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getIntent().hasExtra(CREATE_TRIP_DATA) && ! entered){
                            entered = true;
                            Log.e(TAG, "onClick: tracks "+addressList.size());
                            trip.setTrip(tripLocationList);
                            trip.setAddress(addressList);
                            handBtn.setImageDrawable(defaultHand);
                            leftLayout.setVisibility(View.GONE);
                            rightLayout.setVisibility(View.GONE);
                            if (trip.getTrip().size()>0 && trip.getAddress().size() >0 ){
                                insertTrip(trip);
                            }

                        }
                        else{
                            Log.d(TAG, "onClick: tracks "+tripLocationList.get(0).getLatitude());

                            trip.setTrip(tripLocationList);
                            trip.setAddress(addressList);
                            handBtn.setImageDrawable(defaultHand);
                            leftLayout.setVisibility(View.GONE);
                            rightLayout.setVisibility(View.GONE);
                            Intent intent = new Intent(MainActivity.this, NewTripActivity.class);
                            intent.putExtra(FROM_MAP, trip);
                            startActivity(intent);
                            tripLocationList.clear();
                            addressList.clear();

                        }

                        dialog.dismiss();
                    }
                })
                .create().show();

    }

    @Override
    public void onBackPressed() {
        if (leftLayout.getVisibility() == View.VISIBLE || rightLayout.getVisibility() == View.VISIBLE){
            leftLayout.setVisibility(View.GONE);
            rightLayout.setVisibility(View.GONE);
        }
        else if (tripLocationList.size() > 0){
            showStartDialog();

        }
        else {
            super.onBackPressed();
        }
    }

    private String currentTime(){
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        return dateFormat.format(new Date(currentTimeMillis));
    }

    @Override
    protected void onResume() {
        bottomNavigationView.setSelectedItemId(R.id.home);
        leftLayout.setVisibility(View.GONE);
        rightLayout.setVisibility(View.GONE);
        super.onResume();
    }
    private void getLiveDirection(){
        myViewModel.getDirectionsLive().observe(this, new Observer<Directions>() {
            @Override
            public void onChanged(Directions directions) {

                if (directions != null && getIntent().hasExtra(FIND_LOCATION)){
                    PolylineOptions polylineOptions = new PolylineOptions();
//                    List<Double> latitude = new ArrayList<>();
//                    List<Double> longitude = new ArrayList<>();
                    List<List<Double>> coordinates = directions.getFeatures().get(0).getGeometry().getCoordinates();
                    Segments segments = directions.getFeatures().get(0).getProperties().getSegments().get(0);
                    List<Steps> steps = segments.getSteps();

                    for (int i = 0; i < coordinates.size(); i++) {
                        polylineOptions.add(new LatLng(coordinates.get(i).get(1),coordinates.get(i).get(0)));
//                        latitude.add(coordinates.get(i).get(1));
//                        longitude.add(coordinates.get(i).get(0));
                    }

                    polylineOptions.color(Color.GRAY);
                    polylineOptions.width(10f);

                    // Add the polyline to the map
                    if (mMap != null) {
                        mMap.addPolyline(polylineOptions);
                    }

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude()));
                    builder.include(polylineOptions.getPoints().get(polylineOptions.getPoints().size()-1));
                    LatLngBounds bounds = builder.build();

                    // Set a padding to create space around the bounding box (optional)
                    int padding = 100; // in pixels

                    // Animate the camera to the bounding box with padding
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.animateCamera(cameraUpdate);
                }
            }
        });
    }
    private void liveLoading(){
        ProgressBar progressBar = findViewById(R.id.progress_circular);
        myViewModel.repository.loading.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean != null){
                    if (aBoolean){
                        proceed = false;
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    else{
                        proceed = true;
                       progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
    private void liveFollow(){
        viewModel.follow.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!aBoolean){

                }
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        leftLayout.setVisibility(View.GONE);
        rightLayout.setVisibility(View.GONE);
    }
}




