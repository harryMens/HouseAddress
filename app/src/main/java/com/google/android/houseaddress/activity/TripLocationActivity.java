package com.google.android.houseaddress.activity;

import static com.google.android.houseaddress.constant.Constant.HOUSE_DATA;
import static com.google.android.houseaddress.constant.Constant.HOUSE_NUMBER;
import static com.google.android.houseaddress.constant.Constant.TRIP_DATA;
import static com.google.android.houseaddress.constant.Constant.TRIP_LOCATION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.houseaddress.R;
import com.google.android.houseaddress.utils.Decoration;
import com.google.android.houseaddress.utils.LocationAdapter;
import com.google.android.houseaddress.utils.LocationInfo;
import com.google.android.houseaddress.utils.Trip;
import com.google.android.houseaddress.utils.TripAdapter;

import java.util.List;

public class TripLocationActivity extends AppCompatActivity implements LocationAdapter.LocationViewHolder.TripLocationListener {

    private static final String TAG = "TripLocationActivity";
    LocationAdapter adapter;
    RecyclerView recyclerView;
    List<LocationInfo> locationInfo;
    Trip trip;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        recyclerView = findViewById(R.id.location_view);
        setRecyclerView();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra(TRIP_LOCATION)){
            trip = getIntent().getParcelableExtra(TRIP_LOCATION);
            adapter.setTripLocation(trip.getTrip());
            Log.d(TAG, "onCreate: checking trip "+trip.getTrip().get(0).getLatitude());
            getSupportActionBar().setTitle(trip.getTitle());
        }
    }

    private void setRecyclerView(){
        adapter = new LocationAdapter(this,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new Decoration(5));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void getPosition(int position) {
        trip = getIntent().getParcelableExtra(TRIP_LOCATION);
        Log.d(TAG, "getPosition: checkking trip "+trip.getTrip().get(position).getLatitude());
        LocationInfo in = trip.getTrip().get(0);
        Intent intent = new Intent(TripLocationActivity.this, TripInfoActivity.class);
        intent.putExtra(TRIP_DATA,trip.getTrip().get(position));
        intent.putExtra(HOUSE_DATA,trip.getAddress().get(position));
        intent.putExtra(HOUSE_NUMBER,position+1);
        startActivity(intent);
    }
}