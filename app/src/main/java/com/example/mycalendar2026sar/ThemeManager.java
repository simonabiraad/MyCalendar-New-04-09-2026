package com.example.mycalendar2026sar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {

    private static final String PREFS_NAME = "ThemePrefs";
    private static final String KEY_DARK_MODE = "is_dark_mode";

    /**
     * Applies saved theme on app startup or activity creation.
     */
    public static void applyTheme(Context context) {
        boolean isDark = isDarkMode(context);
        int mode = isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        if (AppCompatDelegate.getDefaultNightMode() != mode) {
            AppCompatDelegate.setDefaultNightMode(mode);
        }
    }

    /**
     * Checks whether Dark Mode is currently active (default true = Dark Mode ON).
     */
    public static boolean isDarkMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_DARK_MODE, true); // Default is Dark Mode
    }

    /**
     * Sets Dark Mode state and immediately applies theme globally across activities.
     */
    public static void setDarkMode(Context context, boolean isDark, Activity currentActivity) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_DARK_MODE, isDark).apply();

        int mode = isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(mode);

        if (currentActivity != null && !currentActivity.isFinishing()) {
            currentActivity.recreate();
        }
    }
}
