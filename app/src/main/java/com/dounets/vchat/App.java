package com.dounets.vchat;

import android.app.Application;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by an.pham1611 on 2/23/16.
 */
public class App extends Application {
    private static App app;

    public static App getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.app = this;
//        FlowManager.init(this);
    }
}
