package com.jersuen.im;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import com.jersuen.im.util.LogUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author JerSuen
 */
public class IM extends Application {

    // 头像文件夹
    public static final String AVATAR_PATH = "/IMClient/avatar/";

    public static final String ACCOUNT_JID = "account_jid";
    public static final String ACCOUNT_PASSWORD = "account_password";
    public static final String ACCOUNT_NICKNAME = "account_nickname";

    public static final String HOST = "192.168.1.123";
//    public static final String HOST = "192.168.199.123";
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

    public static byte[] getFile(String fileName, String directory) {
        FileInputStream fis = null;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String SDCardPath = Environment.getExternalStorageDirectory().getPath() + directory;
                File file = new File(SDCardPath, fileName);
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
        byte[] bytes = getFile(fileName, AVATAR_PATH);
        if (bytes != null) {
            if (bytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                return IM.Bitmap2Drawable(bitmap);
            }
        }
        return IM.im.getResources().getDrawable(R.drawable.ic_launcher);
    }

    public static boolean saveAvatar(byte[] bytes, String fileName) {
        if (bytes == null || TextUtils.isEmpty(fileName)) {
            return false;
        }
        return saveFile(bytes, fileName, AVATAR_PATH);
    }

    /**
     * 保存文件
     */
    public static boolean saveFile(byte[] bytes, String fileName, String directory) {
        FileOutputStream fos = null;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String SDCardPath = Environment.getExternalStorageDirectory().getPath() + directory;
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

    /**
     * 获取拍照文件
     * @return
     *      拍照文件
     */
    public static File getCameraFile() {
        // 使用系统当前日期加以调整作为照片的名称
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        // 拍照文件
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), dateFormat.format(date) + ".jpg");
    }

    /**
     * 启动系统裁剪
     * @param activity
     * @param data
     * @param picCode
     */
    public static void doCropPhoto(Activity activity, Uri data, int picCode) {
        Intent intent = getCropImageIntent(data);
        activity.startActivityForResult(intent, picCode);
    }

    public static Intent getCropImageIntent(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 400);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        return intent;
    }

    /**
     * Bitmap转byte[]
     * @param bitmap
     *          要转换的bitmap文件
     * @return
     *          转换好的byte[]
     */
    public static byte[] Bitmap2Bytes(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Drawable Bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(im.getResources(), bitmap);
    }
}
