package com.visa.vts.certificationapp.utils;

import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Manjit.Kaur on 1/12/2016.
 */
public class ProgressDialog {
    private android.app.ProgressDialog mProgressDialog;
    private OnDismissListener mOnDismissListener;

    public ProgressDialog(Context context) {
        mProgressDialog = new android.app.ProgressDialog(context);

    }


    public void show() {
        if (mProgressDialog == null)
            return;

        mProgressDialog.setTitle("Please wait");
        mProgressDialog.setMessage("Loading..");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();

        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mOnDismissListener == null)
                    return;
                mOnDismissListener.onDismiss(dialog);
            }
        });

    }

    public void dismiss() {
        if (mProgressDialog == null)
            return;

        mProgressDialog.dismiss();
    }

    public void setOnDismissListener(OnDismissListener l) {
        mOnDismissListener = l;
    }

    public interface OnDismissListener {
        void onDismiss(DialogInterface dialog);
    }
}
