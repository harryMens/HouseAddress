package com.google.android.houseaddress.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.houseaddress.room.RepositoryRoom;
import com.google.android.houseaddress.utils.LocationInfo;
import com.google.android.houseaddress.utils.Trip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainViewModel extends AndroidViewModel {
    RepositoryRoom repository;
    public MutableLiveData<LatLng> liveLatLng = new MutableLiveData<>();
    public MutableLiveData<LocationInfo> liveInfo = new MutableLiveData<>();
    public MutableLiveData<Boolean> follow = new MutableLiveData<>();
    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = RepositoryRoom.getInstance(application);
    }

    public LiveData<Long> getInsert(Trip trip){
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String currentTime = dateFormat.format(new Date(currentTimeMillis));
        trip.setTime(currentTime);
        return LiveDataReactiveStreams.fromPublisher(
                repository.insertTrip(trip)
        );
    }
    public LiveData<Integer> getUpdate(Trip trip){
        return LiveDataReactiveStreams.fromPublisher(
                repository.updateTrip(trip)
        );
    }
    public LiveData<Integer> getDelete(Trip trip){
        return LiveDataReactiveStreams.fromPublisher(
                repository.deleteTrip(trip)
        );
    }
    public LiveData<List<Trip>> getTrip(){
        return repository.getLiveTrips();
    }

}
