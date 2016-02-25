package com.dounets.vchat;

import android.app.Application;

import com.dounets.vchat.helper.MyPreferenceManager;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by an.pham1611 on 2/23/16.
 */
public class App extends Application {

    public static final String TAG = App.class.getSimpleName();

    private static App app;

    private MyPreferenceManager pref;

    public static App getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.app = this;
//        FlowManager.init(this);
    }

    public MyPreferenceManager getPrefManager() {
        if (pref == null) {
            pref = new MyPreferenceManager(this);
        }

        return pref;
    }
}
