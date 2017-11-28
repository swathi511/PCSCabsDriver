package com.hjsoft.driverbooktaxi.webservices;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hjsoft on 23/11/16.
 */
public class RestClient {

    private static String BASE_URL="http://192.168.1.31:1533/api/";
    private static API REST_CLIENT;
    //http://192.168.1.13:1533/api/(v) http://192.168.1.10:1532/api/ (sh)
    //http://api.travelsmate.in/api/ (old server)
    //http://104.192.4.94/api/ (new server)
    static {
        setupRestClient();

    }

    public static API get() {
        return REST_CLIENT;
    }

    private static void setupRestClient(){

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        REST_CLIENT=retrofit.create(API.class);

       }
}
