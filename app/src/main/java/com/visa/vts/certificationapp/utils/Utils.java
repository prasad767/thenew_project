package com.visa.vts.certificationapp.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by prrathin on 11/18/15.
 */
public class Utils {

    public static String deviceID;

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public static boolean isNull(String obj) {
        if (null == obj) {
            return true;
        } else {
            return false;
        }

    }


    public static String stripNonDigits(String cardNumber) {
        if (cardNumber != null) {
            return cardNumber.replaceAll("\\D", "");
        }

        return null;
    }

    public static boolean isCardValid(String cardNumber) {

        String _cardNumer = stripNonDigits(cardNumber);
        if (_cardNumer.length() < 13 || _cardNumer.length() > 16) {
            return false;
        }

        // check for visa
        if (cardNumber.length() > 1 && cardNumber.substring(0, 1).equals("4")) {
            return false;
        }

        int factor = 1;
        int sum = 0;

        for (int i = _cardNumer.length() - 1; i >= 0; i--) {

            int codePoint = Integer.parseInt(_cardNumer.substring(i, i + 1));
            int addend = factor * codePoint;

            factor = (factor == 2) ? 1 : 2;

            addend = (addend / 10) + (addend % 10);
            sum += addend;
        }

        return sum % 10 == 0;
    }

    public static void printLog(String message) {
        Log.d("VTS APP", message);
    }


    public static boolean validateEdittext(EditText editText) {

        if (editText != null) {
            if (editText.getText().toString().trim().equalsIgnoreCase("")) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }


    public static void showToast(Activity mContext, String aText) {
        Toast.makeText(mContext, aText, Toast.LENGTH_LONG).show();
    }
}
