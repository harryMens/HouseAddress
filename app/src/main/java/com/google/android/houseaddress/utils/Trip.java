package com.google.android.houseaddress.utils;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.android.houseaddress.houseAddress.Address;

import java.util.List;

@Entity
public class Trip implements Parcelable {
    @TypeConverters
    List<LocationInfo> trip;


    protected Trip(Parcel in) {
        trip = in.createTypedArrayList(LocationInfo.CREATOR);
        address = in.createTypedArrayList(Address.CREATOR);
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        errorReturn = in.readInt();
        time = in.readString();
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    public List<Address> getAddress() {
        return address;
    }

    public void setAddress(List<Address> address) {
        this.address = address;
    }

    @TypeConverters
    List<Address> address;

    @PrimaryKey(autoGenerate = true)
    int id;

    String title;
    String description;
    int errorReturn;
    String time;

    public Trip() {
    }



    public List<LocationInfo> getTrip() {
        return trip;
    }

    public void setTrip(List<LocationInfo> trip) {
        this.trip = trip;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getErrorReturn() {
        return errorReturn;
    }

    public void setErrorReturn(int errorReturn) {
        this.errorReturn = errorReturn;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeTypedList(trip);
        dest.writeTypedList(address);
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(errorReturn);
        dest.writeString(time);
    }
}
