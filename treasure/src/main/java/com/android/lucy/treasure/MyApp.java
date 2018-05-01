package com.android.lucy.treasure;

import android.app.Application;



public class MyApp extends Application {
    private static MyApp instance;

    public static MyApp getContext(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }

}
