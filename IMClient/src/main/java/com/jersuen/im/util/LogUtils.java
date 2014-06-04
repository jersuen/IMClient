package com.jersuen.im.util;



import android.util.Log;

/**
 * 日志工具类
 * @author JerSuen
 */
public class LogUtils {
	// debug控制
	public static final boolean isDebug = true;

    public static void LOGD(Class<?> cls, String message) {
    	if (isDebug && cls != null) {
			Log.d(cls.getSimpleName(), message);
		}
    }

    public static void LOGD(Class<?> cls, String message, Throwable cause) {
        if (isDebug && cls != null) {
			Log.d(cls.getSimpleName(), message, cause);
		}
    }

    public static void LOGV(Class<?> cls, String message) {
    	if (isDebug && cls != null) {
			Log.v(cls.getSimpleName(), message);
		}
    }

    public static void LOGV(Class<?> cls, String message, Throwable cause) {
    	if (isDebug && cls != null) {
			Log.v(cls.getSimpleName(), message, cause);
		}
    }

    public static void LOGI(Class<?> cls, String message) {
    	if (isDebug && cls != null) {
			Log.i(cls.getSimpleName(), message);
		}
    }

    public static void LOGI(Class<?> cls, String message, Throwable cause) {
    	if (isDebug && cls != null) {
			Log.i(cls.getSimpleName(), message, cause);
		}
    }

    public static void LOGW(Class<?> cls, String message) {
    	if (isDebug && cls != null) {
			Log.w(cls.getSimpleName(), message);
		}
    }

    public static void LOGW(Class<?> cls, String message, Throwable cause) {
    	if (isDebug && cls != null) {
			Log.w(cls.getSimpleName(), message, cause);
		}
    }

    public static void LOGE(Class<?> cls, String message) {
    	if (isDebug && cls != null) {
			Log.e(cls.getSimpleName(), message);
		}
    }

    public static void LOGE(Class<?> cls, String message, Throwable cause) {
    	if (isDebug && cls != null) {
			Log.e(cls.getSimpleName(), message, cause);
		}
    }
}
