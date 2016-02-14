package com.visa.vts.certificationapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.visa.vts.certificationapp.R;
import com.visa.vts.certificationapp.activity.ui.CirclePageIndicator;
import com.visa.vts.certificationapp.activity.ui.PageIndicator;
import com.visa.vts.certificationapp.fragment.CardListFragment;
import com.visa.vts.certificationapp.fragment.EmptyWalletFragment;
import com.visa.vts.certificationapp.model.AccountData;
import com.visa.vts.certificationapp.model.AccountDetails;
import com.visa.vts.certificationapp.utils.Constants;
import com.visa.vts.certificationapp.utils.Logger;
import com.visa.vts.certificationapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private StringBuilder log;
    private MyPageAdapter pageAdapter;
    private ViewPager pager;
    private AccountData accountData;
    private Logger logger;
    private PageIndicator mIndicator;
    private static int mdefaultCardIndex = 0;
    private int mcardCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger.addRecordToLog("Dashboard activity started onCreate()..");
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCardScreen();
            }
        });

        ImageButton tapToPayBtn = (ImageButton) findViewById(R.id.tapToPayBtn);
        tapToPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchPaymentComplete();
            }
        });

        ImageButton fabinfo = (ImageButton) findViewById(R.id.fabinfo);
        fabinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCardDetailsScreen();

            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        pager = (ViewPager) findViewById(R.id.viewpagerdashboard);
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

/*
        accountData = AccountData.getInstance();
        mcardCount = accountData.getNoAccounts();
        Log.v("DashboardActivity", " " + mcardCount);
        if(mcardCount!=0){
            loadCaurosel();
        }else{
            loadFragment(Constants.FRAGMENT_EMPTY_WALLET);
        }
*/
    }

    @Override
    public void onResume() {
        super.onResume();

        accountData = AccountData.getInstance();
        mcardCount = accountData.getNoAccounts();
        Log.v("DashboardActivity", " " + mcardCount);
        if (mcardCount != 0) {
            loadCaurosel();
        } else {
            loadFragment(Constants.FRAGMENT_EMPTY_WALLET);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_card_list) {
            Intent cardlistIntent = new Intent(this, CardListActivity.class);
            startActivityForResult(cardlistIntent, Constants.CARD_ACTION_REQUEST_CODE);
        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(this, SettingActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CARD_ACTION_REQUEST_CODE && data != null) {

            mdefaultCardIndex = data.getIntExtra(Constants.DEFAULT_CARD_MESSAGE, 0);
            Utils.printLog("DashboardActivity - DefaultCardIndex:" + mdefaultCardIndex);
            mIndicator.setCurrentItem(mdefaultCardIndex);
        }
    }


    private void showAddCardScreen() {
        startActivity(new Intent(this, AddCardActivity.class));
    }

    public void loadFragment(int fragmentID) {
        View tapView = (View) findViewById(R.id.tapView);
        tapView.setVisibility(View.GONE);
        TextView TxtView = (TextView) findViewById(R.id.textView);
        TxtView.setVisibility(View.GONE);
        pager.setVisibility(View.GONE);
        View indicatorview = (View) findViewById(R.id.indicatorView);
        indicatorview.setVisibility(View.GONE);

        Fragment fragment = null;
        switch (fragmentID) {
            case Constants.FRAGMENT_EMPTY_WALLET:
                fragment = EmptyWalletFragment.newInstance();

                break;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_view, fragment);
        fragmentTransaction.commit();
    }

    public void loadCaurosel() {
        List<Fragment> fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);

        pager.setAdapter(pageAdapter);

        mIndicator.setViewPager(pager);
        mIndicator.setActivity(this);
        mIndicator.setCurrentItem(mdefaultCardIndex);

    }

    public void launchPaymentComplete() {
        Log.v("tapImgButton2", "Clicked for Payment Complete!");

        Intent inet = new Intent(this, PaymentCompleteActivity.class);
        startActivity(inet);
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();
        ArrayList<AccountDetails> acList = accountData.getAccountDetailList();
        for (int i = 0; i < mcardCount; i++) {
            AccountDetails acDetails = acList.get(i);
            fList.add(CardListFragment.newInstance(acDetails.getLast4digit(), acDetails.getExpiry()));
        }
        return fList;
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }

    public void showCardDetailsScreen() {
        if (mcardCount != 0) {
            int cardIndex = mIndicator.getmCurrentPage();
            Intent intent = new Intent(this, CardDetailsActivity.class);
            intent.putExtra("cardIndex", cardIndex);
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.txt_no_card_info);
            builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
