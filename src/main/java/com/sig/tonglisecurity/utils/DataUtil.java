package com.sig.tonglisecurity.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 存取小型数据
 */
public class DataUtil {
    public static String RMB_INFO = "remember_info";
    private static String NAVIGATE = "navigate";
    private static String UPLOAD_INFO = "upload_info";

    /**
     * 创建SharePreference
     *
     * @param context 上下文
     * @return SharedPreference对象
     */
    public static SharedPreferences getInfoSharedPreferences(Context context) {
        return context.getSharedPreferences(RMB_INFO, Context.MODE_PRIVATE);
    }

    /**
     * 存储向导
     *
     * @param context  上下文
     * @param has_help 是否已向导
     */
    public static void saveNavigate(Context context, boolean has_help) {
        SharedPreferences.Editor localEditor = getInfoSharedPreferences(context).edit();
        localEditor.putBoolean(NAVIGATE, has_help);
        localEditor.apply();
    }

    /**
     * 获取向导
     *
     * @param context 上下文
     * @return 是否已经向导
     */
    public static boolean getNavigate(Context context) {
        return getInfoSharedPreferences(context).getBoolean(NAVIGATE, false);
    }

    /**
     * 存取是否已经上传过手机信息
     *
     * @param has_upload 是否已经上传
     */
    public static void saveUploadInfo(Context c, boolean has_upload) {
        SharedPreferences.Editor localEditor = getInfoSharedPreferences(c).edit();
        localEditor.putBoolean(UPLOAD_INFO, has_upload);
        localEditor.apply();
    }

    /**
     * 获取是否已经上传手机信息标识
     *
     * @param context 上下文
     * @return 是否已经上传手机信息标识
     */
    public static boolean getUploadInfo(Context context) {
        // 这里是通过  SharedPreferences 拿到一个值名为: upload_info 的键值
        return getInfoSharedPreferences(context).getBoolean(UPLOAD_INFO, false);
    }
}
