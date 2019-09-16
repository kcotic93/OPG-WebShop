package com.example.kristijan.opg_webshop.Common;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.kristijan.opg_webshop.R;

public class ToolbarElevationOffsetListener implements AppBarLayout.OnOffsetChangedListener {
    private final Toolbar mToolbar;
    private float mTargetElevation;
    private final AppCompatActivity mActivity;

    public ToolbarElevationOffsetListener(AppCompatActivity appCompatActivity, Toolbar toolbar) {
        mActivity = appCompatActivity;
        mToolbar = toolbar;
        mTargetElevation = mToolbar.getContext().getResources().getDimension(R.dimen.appbar_elevation);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        offset = Math.abs(offset);
        mTargetElevation = Math.max(mTargetElevation, appBarLayout.getTargetElevation());
        if (offset >= appBarLayout.getTotalScrollRange() - mToolbar.getHeight()) {
            float flexibleSpace = appBarLayout.getTotalScrollRange() - offset;
            float ratio = 1 - (flexibleSpace / mToolbar.getHeight());
            float elevation = ratio * mTargetElevation;
            setToolbarElevation(elevation);
        } else {
            setToolbarElevation(0);
        }

    }

    private void setToolbarElevation(float targetElevation) {
        ActionBar supportActionBar = mActivity.getSupportActionBar();
        if (supportActionBar != null) supportActionBar.setElevation(targetElevation);
        else if (mToolbar != null)
            ViewCompat.setElevation(mToolbar, targetElevation);
    }
}
