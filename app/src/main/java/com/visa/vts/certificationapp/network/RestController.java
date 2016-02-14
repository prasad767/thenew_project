package com.visa.vts.certificationapp.network;

import com.squareup.okhttp.OkHttpClient;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
/**
 * Created by prrathin on 11/18/15.
 */
//.baseUrl("http://sandbox.digital.visa.com/")

public enum  RestController {
    INSTANCE;
    private static BackendEndPoints backendApi;

    public static BackendEndPoints getApi(){

        if(backendApi == null){

            OkHttpClient client = new OkHttpClient();
            client.interceptors().add(new LoggingInterceptor());
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://dmpdapps.com/")
                    .client(client)
                    .addConverterFactory(new ToStringConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            backendApi = retrofit.create(BackendEndPoints.class);
        }return backendApi;
    }

}