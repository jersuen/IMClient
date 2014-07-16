package com.jersuen.im;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;
import com.jersuen.im.util.LogUtils;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.*;

/**
 * @author JerSuen
 */
public class IM extends Application {

    public static final String ACCOUNT_JID = "account_jid";
    public static final String ACCOUNT_PASSWORD = "account_password";
    public static final String ACCOUNT_NICKNAME = "account_nickname";

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
     *
     * @param key
     * @param value
     * @return 插入结果
     */
    public static boolean putString(String key, String value) {
        SharedPreferences settings = im.getSharedPreferences("im_account", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * 获取字符串
     *
     * @param key
     * @return 默认值为空字符串
     */
    public static String getString(String key) {
        SharedPreferences settings = im.getSharedPreferences("im_account", MODE_PRIVATE);
        return settings.getString(key, "");
    }

    public static byte[] getFile(String fileName) {
        FileInputStream fis = null;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String SDCardPath = Environment.getExternalStorageDirectory().getPath() + "/IMClient/avatar/";
                File file = new File(SDCardPath,fileName);
                fis = new FileInputStream(file);
            } else {
                fis = im.openFileInput(fileName);
            }
            int length = fis.available();
            byte[] buffer = new byte[length];
            fis.read(buffer);
            fis.close();
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.LOGE(IM.class, "getFile()" + e.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Drawable getAvatar(String fileName) {
        byte[] bytes = getFile(fileName);
        if (bytes != null) {
            if (bytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                return new BitmapDrawable(im.getResources(), bitmap);
            }
        }
        return null;
    }

    public static boolean saveAvatar(byte[] bytes, String fileName) {
        if (bytes == null || TextUtils.isEmpty(fileName)) {
            return false;
        }
        return saveFile(bytes,fileName);
    }

    /**
     * 保存文件
     */
    public static boolean saveFile(byte[] bytes, String fileName) {
        FileOutputStream fos = null;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String SDCardPath = Environment.getExternalStorageDirectory().getPath() + "/IMClient/avatar/";
                File fileDirectory = new File(SDCardPath);
                if (!fileDirectory.exists()) {
                    fileDirectory.mkdirs();
                }
                File file = new File(fileDirectory, fileName);
                fos = new FileOutputStream(file);
            } else {
                fos = im.openFileOutput(fileName, MODE_PRIVATE);
            }
            fos.write(bytes);
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.LOGE(IM.class, "saveFile()" + e.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
