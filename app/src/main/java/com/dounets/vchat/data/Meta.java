package com.dounets.vchat.data;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.dounets.vchat.App;

public class Meta {
    private static Meta instance = new Meta();

    public static Meta getInstance() {
        return instance;
    }

    public String getApiDomain() {
        try {
            String packageName = App.getInstance().getPackageName();
            ApplicationInfo appInfo = App.getInstance().getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("API_DOMAIN");
        } catch (PackageManager.NameNotFoundException e) {
        }
        return null;
    }

    public String getApiScheme() {
        try {
            String packageName = App.getInstance().getPackageName();
            ApplicationInfo appInfo = App.getInstance().getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("API_SCHEME");
        } catch (PackageManager.NameNotFoundException e) {
        }
        return null;
    }

}
