package com.zify.musicsearch;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

/**
 * Created by Murali on 17/12/2019.
 */

public class MusicSearchApplication extends Application {

    private static MusicSearchApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

    }


    /**
     * @return the main context of the Application
     */
    public static Context getAppContext()
    {
        return instance;
    }

    /**
     * @return the main resources from the Application
     */
    public static Resources getAppResources()
    {
        return instance.getResources();
    }
}
