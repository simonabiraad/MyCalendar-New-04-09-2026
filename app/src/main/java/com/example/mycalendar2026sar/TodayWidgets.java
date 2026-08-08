package com.example.mycalendar2026sar;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import java.util.Map;

public class TodayWidgets {

    private static int getTodayCount(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("CalendarNotes", Context.MODE_PRIVATE);
        String todayKey = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(new java.util.Date());
        String notes = prefs.getString(todayKey, "");
        if (notes.isEmpty()) return 0;
        return notes.split("\n").length;
    }

    public static class Small extends BaseWidgetProvider {
        @Override protected String getWidgetType() { return "today"; }
        @Override protected String getWidgetTitle() { return "Today"; }
        @Override protected void updateWidget(Context context, AppWidgetManager mgr, int id) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_small_summary);
            views.setTextViewText(R.id.widget_title, "TODAY");
            views.setTextViewText(R.id.widget_count, String.valueOf(getTodayCount(context)));
            views.setTextViewText(R.id.widget_subtitle, "Notes");
            
            Intent intent = new Intent(context, MainActivity.class);
            android.app.PendingIntent pi = android.app.PendingIntent.getActivity(context, 0, intent, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_root, pi);
            
            mgr.updateAppWidget(id, views);
        }
    }

    public static class Medium extends BaseWidgetProvider {
        @Override protected String getWidgetType() { return "today"; }
        @Override protected String getWidgetTitle() { return "Today's Notes"; }
        @Override protected void updateWidget(Context context, AppWidgetManager mgr, int id) {
            mgr.updateAppWidget(id, getListRemoteViews(context, id, getWidgetType(), getWidgetTitle(), MainActivity.class));
        }
    }

    public static class Large extends BaseWidgetProvider {
        @Override protected String getWidgetType() { return "today"; }
        @Override protected String getWidgetTitle() { return "Today's Notes"; }
        @Override protected void updateWidget(Context context, AppWidgetManager mgr, int id) {
            mgr.updateAppWidget(id, getListRemoteViews(context, id, getWidgetType(), getWidgetTitle(), MainActivity.class));
        }
    }
}
