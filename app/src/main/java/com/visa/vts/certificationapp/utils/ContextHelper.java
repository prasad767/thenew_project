package com.visa.vts.certificationapp.utils;

import android.content.Context;

/**
 * Created by Chandrakanta_Sahoo on 11/25/2015.
 */
public class ContextHelper {

    private static Context applicationContext;
    private volatile static boolean initialized = false;
    private volatile static ContextHelper instance;

    private ContextHelper(){
    }

    public static ContextHelper getInstance(Context context){
        if(instance == null){
            synchronized (ContextHelper.class) {
                if (instance == null) {
                    ContextHelper privateInstance = new ContextHelper();
                    privateInstance.init(context);
                    instance = privateInstance;
                }
            }
        }
        return instance;
    }

    private void init(Context context) {
        if (!initialized) {
            applicationContext = context;
            this.initialized = true;
        }
    }

    public static Context getContext(){
        if(initialized) {
            return applicationContext;
        }
        return null;
    }
}
