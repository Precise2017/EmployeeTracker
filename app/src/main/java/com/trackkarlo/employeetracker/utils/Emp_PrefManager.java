package com.trackkarlo.employeetracker.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by precise on 4/25/2017.
 */

public class Emp_PrefManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    private static final String PREF_NAME = "raas_location";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String REGULAR_FONT_FAMILY_SELECTED = "regular_font_family_selected";
    private static final String BOLD_FONT_FAMILY_SELECTED = "bold_font_family_selected";

    private static final String SECOND_APP_PACKAGE_NAME = "SECOND_APP_PACKAGE_NAME";
    private static final String SECOND_APP_SERVICE_NAME = "SECOND_APP_SERVICE_NAME";
    private static final String SECOND_APP_APK_NAME_IN_ASSEST = "SECOND_APP_APK_NAME_IN_ASSEST";
    private static final String SECOND_APP_APK_VERSION = "SECOND_APP_APK_VERSION";


    private static final String IS_LOGGED_IN = "logged_in";
    private static final String USER_NAME = "user_name";
    private static final String USER_ID = "user_id";
    private static final String IMEI = "imei";
    private static final String EMAIL = "email";
    private static final String USER_IMAGE = "user_image";
    private static final String USER_TYPE = "user_type";

    public Emp_PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }


    public void setSECOND_APP_PACKAGE_NAME(String type) {
        editor.putString(SECOND_APP_PACKAGE_NAME, type);
        editor.apply();
    }

    public void setSECOND_APP_SERVICE_NAME(String type) {
        editor.putString(SECOND_APP_SERVICE_NAME, type);
        editor.apply();
    }

    public void setSECOND_APP_APK_NAME_IN_ASSEST(String type) {
        editor.putString(SECOND_APP_APK_NAME_IN_ASSEST, type);
        editor.apply();
    }

    public void setSECOND_APP_APK_VERSION(int type) {
        editor.putInt(SECOND_APP_APK_VERSION, type);
        editor.apply();
    }

    public String getSECOND_APP_PACKAGE_NAME() {
        return pref.getString(SECOND_APP_PACKAGE_NAME, "com.trackkarlo.emptracker");
    }

    public String getSECOND_APP_SERVICE_NAME() {
        return pref.getString(SECOND_APP_SERVICE_NAME, "com.trackkarlo.emptracker.TrackKarloEmpService");
    }

    public String getSECOND_APP_APK_NAME_IN_ASSEST() {
        return pref.getString(SECOND_APP_APK_NAME_IN_ASSEST, "SDKLibrary.apk");
    }

    public int getSECOND_APP_APK_VERSION() {
        return pref.getInt(SECOND_APP_APK_VERSION, 23);
    }

    public void setRegularFontFamily(String fontFamily) {
        editor.putString(REGULAR_FONT_FAMILY_SELECTED, fontFamily);
        editor.apply();
    }

    public String getRegularFontFamilySelected() {
        return pref.getString(REGULAR_FONT_FAMILY_SELECTED, Emp_Constants.DEFAULT_REGULAR_FONT_FAMILY);
    }

    public void setBoldFontFamily(String fontFamily) {
        editor.putString(BOLD_FONT_FAMILY_SELECTED, fontFamily);
        editor.apply();
    }

    public String getBoldFamilySelected() {
        return pref.getString(BOLD_FONT_FAMILY_SELECTED, Emp_Constants.DEFAULT_BOLD_FONT_FAMILY);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.apply();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

//    public void setIS_LOGGED_IN(Boolean imei) {
//        editor.putBoolean(IS_LOGGED_IN, imei);
//        editor.apply();
//    }
//
//    public Boolean getIS_LOGGED_IN() {
//        return pref.getBoolean(IS_LOGGED_IN, false);
//    }
//
//
//    public void setUSER_NAME(String user_name) {
//        editor.putString(USER_NAME, user_name);
//        editor.apply();
//    }
//    public String getUSER_NAME() {
//        return pref.getString(USER_NAME, "");
//    }
//
//
//    public void setUSER_ID(String user_id) {
//        editor.putString(USER_ID, user_id);
//        editor.apply();
//    }
//    public String getUSER_ID() {
//        return pref.getString(USER_ID, "");
//    }
//
//
//    public void setIMEI(String imei) {
//        editor.putString(IMEI, imei);
//        editor.apply();
//    }
//    public String getIMEI() {
//        return pref.getString(IMEI, "");
//    }
//
//
//    public void setEMAIL(String email) {
//        editor.putString(EMAIL, email);
//        editor.apply();
//    }
//    public String getEMAIL() {
//        return pref.getString(EMAIL, "");
//    }
//
//
//    public void setUSER_IMAGE(String user_image) {
//        editor.putString(USER_IMAGE, user_image);
//        editor.apply();
//    }
//    public String getUSER_IMAGE() {
//        return pref.getString(USER_IMAGE, "");
//    }
//
//
//    public void setUSER_TYPE(String user_type) {
//        editor.putString(USER_TYPE, user_type);
//        editor.apply();
//    }
//    public String getUSER_TYPE() {
//        return pref.getString(USER_TYPE, "");
//    }

}
