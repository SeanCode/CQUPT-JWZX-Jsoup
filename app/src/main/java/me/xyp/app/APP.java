package me.xyp.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by cc on 16/4/23.
 */
public class APP extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

}
