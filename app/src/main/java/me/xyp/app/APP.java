package me.xyp.app;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.helper.StringUtil;

import me.xyp.app.config.Config;
import me.xyp.app.event.LoginEvent;
import me.xyp.app.util.Util;

/**
 * Created by cc on 16/4/23.
 */
public class APP extends Application {

    private static Context context;
    private static String stuNum;

    public static String getStuNum() {
        if (StringUtil.isBlank(stuNum)) {
            stuNum = Util.get(getContext(), Config.SP_KEY_STU_NUM);
            if (StringUtil.isBlank(stuNum)) {
                EventBus.getDefault().post(new LoginEvent(LoginEvent.TYPE.NO_COOKIE));
            }
        }
        return stuNum;
    }

    public static void clearStuNum() {
        APP.stuNum = null;
    }

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
