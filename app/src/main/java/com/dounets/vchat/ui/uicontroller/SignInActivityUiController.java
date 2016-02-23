package com.dounets.vchat.ui.uicontroller;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dounets.vchat.R;
import com.dounets.vchat.ui.activity.SignInActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by an.pham1611 on 2/23/16.
 */
public class SignInActivityUiController  implements View.OnClickListener {
    SignInActivity activity;

    @Bind(R.id.tv_name)
    EditText tvName;

    @Bind(R.id.btn_next)
    Button btnNext;

    public SignInActivityUiController(SignInActivity activity) {
        this.activity = activity;
        ButterKnife.bind(this, activity);
        init();
    }

    private void init() {
        btnNext.setOnClickListener(this);
    }

    private String getName() {
        return tvName.getText().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                activity.onClickNext(getName());
                break;
        }
    }
}
