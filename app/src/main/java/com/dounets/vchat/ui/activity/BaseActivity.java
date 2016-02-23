package com.dounets.vchat.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.dounets.vchat.R;
import com.dounets.vchat.net.api.ApiError;
import com.dounets.vchat.ui.LoadingDialog;

public abstract class BaseActivity extends AppCompatActivity {

    private LoadingDialog dialogMessage;

    protected void toastApiErrorMessage(ApiError error) {
        String message = error.getApiMessage();
        if (message != null) {
            Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(BaseActivity.this, "Failed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void showLoadingMessage(int strResId) {
        if (isFinishing()) {
            return;
        }
        if (dialogMessage != null) {
            return;
        }
        dialogMessage = LoadingDialog.newInstance(R.string.processing);
        dialogMessage.show(getFragmentManager(), "LoadingDialog");
    }

    public void dismissLoadingMessage() {
        if (dialogMessage == null) {
            return;
        }
        try {
            dialogMessage.dismiss();
        } catch (IllegalStateException e) {

        } finally {
            dialogMessage = null;
        }
    }

}
