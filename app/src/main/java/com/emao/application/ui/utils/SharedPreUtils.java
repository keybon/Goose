package com.emao.application.ui.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.emao.application.ui.application.MainApp;

/**
 *
 * @author keybon
 */

public class SharedPreUtils {


    public static final String SP_OPENID = "openid";
    public static final String SP_USERID = "userid";
    public static final String SP_ACCESS_TOKEN = "access_token";
    public static final String SP_NICKNAME = "nickname";
    public static final String SP_SEX = "sex";
    public static final String SP_HEADIMGURL = "headimgurl";
    public static final String SP_MOBILE = "mobile";
    public static final String SP_SIGNATURE = "signature";
    public static final String SP_COLLECTION = "collection";


    private static int SP_MODE_TYPE = Context.MODE_PRIVATE;

    private static SharedPreferences instance;
    private static SharedPreferences.Editor editor;

    public static SharedPreferences getInstance(){
        if (instance == null) {
            synchronized (SharedPreUtils.class){
                if (instance == null){
                    instance = MainApp.IMApp.getSharedPreferences(Constants.SP_FILE_NAME,SP_MODE_TYPE);
                }
            }
        }
        return instance;
    }

    /**
     * 根据类型调用不同的保存方法
     *
     * @param key
     *            添加的键
     * @param value
     *            添加的值
     * @return 是否添加成功（可以使用apply提交）
     */
    public static boolean put(String key, Object value) {
        if (instance == null) {
            instance = getInstance();
        }
        editor = instance.edit();
        if (value == null) {
            editor.putString(key, null);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else {
            editor.putString(key, value.toString());
        }
        return editor.commit();
    }

    public static <T> T get(String key,
                            Object defaultValue) {
        if (instance == null) {
            instance = getInstance();
        }
        if (defaultValue instanceof String) {
            return (T) instance.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return (T) new Integer((instance.getInt(key, (Integer) defaultValue)));
        } else if (defaultValue instanceof Boolean) {
            return (T) new Boolean(instance.getBoolean(key, (Boolean) defaultValue));
        } else if (defaultValue instanceof Float) {
            return (T) new Float(instance.getFloat(key, (Float) defaultValue));
        } else if (defaultValue instanceof Long) {
            return (T) new Long(instance.getLong(key, (Long) defaultValue));
        }
        return null;
    }

    /**
     * 清除数据
     * @param context
     * @return 是否清除成功(如果不关注结果，可以使用apply)
     */
    public static boolean clear(Context context) {
        if (instance == null) {
            instance = getInstance();
        }
        return instance.edit().clear().commit();
    }

}
