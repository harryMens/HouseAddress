package com.google.android.houseaddress.activity;

import static com.google.android.houseaddress.constant.Constant.TRIP_LOCATION;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.houseaddress.R;
import com.google.android.houseaddress.utils.Decoration;
import com.google.android.houseaddress.utils.Trip;
import com.google.android.houseaddress.utils.TripAdapter;
import com.google.android.houseaddress.viewModel.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TripActivity extends AppCompatActivity implements TripAdapter.TripViewHolder.TripListener {
    private static final String TAG = "TripActivity";
    RecyclerView recyclerView;
    TripAdapter adapter;
    MainViewModel viewModel;
    List<Trip> list = new ArrayList<>();
    Toolbar toolbar;

    boolean announce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        recyclerView = findViewById(R.id.trip_view);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setTitle("");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //This will try to create a trip to the list
                Intent intent = new Intent(TripActivity.this, NewTripActivity.class);
                startActivity(intent);
            }
        });

        setRecyclerView();
        getTrip();

    }

    private void setRecyclerView(){
         adapter = new TripAdapter(this,this);
         recyclerView.setLayoutManager(new LinearLayoutManager(this));
         recyclerView.addItemDecoration(new Decoration(5));
         recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                int position = viewHolder.getAdapterPosition();
                deleteTrip(list.get(position));
                list.remove(position);
                adapter.notifyDataSetChanged();

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    private void removeItem(int position) {
        // Remove the item from your data source
        // ...

        // Notify the adapter that an item has been removed
        deleteTrip(list.get(position));
        adapter.notifyItemRemoved(position);


    }

    @Override
    public void getPosition(int position) {
        Log.d(TAG, "getPosition: item clicked ");
        Intent intent = new Intent(TripActivity.this, TripLocationActivity.class);
        intent.putExtra(TRIP_LOCATION, list.get(position));
        startActivity(intent);

    }
    private void deleteTrip(Trip trip){
        viewModel.getDelete(trip).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

            }
        });
    }


    private void getTrip(){
        viewModel.getTrip().observe(this, new Observer<List<Trip>>() {
            @Override
            public void onChanged(List<Trip> trips) {
                if (trips != null && trips.size() > 0){
                    if (!announce) {
                        Log.e(TAG, "onChanged: size "+ trips.size());
                        Toast.makeText(TripActivity.this, "Swipe to the right to delete a trip", Toast.LENGTH_SHORT).show();
                        announce = true;
                    }
                    adapter.setTripList(trips);

                    if (list.size() >0){
                        list.clear();
                    }
                    list = trips;
                }
            }
        });
    }
}



