package com.visa.vts.certificationapp.activity.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by chandrakanta_Sahoo on 12/9/2015.
 */
public abstract class LoadingAsync extends AsyncTask<Object, Void, String>{

    ProgressDialog progressDialog;
    public String loadingMessage = "";
    Context context;

    public LoadingAsync(Context context) {
        this.context = context;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(loadingMessage.length()>0)
        {
            progressDialog =  ProgressDialog.show(context,"",loadingMessage);
            progressDialog.isIndeterminate();
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    protected String doInBackground(Object... params) {

        on_Background();

        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try{
            if(progressDialog!=null){
                progressDialog.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        on_PostExecute();

    }



    public abstract void on_Background();

    public abstract void on_PostExecute();

}
