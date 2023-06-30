package com.google.android.houseaddress.viewModel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.houseaddress.network.Repository;
import com.google.android.houseaddress.network.directions.Directions;

import java.util.List;

public class MyViewModel extends AndroidViewModel {
    private static final String TAG = "MyViewModel";
    public MutableLiveData<List<List<LatLng>>> pointsLiveData = new MutableLiveData<>();
    public Repository repository;
    Context application;
    public MyViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance();
        this.application = application;
    }

    public void getOpenDirections(String mode, String start, String end){

        repository.getOpenDirections(mode, start, end, application);
    }
    public MutableLiveData<Directions> getDirectionsLive(){
        return repository.liveDirections;
    }
}
