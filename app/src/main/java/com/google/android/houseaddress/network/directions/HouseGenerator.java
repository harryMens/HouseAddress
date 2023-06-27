package com.google.android.houseaddress.network.directions;

import static com.google.android.houseaddress.network.Static.HOUSE_ADDRESS;

import com.google.android.houseaddress.network.Network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class HouseGenerator {
    static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .build();
    private static Retrofit retrofit =
            new Retrofit.Builder()
                    // .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(HOUSE_ADDRESS)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

    public static Network request =
            retrofit.create(Network.class);

    public Network getRequest(){
        return request;
    }
}