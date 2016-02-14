package com.visa.vts.certificationapp.manager;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

/**
 * Created by Manjit.Kaur on 1/13/2016.
 */
public class NetworkManager {

    public static boolean checkInternet(Activity aContext) {
        try {
            ConnectivityManager cm = (ConnectivityManager) aContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null)
                return true;
            else {
                showToast(aContext);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(aContext);
            return false;
        }
    }

    private static void showToast(Activity aContext) {
        Toast.makeText(aContext, "Not Connected to Internet", Toast.LENGTH_LONG).show();
    }
}
