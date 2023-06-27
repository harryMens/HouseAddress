package com.google.android.houseaddress.room;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.google.android.houseaddress.utils.Trip;

@Database(entities = Trip.class,version = 9)
@TypeConverters({Converter.class,HouseConverter.class})
public abstract class TripDataBase extends RoomDatabase {
    public static final String DATABASE_NAME="name_database";

    private static TripDataBase instance;
    public static TripDataBase getInstance(final Context context){
        if (instance == null) {
            return Room.databaseBuilder(
                            context.getApplicationContext(),
                            TripDataBase.class,
                            DATABASE_NAME
                    )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
    public abstract TripDao getTripDao();
}
