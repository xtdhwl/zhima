package com.zhima.base.utils;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences.Editor;

/**
* @ClassName: SettingHelper
* @Description: Shared Preferences读写数据
* @author liubingsr
* @date 2012-5-22 上午11:53:36
*
*/
public final class SettingHelper {
	public final static String TAG = "SettingHelper";
    private final static String DATA_NAME = "zhima.dat";
    private final static String SPLITCHAR = ",";

    public enum Field {
    	/**
    	 * app首次启动标志
    	 */
    	APP_FIRSTSTARTUP,
        LOGIN_SUCCESS,
        ACCOUNT_TYPE,
        ACCOUNT_NAME,
        ACCOUNT_PASSWORD,
        ACCESS_TOKEN,
        ACCOUNT_DISPLAYNAME,
        ACCOUNT_USERID,
        ACCOUNT_CITYID,
        ACCOUNT_ACCOUNT_ID,
        ACCOUNT_SIGNATURE,
        ACCOUNT_ZMUSER_Id,
        ACCOUNT_GENDER,
        /**
         * 空间字典最后更新时间
         */
        DICT_SPACE_TIMESTAMP,
        /**
         * 地区字典最后更新时间
         */
        DICT_CITY_TIMESTAMP,
        /**
         * 心跳请求间隔时间
         */
        PING_INTERVAL,
        /**
         * 固定的Server url
         */
        FIXED_SERVER_URL,
        /**
         * rest根路径
         */
        SERVER_REST_ROOTPATH,
        /**
         * ssl rest根路径
         */
        SERVER_SSL_REST_ROOTPATH,
        /**
         * 分发渠道
         */
        PUBLISH_CHANNEL,
        /**
         * 资源server
         */
        RESOURCE_PATH,
        /**
         * 版本号
         */
        VERSION,
        /**************************** 设置  *********************/
        /** 扫码提示音设置 */
        SETTING_SCAN_WARNINGTONE,
        /** 扫码震动设置 */
        SETTING_SCAN_SHAKE,
        
        /** 图片优化 */
        SETTING_IMAGE_OPTIMIZE,
        
        /** 是否提示更新 */
        ISPROMPT_UPDATE_VERSION,
        
        /*-----------------------------检索------------------------------*/
        /** 当前省份 */
        CURRENT_PROVINCE,
        /** 当前城市 */
        CURRENT_CITY,
        /** 当前区域 */
        CURRENT_AREA,
        /***/
        ACCESS_TOKEN_TYPE,
        /**用户头像*/
        ACCOUNT_IMAGE_URL,
        /**
         * 知天使职业字典最后更新时间
         */
        DICT_IDOL_JOB_TIMESTAMP,
        
        IS_NEED_GET_MYSELF,
    };
    
    public static int getInt(Context context, Field field, int defaultValue) {
        return context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE).getInt(field.name(), defaultValue);
    }

    public static void setInt(Context context, Field field, int value) {
        Editor editor = context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(field.name(), value);
        editor.commit();
    }    

    public static String getString(Context context, Field field, String defaultValue) {
        return context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE).getString(field.name(), defaultValue);
    }

    public static void setString(Context context, Field field, String value) {
        Editor editor = context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(field.name(), value);
        editor.commit();
    }

    public static long getLong(Context context, Field field, long defaultValue) {
        return context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE).getLong(field.name(), defaultValue);
    }

    public static void setLong(Context context, Field field, long value) {
        Editor editor = context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE).edit();
        editor.putLong(field.name(), value);
        editor.commit();
    }

    public static boolean getBoolean(Context context, Field field, boolean defaultValue) {
        return context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE).getBoolean(field.name(), defaultValue);
    }

    public static void setBoolean(Context context, Field field, boolean value) {
        Editor editor = context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(field.name(), value);
        editor.commit();
    }
    
    public static void setArraylong(Context context, Field field, ArrayList<Long> arrayLong) {
        if (arrayLong != null && arrayLong.size() > 0) {
            String str = "";
            for (int index = 0, count = arrayLong.size(); index < count; ++index) {
                str = str + arrayLong.get(index).toString() + SPLITCHAR;
            }
            Editor editor = context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(field.name(), str);
            editor.commit();
        }
    }
    
    public static void removeKey(Context context,Field field){
    	Editor editor = context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE).edit();
    	editor.remove(field.name());
    	editor.commit();
    }

    public static ArrayList<Long> getArrayLong(Context context, Field field, String defaultValue) {
        ArrayList<Long> arrayLong = new ArrayList<Long>();
        String str = context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE).getString(field.name(), defaultValue);
        if (str != null) {
            String[] ids = str.split(SPLITCHAR);
            for (int index = 0; index < ids.length; ++index) {
                long id = Long.parseLong(ids[index]);
                arrayLong.add(id);
            }
        }
        return arrayLong;
    }

    public static void setArrayString(Context context, Field field, ArrayList<String> displayList) {
        String str = "";
        Integer num = 0;
        for (int index = 0, count = displayList.size(); index < count; ++index) {
            str = str + displayList.get(index) + SPLITCHAR;
            num++;
        }
        Editor editor = context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(field.name(), str);
        editor.commit();
    }

    public static ArrayList<String> getArrayString(Context context, Field field, String defaultValue) {
        ArrayList<String> displayList = new ArrayList<String>();
        String str = context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE).getString(field.name(), defaultValue);
        if (str != null) {
            String[] ids = str.split(SPLITCHAR);
            for (int index = 0; index < ids.length; index++) {
                if (!ids[index].equals(""))
                    displayList.add(ids[index]);
            }
        }
        return displayList;
    }
    
}
