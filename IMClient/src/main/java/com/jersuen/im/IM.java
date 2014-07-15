package com.jersuen.im;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author JerSuen
 */
public class IM extends Application {

    public static final String ACCOUNT_USERNAME = "account_username";
    public static final String ACCOUNT_PASSWORD = "account_password";
    public static final String ACCOUNT_AVATAR = "account_avatar";

    //public static final String HOST = "192.168.1.123";
    public static final String HOST = "192.168.199.123";
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

    public static Drawable getAvatar(String hashName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                String SDCardPath = Environment.getExternalStorageDirectory().getPath() + "/IMClient/avatar/";
                File file = new File(SDCardPath + hashName);
                FileInputStream fis = new FileInputStream(file);
                int length = fis.available();
                byte [] buffer = new byte[length];
                fis.read(buffer);
                fis.close();
                if (buffer.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                    return new BitmapDrawable(im.getResources(), bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static boolean saveAvatar(VCard vCard) {
        return saveFile(vCard.getAvatar(), vCard.getAvatarHash());
    }

    public static boolean saveFile(byte[] bytes, String fileName) {
       if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
           try {
               String SDCardPath = Environment.getExternalStorageDirectory().getPath() + "/IMClient/avatar/";
               File file = new File(SDCardPath + fileName);
               FileOutputStream fos = new FileOutputStream(file);
               fos.write(bytes);
               fos.close();
               return true;
           } catch (IOException e) {
               e.printStackTrace();
               return false;
           }
       }
        return false;
    }
}
