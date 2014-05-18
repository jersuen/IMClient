package com.jersuen.im;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * @author JerSuen
 */
public class IM extends Application {

    public static final String ACCOUNT_USERNAME = "account_username";
    public static final String ACCOUNT_PASSWORD = "account_password";

    public static final String HOST = "192.168.1.123";
    public static final int PORT = 5222;
    public static IM im;

    public void onCreate() {
        super.onCreate();
        im = this;
    }


    /**
     * 插入字符串
     * @param key
     * @param value
     * @return
     *      插入结果
     */
    public static boolean putString(String key, String value) {
        SharedPreferences settings = im.getSharedPreferences("im_account", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * 获取字符串
     * @param key
     * @return
     *      默认值为空字符串
     */
    public static String getString(String key) {
        SharedPreferences settings = im.getSharedPreferences("im_account", MODE_PRIVATE);
        return settings.getString(key,"");
    }
}
