package com.android.lucy.treasure.application;

public class MyApp extends LitePalApplication {
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
