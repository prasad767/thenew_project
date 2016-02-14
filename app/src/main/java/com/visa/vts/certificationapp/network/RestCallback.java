package com.visa.vts.certificationapp.network;

import com.visa.vts.certificationapp.utils.Utils;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public abstract class RestCallback<T> implements Callback<T> {

    @Override
    public void onResponse(Response<T> response, Retrofit retrofit){
        String cookieData = response.headers().get("Set-Cookie");
        if(!Utils.isNull(cookieData)){
            String values [] = cookieData.split(";");
            //Session.getInstance().sessionID = values[0];
        }
    }

    @Override
    public void onFailure(Throwable throwable) {

    }

}