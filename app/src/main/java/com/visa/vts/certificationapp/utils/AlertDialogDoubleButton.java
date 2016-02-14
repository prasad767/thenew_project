package com.visa.vts.certificationapp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.visa.vts.certificationapp.R;

/**
 * Created by manjit.kaur on 2/9/2016.
 */
public class AlertDialogDoubleButton {

    /**
     * show Alert with double  button
     *
     * @param aActivity
     * @param aMessage
     * @param aSetOnPositiveButtonClick
     * @param aSetOnNegativeButtonClick
     */
    public static void showAlert(Activity aActivity, String aMessage, final setOnPositiveButtonClick aSetOnPositiveButtonClick, final setOnNegativeButtonClick aSetOnNegativeButtonClick) {

        AlertDialog.Builder builder = new AlertDialog.Builder(aActivity);
        if (aMessage != null)
            if (aMessage.length() > 0)
                builder.setMessage(aMessage);
        // Add the buttons
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener()

                {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        if (aSetOnPositiveButtonClick != null) {
                            aSetOnPositiveButtonClick.onPositiveButton(dialog);
                        }
                    }
                }

        );
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener()

                {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        if (aSetOnNegativeButtonClick != null) {
                            aSetOnNegativeButtonClick.onNegativeButton(dialog);
                        }
                    }
                }

        );

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public interface setOnPositiveButtonClick {
        void onPositiveButton(DialogInterface dialog);
    }

    public interface setOnNegativeButtonClick {
        void onNegativeButton(DialogInterface dialog);
    }
}
