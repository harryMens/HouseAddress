package com.google.android.houseaddress.network;


import com.google.android.houseaddress.houseAddress.HouseAddress;
import com.google.android.houseaddress.network.directions.Directions;
import com.google.android.houseaddress.utility.GO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Network {
    @GET("geocode/{query}/")
    Call<GO> getGeocoder(
            @Path("query") String query,
            @Query("key") String apiKey,
            @Query("language") String language
    );
    @GET("reverseGeocode/{query}/")
    Call<GO> getReverseGeocoder(
            @Path("query") String query,
            @Query("key") String apiKey,
            @Query("language") String language
    );
    @GET("reverse")
    Call<HouseAddress> houseAddress(
            @Query("format") String format,
            @Query("lat") double latitude,
            @Query("lon") double longitude
    );
    @GET("directions/{route_mode}")
    Call<Directions> getOpenDirection(
            @Path("route_mode") String query,
            @Query("api_key") String apiKey,
            @Query("start") String start,
            @Query("end") String end
    );
}
