package com.google.android.houseaddress.activity;

import static com.google.android.houseaddress.constant.Constant.HOUSE_DATA;
import static com.google.android.houseaddress.constant.Constant.HOUSE_NUMBER;
import static com.google.android.houseaddress.constant.Constant.TRIP_DATA;
import static com.google.android.houseaddress.constant.Constant.VIEW_HOUSE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.houseaddress.MainActivity;
import com.google.android.houseaddress.R;
import com.google.android.houseaddress.houseAddress.Address;
import com.google.android.houseaddress.utils.LocationInfo;

public class TripInfoActivity extends AppCompatActivity {

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_info);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView title = findViewById(R.id.title);
        TextView latitude = findViewById(R.id.latitude);
        TextView longitude = findViewById(R.id.longitude);
        TextView altitude = findViewById(R.id.altitude);
        TextView houseNumber = findViewById(R.id.house_number);
        TextView road = findViewById(R.id.road);
        TextView postCode = findViewById(R.id.post_code);
        TextView america = findViewById(R.id.america);

        LocationInfo  info= getIntent().getParcelableExtra(TRIP_DATA);
        Address address = getIntent().getParcelableExtra(HOUSE_DATA);
        int i = getIntent().getIntExtra(HOUSE_NUMBER,0);

        findViewById(R.id.create_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TripInfoActivity.this, MainActivity.class);
                        intent.putExtra(VIEW_HOUSE,info);
                        startActivity(intent);
                    }
                });

        if (i != 0){
            getSupportActionBar().setTitle("HOUSE "+i);
        }

        if (info.getLatitude()>= -56 && info.getLatitude() <= 84){
            america.setText("House Description: It is probably a wood house");
        }
        else{
            america.setText("House Description: Since this is outside North America, this house may be made of stone or brick.");
        }
        latitude.setText("Latitude: "+info.getLatitude());
        longitude.setText("Longitude: "+info.getLongitude());
        altitude.setText("Altitude: "+info.getAltitude());
        title.setText(unknownStringLocality(address.getSuburb())
                +unknownStringLocality(address.getCity())
                +unknownStringLocality(address.getState()));
        houseNumber.setText("House Number: "+unknownString(address.getHouse_number()));
        road.setText("Street: "+unknownString(address.getRoad()));
        postCode.setText("Postcode: "+unknownString(address.getPostcode()));
    }

    String unknownString(String value){
        if (value == null){
            return "Unknown";
        }
        else {
            if (value.trim().equals("")){
                return "Unknown";
            }
        }
        return value;
    }
    String unknownStringLocality(String value){
        if (value == null){
            return "";
        }
        else {
            if (value.trim().equals("")){
                return "";
            }
        }
        return value+", ";
    }
}