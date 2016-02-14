package com.visa.vts.certificationapp.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.visa.vts.certificationapp.R;
import com.visa.vts.certificationapp.activity.AddCardActivity;
import com.visa.vts.certificationapp.activity.PaymentCompleteActivity;

/**
 * Created by prrathin on 11/18/15.
 */
public class CardListFragment extends BaseFragment {
    public static final String CARD_LAST4_DIGIT = "CARD_LAST4_DIGIT";
    public static final String CARD_EXPIRY = "CARD_EXPIRY";

    public static final CardListFragment newInstance()
    {
        CardListFragment f = new CardListFragment();
        return f;
    }

    public static final CardListFragment newInstance(String message)
    {
        CardListFragment f = new CardListFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString(CARD_LAST4_DIGIT, message);
        f.setArguments(bdl);
        return f;
    }
    public static final CardListFragment newInstance(String last4Digit,String cardexpiry)
    {
        CardListFragment f = new CardListFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString(CARD_LAST4_DIGIT, last4Digit);
        bdl.putString(CARD_EXPIRY,cardexpiry);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("CardListFragment", "onCreateView().");
        View v = inflater.inflate(R.layout.fragment_card_list, container, false);

        //set Card Details with last 4 digit and expiry date.
        String last4Digit = getArguments().getString(CARD_LAST4_DIGIT);
        String cardExpiry = getArguments().getString(CARD_EXPIRY);
        TextView txtCardDetails =(TextView)v.findViewById(R.id.textViewCardDetails);
        txtCardDetails.setText("Visa ..."+last4Digit +","+" Exp. "+cardExpiry);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.v("CardListFragment", "onActivityCreated().");
/*
        ImageButton tapToPayBtn = (ImageButton) getView().findViewById(R.id.tapToPayBtn);
        //ImageButton payConfBtn = (ImageButton) getView().findViewById(R.id.payConfBtn);

        tapToPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inet = new Intent(getActivity(), PaymentCompleteActivity.class);
                startActivity(inet);

                /*
                // Change the fragment background color to black
                Log.v("tapImgButton", "Clicked!");
                RelativeLayout fragmentCardList = (RelativeLayout) getView().findViewById(R.id.fragmentCardList);
                fragmentCardList.setBackgroundColor(Color.WHITE);
                v.setVisibility(View.INVISIBLE);
                TextView textViewCardType = (TextView) getView().findViewById(R.id.textViewCardType);
                TextView textViewCardDetails = (TextView) getView().findViewById(R.id.textViewCardDetails);

                textViewCardType.setTextColor(Color.WHITE);
                textViewCardDetails.setTextColor(Color.WHITE);
                getView().findViewById(R.id.imageViewDots).setVisibility(View.INVISIBLE);
                //getView().findViewById(R.id.fabaddcard).setVisibility(View.INVISIBLE);
                getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
                getView().findViewById(R.id.payConfBtn).setVisibility(View.VISIBLE);


            }

        });
/*
        payConfBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Call the Payment complete screen
                Log.v("tapImgButton2", "Clicked for Payment Complete!");
                Intent inet = new Intent(getActivity(), PaymentCompleteActivity.class);
                startActivity(inet);
                getActivity().finish();


            }
        });
*/
    }

}
