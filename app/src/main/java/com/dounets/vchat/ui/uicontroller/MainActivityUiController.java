package com.dounets.vchat.ui.uicontroller;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.dounets.vchat.R;
import com.dounets.vchat.ui.activity.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by an.pham1611 on 2/23/16.
 */
public class MainActivityUiController implements View.OnClickListener {
    MainActivity activity;

    @Bind(R.id.toolbar)
    Toolbar toolBar;

    @Bind(R.id.button)
    Button recordButton;

    public MainActivityUiController(MainActivity activity) {
        this.activity = activity;
        ButterKnife.bind(this, activity);
        init();
    }

    private void init() {
        initToolbar();
        recordButton.setOnClickListener(this);
    }

    private void initToolbar() {
        toolBar.setNavigationIcon(R.drawable.icn_white_back);
        toolBar.setNavigationOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button: {
                activity.onClickRecord();
            }
            break;
            default:
                activity.onBackPressed();
                break;
        }
    }
}
