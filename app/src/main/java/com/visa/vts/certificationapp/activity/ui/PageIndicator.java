package com.visa.vts.certificationapp.activity.ui;

import android.app.Activity;
import android.support.v4.view.ViewPager;

import com.visa.vts.certificationapp.activity.DashboardActivity;

/**
 * Created by Chandrakanta_Sahoo on 12/3/2015.
 */
public interface PageIndicator extends ViewPager.OnPageChangeListener {
    /**
     * Bind the indicator to a ViewPager.
     *
     * @param view
     */
    void setViewPager(ViewPager view);

    /**
     * Bind the indicator to a ViewPager.
     *
     * @param view
     * @param initialPosition
     */
    void setViewPager(ViewPager view, int initialPosition);

    /**
     * <p>Set the current page of both the ViewPager and indicator.</p>
     *
     * <p>This <strong>must</strong> be used if you need to set the page before
     * the views are drawn on screen (e.g., default start page).</p>
     *
     * @param item
     */
    void setCurrentItem(int item);

    /**
     * Set a page change listener which will receive forwarded events.
     *
     * @param listener
     */
    void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);

    /**
     * Notify the indicator that the fragment list has changed.
     */
    void notifyDataSetChanged();

    /**
     * Set parent activity to perform any activity
     *
     */

    void setActivity(DashboardActivity activity);

    int getmCurrentPage();

    }
