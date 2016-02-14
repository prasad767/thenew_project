package com.visa.vts.certificationapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.visa.vts.certificationapp.R;
import com.visa.vts.certificationapp.environment.EnvListener;

import java.util.List;

/**
 * Created by Chandrakanta_Sahoo on 12/22/2015.
 */
public class EnvListAdapter extends ArrayAdapter<String> {

    List<String> environmentList;
    Context context;
    EnvListener environmentListener;


    public EnvListAdapter(Context context, int resource, int textViewResourceId, List<String> environmentList, EnvListener environmentListener) {
        super(context, resource, textViewResourceId, environmentList);

        this.environmentList        =   environmentList;
        this.context                =   context;
        this.environmentListener    =   environmentListener;
    }

    @Override
    public int getCount() {
        return environmentList.size();
    }

    @Override
    public String getItem(int position) {
        return environmentList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView==null)
        {
            LayoutInflater layoutInflater =  ((Activity)context).getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.env_listitem_layout,null);

            viewHolder = new ViewHolder();
            viewHolder.environmentTextView = (TextView)convertView.findViewById(R.id.environmentText);
            viewHolder.environmentTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   environmentListener.onEnvironmentSelected(environmentList.get(position));

                }
            });


        }else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.environmentTextView.setText(environmentList.get(position));


        return convertView;
    }


    class ViewHolder{
        TextView environmentTextView;
    }
}
