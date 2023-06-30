package com.google.android.houseaddress.activity;

import static com.google.android.houseaddress.constant.Constant.CREATE_TRIP_DATA;
import static com.google.android.houseaddress.constant.Constant.FROM_MAP;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.houseaddress.MainActivity;
import com.google.android.houseaddress.R;
import com.google.android.houseaddress.utils.Trip;
import com.google.android.houseaddress.viewModel.MainViewModel;
import com.google.android.houseaddress.viewModel.MyViewModel;

public class NewTripActivity extends AppCompatActivity {
    private static final String TAG = "NewTripActivity";
    EditText title, description;
    MainViewModel viewModel;
    MyViewModel myViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);
        title = findViewById(R.id.create_title);
        description = findViewById(R.id.create_description);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        findViewById(R.id.create_button)
                .setOnClickListener(v -> {
                    if (title.getText().toString().trim().equals("") || description.getText().toString().trim().equals("")){
                        Toast.makeText(NewTripActivity.this,
                                "please provide title and description", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Trip trip;
                        myViewModel.repository.liveDirections.setValue(null);

                        if (getIntent().hasExtra(FROM_MAP)){
                            trip = getIntent().getParcelableExtra(FROM_MAP);
                            trip.setTitle(title.getText().toString());
                            trip.setDescription(description.getText().toString());
                            insertTrip(trip);
                            finish();

                        }
                        else {
                            trip = new Trip();
                            trip.setTitle(title.getText().toString());
                            trip.setDescription(description.getText().toString());
                            Intent intent = new Intent(NewTripActivity.this, MainActivity.class);
                            intent.putExtra(CREATE_TRIP_DATA, trip);
                            startActivity(intent);
                            finishAffinity();
                        }
                    }
                });
    }
    private void insertTrip(Trip trip){
        viewModel.getInsert(trip).observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                Log.d(TAG, "onChanged: trip saved");
            }
        });
    }
}