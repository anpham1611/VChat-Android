package com.dounets.vchat.data.model;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dounets.vchat.App;
import com.dounets.vchat.data.LocalStoreUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.cache.BaseCacheableModel;

@Table(databaseName = LocalStoreUtil.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends BaseCacheableModel {
    static User currentUser;

    @Column
    @PrimaryKey
    Long id;

    @Column
    String name;

    @Column
    String api_token;

    @Column
    String mobile_id;

    public static User find(long id) {
        com.raizlabs.android.dbflow.sql.builder.Condition condition = com.raizlabs.android.dbflow.sql.builder.Condition.column("id").eq(id);
        User user = new Select().from(User.class).where(condition).querySingle();
        return user;
    }

    public static User currentUser() {
        if (currentUser != null) {
            return currentUser;
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(App.getInstance());
        long id = pref.getLong("current_user_id", -1L);
        String apiToken = pref.getString("current_user_api_token", null);

        if (id < 0 || apiToken == null) {
            return null;
        }

        currentUser = User.find(id);
        if (currentUser == null) {
            return null;
        }
        return currentUser;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getApi_token() {
        return api_token;
    }

    public String getMobile_id() {
        return mobile_id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setApi_token(String api_token) {
        this.api_token = api_token;
    }

    public void setMobile_id(String mobile_id) {
        this.mobile_id = mobile_id;
    }
}