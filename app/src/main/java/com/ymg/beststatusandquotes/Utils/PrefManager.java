package com.ymg.beststatusandquotes.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {


    private static final String PREF_NAME = "status_app";
    String TAG_NIGHT_MODE = "nightmode";
    Context _context;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    public static final String URL = "https://yashchouhan.com/verify/api.php?nid=wallx";

    public PrefManager(Context context) {
        this._context = context;
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
        this.pref = sharedPreferences;
        this.editor = sharedPreferences.edit();
    }

    //post
    public String getPostToken() {
        return pref.getString("post_token", null);
    }

    public void updatePostToken(String post_token) {
        editor.putString("post_token", post_token);
        editor.apply();
    }

    public void resetPostToken() {
        pref.edit().remove("post_token").apply();
    }

    //category detail
    public String getCategoryDetailToken() {
        return pref.getString("category_detail_token", null);
    }

    public void updateCategoryDetailToken(String category_detail_token) {
        editor.putString("category_detail_token", category_detail_token);
        editor.apply();
    }

    public void resetCategoryDetailToken() {
        pref.edit().remove("category_detail_token").apply();
    }

    public void setBoolean(String str, Boolean bool) {
        this.editor.putBoolean(str, bool.booleanValue());
        this.editor.commit();
    }

    public void setString(String str, String str2) {
        this.editor.putString(str, str2);
        this.editor.commit();
    }

    public void setInt(String str, int i) {
        this.editor.putInt(str, i);
        this.editor.commit();
    }

    public boolean getBoolean(String str) {
        return this.pref.getBoolean(str, true);
    }

    public void remove(String str) {
        if (this.pref.contains(str)) {
            this.editor.remove(str);
            this.editor.commit();
        }
    }

    public String getString(String str) {
        return this.pref.contains(str) ? this.pref.getString(str, null) : "";
    }

    public int getInt(String str) {
        return this.pref.getInt(str, 0);
    }


    //save
    public void setNightModeState(Boolean state){
        SharedPreferences.Editor editor= pref.edit();
        editor.putBoolean("NightMode",state);
        editor.commit();
    }

    //load
    public Boolean loadNightModeState(){
        Boolean state = pref.getBoolean("NightMode",false);
        return state;
    }

}
