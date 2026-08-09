package com.example.mycalendar2026sar;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import org.json.JSONArray;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class SARCalendarWidget4x4 extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager mgr, int[] ids) {
        for (int id : ids) {
            updateWidget(context, mgr, id);
        }
    }

    private void updateWidget(Context context, AppWidgetManager mgr, int id) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_sar_4x4);
        Calendar now = Calendar.getInstance();

        // 1. Set Main Date
        views.setTextViewText(R.id.widget_day_name, new SimpleDateFormat("EEEE", Locale.getDefault()).format(now.getTime()).toUpperCase());
        views.setTextViewText(R.id.widget_day_number, String.valueOf(now.get(Calendar.DAY_OF_MONTH)));
        views.setTextViewText(R.id.widget_month_year, new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(now.getTime()).toUpperCase());

        // 2. Calculate Counts
        views.setTextViewText(R.id.count_today, String.valueOf(getTodayCount(context)));
        views.setTextViewText(R.id.count_sticky, String.valueOf(getStickyCount(context)));
        views.setTextViewText(R.id.count_tasks, String.valueOf(getTaskCount(context)));

        // 3. Set Click Intent to open App
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_root, pi);

        mgr.updateAppWidget(id, views);
    }

    private int getTodayCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CalendarNotes", Context.MODE_PRIVATE);
        String key = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());
        String notes = prefs.getString(key, "");
        return notes.isEmpty() ? 0 : notes.split("\n").length;
    }

    private int getStickyCount(Context context) {
        SharedPreferences securePrefs = context.getSharedPreferences("SecureBoxNotes", Context.MODE_PRIVATE);
        SharedPreferences securityPrefs = context.getSharedPreferences("SecuritySettings", Context.MODE_PRIVATE);
        Map<String, ?> all = securePrefs.getAll();
        int count = 0;
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            // Only count notes that aren't category protected (Public Notes)
            if (!securityPrefs.getBoolean("cat_protected_" + entry.getKey(), false)) {
                String val = entry.getValue().toString();
                if (!val.isEmpty()) count += val.split("###NOTE_SEP###").length;
            }
        }
        return count;
    }

    private int getTaskCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE);
        String json = prefs.getString("tasks", null);
        if (json == null) return 0;
        try {
            JSONArray arr = new JSONArray(json);
            int pending = 0;
            for (int i = 0; i < arr.length(); i++) {
                if (!arr.getJSONObject(i).getBoolean("completed")) pending++;
            }
            return pending;
        } catch (Exception e) { return 0; }
    }
}
