package com.dounets.vchat.ui.uicontroller;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;

import com.dounets.vchat.R;
import com.dounets.vchat.ui.activity.MainActivity;
import com.etsy.android.grid.StaggeredGridView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by an.pham1611 on 2/23/16.
 */
public class MainActivityUiController implements View.OnClickListener, AbsListView.OnScrollListener, AbsListView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    MainActivity activity;

    @Bind(R.id.toolbar)
    Toolbar toolBar;

    @Bind(R.id.grid_view)
    StaggeredGridView gridView;

    public MainActivityUiController(MainActivity activity) {
        this.activity = activity;
        ButterKnife.bind(this, activity);
        init();
    }

    private void init() {
        initToolbar();
    }

    private void initToolbar() {
        toolBar.setNavigationIcon(R.drawable.icn_white_back);
        toolBar.setNavigationOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            default:
                activity.onBackPressed();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
