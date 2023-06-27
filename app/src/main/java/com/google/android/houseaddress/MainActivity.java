package com.google.android.houseaddress;

import static com.google.android.houseaddress.network.Static.DRIVING;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.houseaddress.network.directions.Directions;
import com.google.android.houseaddress.viewModel.MyViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private MyViewModel myViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        LatLng accra = new LatLng(5.614818, -0.205874);
        LatLng lagos = new LatLng( 6.465422, 3.406448);
        String destination = lagos.longitude + "," + lagos.latitude;
        String begin = accra.longitude + "," + accra.latitude;
        myViewModel.getOpenDirections(DRIVING, begin, destination);

        getLiveDirection();
    }
    private void getLiveDirection(){
        myViewModel.getDirectionsLive().observe(this, new Observer<Directions>() {
            @Override
            public void onChanged(Directions directions) {
                if (directions != null){
                    Log.d(TAG, "onChanged: checking distance between from Accra to Lagos "+ directions
                            .getFeatures().get(0).getProperties().getSegments().get(0).getDistance());
                }
            }
        });
    }

}