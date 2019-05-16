package com.demo.openglesdemos.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by wangyt on 2019/5/9
 */
public class CommonUtil {
    public static final String TAG = "openglesDemos";

    private static Context context;

    public static void init(Context ctx){
        context = ctx.getApplicationContext();
    }

    public static boolean checkGLVersion(){
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo ci = am.getDeviceConfigurationInfo();
        return ci.reqGlEsVersion >= 0x30000;
    }

}
