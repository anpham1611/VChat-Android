package com.dounets.vchat.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.dounets.vchat.R;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by yo on 6/9/15.
 */
public class LoadingDialog extends DialogFragment {
    int messageResId;

    @Bind(R.id.dlMessage)
    TextView ldMessage;

    public static LoadingDialog newInstance(int messageResId) {
        LoadingDialog f = new LoadingDialog();
        f.setCancelable(false);
        Bundle args = new Bundle();
        args.putInt("messageResId", messageResId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageResId = getArguments().getInt("messageResId");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.LoadingDialogTheme);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_loading, null);
        ButterKnife.bind(this, view);

        ldMessage.setText(messageResId);
        return view;
    }
}
