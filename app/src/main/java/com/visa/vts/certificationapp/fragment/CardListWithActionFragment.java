package com.visa.vts.certificationapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.visa.vts.certificationapp.R;


/**
 * Created by Chandrakanta_Sahoo on 11/26/2015.
 */
public class CardListWithActionFragment extends BaseFragment {
    public static final String CARD_LAST4_DIGIT = "CARD_LAST4_DIGIT";
    public static final String CARD_EXPIRY = "CARD_EXPIRY";

    public static final CardListWithActionFragment newInstance()
    {
        CardListWithActionFragment f = new CardListWithActionFragment();
        return f;
    }
    public static final CardListWithActionFragment newInstance(String last4Digit,String cardexpiry)
    {
        CardListWithActionFragment f = new CardListWithActionFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString(CARD_LAST4_DIGIT, last4Digit);
        bdl.putString(CARD_EXPIRY,cardexpiry);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("CardListWithAction", "onCreateView()");
        View v = inflater.inflate(R.layout.fragment_card_list_with_action, container, false);

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
        Log.v("CardListWithAction", "onActivityCreated().");

    }
}
