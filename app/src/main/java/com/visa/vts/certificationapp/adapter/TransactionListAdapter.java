package com.visa.vts.certificationapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.visa.vts.certificationapp.R;
import com.visa.vts.certificationapp.model.ListTransactionModel;

import java.util.ArrayList;

/**
 * Created by Manjit.Kaur on 12/15/2015.
 */
public class TransactionListAdapter extends BaseAdapter implements View.OnClickListener {

    private Activity mActivity;
    private ArrayList listData;
    private static LayoutInflater inflater = null;
    private Resources res;
    private ListTransactionModel tempValues = null;
    private int i = 0;

    public TransactionListAdapter(Activity a, ArrayList d, Resources resLocal) {

        mActivity = a;
        listData = d;
        res = resLocal;
        inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        if (listData.size() <= 0)
            return 1;
        return listData.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /*********
     * Create a holder Class to contain inflated xml file elements
     *********/
    public static class ViewHolder {
        public TextView TransactionName;
        public TextView TransactionPlace;
        public TextView TransactionTime;
        public TextView TransactionBill;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View convertview = convertView;
        ViewHolder holder;
        if (convertView == null) {
            convertview = inflater.inflate(R.layout.transaction_list_row, null);

            holder = new ViewHolder();
            holder.TransactionName = (TextView) convertview.findViewById(R.id.transaction_name);
            holder.TransactionPlace = (TextView) convertview.findViewById(R.id.transaction_place);
            holder.TransactionBill = (TextView) convertview.findViewById(R.id.transaction_bill);
            holder.TransactionTime = (TextView) convertview.findViewById(R.id.transaction_time);

            convertview.setTag(holder);
        } else
            holder = (ViewHolder) convertview.getTag();

        if (listData.size() <= 0) {
            holder.TransactionName.setText("No Data");

        } else {
            /***** Get each Model object from Arraylist ********/
            tempValues = null;
            tempValues = (ListTransactionModel) listData.get(position);
            /************  Set Model values in Holder elements ***********/
            holder.TransactionName.setText(tempValues.getTransactionName());
            holder.TransactionPlace.setText(tempValues.getTransactionPlace());
            holder.TransactionTime.setText(tempValues.getTransactionTime());
            holder.TransactionBill.setText((tempValues.getTransactionBill()));
            convertview.setOnClickListener(new OnItemClickListener(position));
        }
        return convertview;
    }

    @Override
    public void onClick(View v) {

        Log.v("TransactionListAdapter", "Row button clicked");
    }

    private class OnItemClickListener implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {

        }
    }
}
