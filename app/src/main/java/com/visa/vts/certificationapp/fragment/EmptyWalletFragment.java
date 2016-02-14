package com.visa.vts.certificationapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.visa.vts.certificationapp.R;

/**
 * Created by prrathin on 11/18/15.
 */
public class EmptyWalletFragment extends BaseFragment {

    public static EmptyWalletFragment newInstance() {
        EmptyWalletFragment emptyWalletFragment = new EmptyWalletFragment();
        return emptyWalletFragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_empty_wallet, container, false);
        return v;
    }
}
