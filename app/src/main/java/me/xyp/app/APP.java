package me.xyp.app;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.Logger;

/**
 * Created by cc on 16/4/23.
 */
public class APP extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init("JWZX_JSOUP");
        context = getBaseContext();
    }

    public static Context getContext() {
        return context;
    }

//    @Override
//    public void onTerminate() {
//        super.onTerminate();
//        context = null;
//    }
}
