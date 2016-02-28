package com.dounets.vchat.ui.uicontroller;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dounets.vchat.R;
import com.dounets.vchat.data.model.Contact;
import com.dounets.vchat.helper.SharedPreferenceUtils;
import com.dounets.vchat.ui.activity.MainActivity;
import com.dounets.vchat.ui.adapter.ContactAdapter;
import com.etsy.android.grid.StaggeredGridView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by an.pham1611 on 2/23/16.
 */
public class MainActivityUiController implements View.OnClickListener, AbsListView.OnScrollListener, AbsListView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    MainActivity activity;
    ContactAdapter adapter;
    List<Contact> data;
    boolean mHasRequestedMore;
    private String TAG = MainActivityUiController.class.getSimpleName();

    @Bind(R.id.toolbar)
    Toolbar toolBar;

    @Bind(R.id.grid_view)
    StaggeredGridView gridView;

    ImageView imvGroup;
    ImageView imvUser;

    public MainActivityUiController(MainActivity activity, ContactAdapter adapter) {
        this.activity = activity;
        this.adapter = adapter;
        ButterKnife.bind(this, activity);
        init();
    }

    private void init() {
        initToolbar();

        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View header = layoutInflater.inflate(R.layout.list_header, null);
        imvUser = ButterKnife.findById(header, R.id.im_user);
        imvGroup = ButterKnife.findById(header, R.id.im_group);
        activity.setSupportActionBar(toolBar);
        activity.getSupportActionBar().setTitle(".:: " +
                "Hello " + SharedPreferenceUtils.getString("user_name"));

        gridView.addHeaderView(header);
        gridView.setAdapter(adapter);
        gridView.setOnScrollListener(this);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);
        imvGroup.setOnClickListener(this);
        imvUser.setOnClickListener(this);
    }

    private void initToolbar() {
//        toolBar.setNavigationIcon(R.drawable.icn_white_back);
//        toolBar.setNavigationOnClickListener(this);
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.im_user:
//                Toast.makeText(activity, "Coming soon!", Toast.LENGTH_LONG).show();
                break;

            case R.id.im_group:
                activity.onClickRecord(0);
                break;

            default:
                activity.onBackPressed();
                break;
        }
    }

    @Override
    public void onScrollStateChanged(final AbsListView view, final int scrollState) {
        Log.d(TAG, "onScrollStateChanged:" + scrollState);
    }

    @Override
    public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
        Log.d(TAG, "onScroll firstVisibleItem:" + firstVisibleItem +
                " visibleItemCount:" + visibleItemCount +
                " totalItemCount:" + totalItemCount);
        // our handling
        if (!mHasRequestedMore) {
            int lastInScreen = firstVisibleItem + visibleItemCount;
            if (lastInScreen >= totalItemCount) {
                Log.d(TAG, "onScroll lastInScreen - so load more");
                mHasRequestedMore = true;
                onLoadMoreItems();
            }
        }
    }

    private void onLoadMoreItems() {
        mHasRequestedMore = false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//        Toast.makeText(activity, "Item Clicked: - position: " + position + " - id: " + id, Toast.LENGTH_SHORT).show();
        if (position != 0) {
            activity.onClickRecord(id);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
//        Toast.makeText(activity, "Item Long Clicked: " + position, Toast.LENGTH_SHORT).show();
        return true;
    }
}
