package com.google.android.houseaddress.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.google.android.houseaddress.utils.LocationInfo;
import com.google.android.houseaddress.utils.Trip;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface TripDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insertTrip(Trip trip);

    @Update
    Single<Integer> updateTrip(Trip trip);

    @Delete
    Single<Integer> deleteTrip(Trip trip);

    @Query("SELECT * FROM trip")
    LiveData<List<Trip>> getTrip();
}
