package com.google.android.houseaddress.room;

import androidx.room.TypeConverter;

import com.google.android.houseaddress.utils.LocationInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public  class Converter {
    @TypeConverter
    public static List<LocationInfo> storedStringToMyObjects(String data) {
        Gson gson = new Gson();
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<LocationInfo>>() {}.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String myObjectsToStoredString(List<LocationInfo> myObjects) {
        Gson gson = new Gson();
        return gson.toJson(myObjects);
    }
    }