package com.visa.vts.certificationapp.adapter;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import com.visa.vts.certificationapp.R;
import com.visa.vts.certificationapp.model.AccountData;
import com.visa.vts.certificationapp.model.AccountDetails;
import com.visa.vts.certificationapp.utils.Constants;
import com.visa.vts.certificationapp.utils.Utils;

/**
 * Created by prrathin on 12/2/15.
 */
public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.ViewHolder> implements View.OnCreateContextMenuListener, View.OnLongClickListener {

    Activity mActivity;
    public int mselectedCardIndex;


    public CardListAdapter(Activity activity) {
        mActivity = activity;
        mselectedCardIndex = -1;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_card_item, parent, false);
        ViewHolder holder = new ViewHolder(v);
        v.setOnLongClickListener(this);
        v.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        AccountDetails account = AccountData.getInstance().getAccountDetailList().get(position);
        viewHolder.cardNumber.setText("xxxx"+account.getLast4digit());
        viewHolder.cardExpiry.setText(account.getExpiry());
        viewHolder.cardStatus.setText("Active");
    }

    @Override
    public int getItemCount() {
        return AccountData.getInstance().getNoAccounts();
    }

    @Override
    public boolean onLongClick(View v) {
        ViewHolder holder = (ViewHolder) v.getTag();
        mselectedCardIndex = holder.getPosition();
        Utils.printLog("onLongClick :"+mselectedCardIndex);
        return false;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView cardNumber;
        public TextView cardExpiry;
        public TextView cardStatus;


        public ViewHolder(View v) {
            super(v);
            cardNumber = (TextView) v.findViewById(R.id.card_number);
            cardExpiry = (TextView) v.findViewById(R.id.card_expiry);
            cardStatus = (TextView) v.findViewById(R.id.card_status);
            v.setOnCreateContextMenuListener(CardListAdapter.this);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    final ContextMenu.ContextMenuInfo menuInfo) {

        menu.setHeaderTitle("Select The Action");
        //groupId, itemId, order, title

        menu.add(0, Constants.CMD_SUSPEND, 0, "Suspend");
        menu.add(0, Constants.CMD_RESUME, 0, "Resume");
        menu.add(0, Constants.CMD_DELETE, 0, "Delete");
        menu.add(0, Constants.CMD_REPLENISH, 0, "Replenishment");
        menu.add(0, Constants.CMD_CONFIRM_REPLENISH, 0, "Confirm Replenish");
        menu.add(0, Constants.CMD_SETAS_DEFAULT, 0, "Set As Default");


    }

    }
