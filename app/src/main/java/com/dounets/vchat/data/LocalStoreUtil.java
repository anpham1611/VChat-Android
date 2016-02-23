package com.dounets.vchat.data;

import com.dounets.vchat.data.model.User;
import com.dounets.vchat.net.api.ApiError;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.sql.language.BaseModelQueriable;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.structure.Model;

import java.util.List;
import java.util.concurrent.Callable;

import bolts.Task;

@Database(name = LocalStoreUtil.NAME, version = LocalStoreUtil.VERSION)
public class LocalStoreUtil {
    public static final String NAME = "nykgear";
    public static final int VERSION = 2;
    public static final int NUMBER_OF_ITEMS_IN_A_PAGE = 36;

    public static Task<Void> clearAllInBackground() {
        return Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                LocalStoreUtil.clearAll();
                return null;
            }
        });
    }

    public static void clearAll() {
        new Delete().from(User.class).query();
    }

    public static <T extends Model> Task<List<T>> queryInBackgroud(final BaseModelQueriable<T> queriable, final Class<T> model) throws ApiError {
        return Task.callInBackground(new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                return LocalStoreUtil.query(queriable, model);
            }
        });
    }

    public static <T extends Model> List<T> query(BaseModelQueriable<T> queriable, Class<T> model) throws ApiError {
        return queriable.queryList();
    }
}