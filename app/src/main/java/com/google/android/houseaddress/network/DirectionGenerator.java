package com.google.android.houseaddress.network;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.android.houseaddress.network.Static.OPEN_DIRECTION;

public class DirectionGenerator {
    private static Retrofit retrofit =
            new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(OPEN_DIRECTION)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

    public static Network request =
            retrofit.create(Network.class);

    public Network getRequest(){
        return request;
    }
}