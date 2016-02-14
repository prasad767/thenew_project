package com.visa.vts.certificationapp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by manjit.kaur on 2/9/2016.
 */
public class AlertDialogSingleButton {

    /**
     * show Alert with single button
     *
     * @param aActivity
     * @param aMessage
     * @param aSetOnNeutralButtonClick
     */
    public static void showAlert(Activity aActivity, String aMessage, final setOnNeutralButtonClick aSetOnNeutralButtonClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(aActivity);
        if (aMessage != null)
            if (aMessage.length() > 0)
                builder.setMessage(aMessage);
        // Add the buttons
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener()

                {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        if (aSetOnNeutralButtonClick != null) {
                            aSetOnNeutralButtonClick.onNeutralButton(dialog);
                        }
                    }
                }

        );

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public interface setOnNeutralButtonClick {
        void onNeutralButton(DialogInterface dialog);
    }


}
