package com.google.android.houseaddress.room;

import android.content.Context;

import androidx.lifecycle.LiveData;


import com.google.android.houseaddress.utils.Trip;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;


public class RepositoryRoom {
    TripDao tripDao;
    private static RepositoryRoom instance;
    Context context;

    public RepositoryRoom(Context context) {
        this.context = context;
        tripDao = TripDataBase.getInstance(context).getTripDao();

    }
    public static RepositoryRoom getInstance(final Context context){
        if (instance == null){
            instance =  new RepositoryRoom(context);
        }
        return instance;
    }

    public LiveData<List<Trip>> getLiveTrips() {
        return tripDao.getTrip();
    }
    public Flowable<Long> insertTrip(Trip trip){
        return tripDao.insertTrip(trip)
                .toFlowable()
                .subscribeOn(Schedulers.io());
    }
    public Flowable<Integer> updateTrip(Trip trip){
        return tripDao.updateTrip(trip)
                .toFlowable()
                .subscribeOn(Schedulers.io());
    }
    public Flowable<Integer> deleteTrip(Trip trip){
        return tripDao.deleteTrip(trip)
                .toFlowable()
                .subscribeOn(Schedulers.io());
    }
}



