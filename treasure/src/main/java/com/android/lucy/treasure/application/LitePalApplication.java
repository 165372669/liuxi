package com.android.lucy.treasure.application;

import android.app.Application;

/**
 * 数据库Application
 */

public class LitePalApplication extends Application {
    private static LitePalApplication instance;

    public static LitePalApplication getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
