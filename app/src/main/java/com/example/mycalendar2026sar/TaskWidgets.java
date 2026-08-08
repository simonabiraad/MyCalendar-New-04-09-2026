package com.example.mycalendar2026sar;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import org.json.JSONArray;

public class TaskWidgets {

    private static int getTaskCount(Context context) {
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

    public static class Small extends BaseWidgetProvider {
        @Override protected String getWidgetType() { return "task"; }
        @Override protected String getWidgetTitle() { return "Tasks"; }
        @Override protected void updateWidget(Context context, AppWidgetManager mgr, int id) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_small_summary);
            views.setTextViewText(R.id.widget_title, "TASKS");
            views.setTextViewText(R.id.widget_count, String.valueOf(getTaskCount(context)));
            views.setTextViewText(R.id.widget_subtitle, "Pending");

            Intent intent = new Intent(context, TaskActivity.class);
            android.app.PendingIntent pi = android.app.PendingIntent.getActivity(context, 0, intent, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_root, pi);

            mgr.updateAppWidget(id, views);
        }
    }

    public static class Medium extends BaseWidgetProvider {
        @Override protected String getWidgetType() { return "task"; }
        @Override protected String getWidgetTitle() { return "Tasks List"; }
        @Override protected void updateWidget(Context context, AppWidgetManager mgr, int id) {
            mgr.updateAppWidget(id, getListRemoteViews(context, id, getWidgetType(), getWidgetTitle(), TaskActivity.class));
        }
    }

    public static class Large extends BaseWidgetProvider {
        @Override protected String getWidgetType() { return "task"; }
        @Override protected String getWidgetTitle() { return "Tasks List"; }
        @Override protected void updateWidget(Context context, AppWidgetManager mgr, int id) {
            mgr.updateAppWidget(id, getListRemoteViews(context, id, getWidgetType(), getWidgetTitle(), TaskActivity.class));
        }
    }
}
