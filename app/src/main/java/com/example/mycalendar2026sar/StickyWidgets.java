package com.example.mycalendar2026sar;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import java.util.Map;

public class StickyWidgets {

    private static int getStickyCount(Context context) {
        SharedPreferences securePrefs = context.getSharedPreferences("SecureBoxNotes", Context.MODE_PRIVATE);
        SharedPreferences securityPrefs = context.getSharedPreferences("SecuritySettings", Context.MODE_PRIVATE);
        Map<String, ?> all = securePrefs.getAll();
        int count = 0;
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            if (!securityPrefs.getBoolean("cat_protected_" + entry.getKey(), false)) {
                String val = entry.getValue().toString();
                if (!val.isEmpty()) count += val.split("###NOTE_SEP###").length;
            }
        }
        return count;
    }

    public static class Small extends BaseWidgetProvider {
        @Override protected String getWidgetType() { return "sticky"; }
        @Override protected String getWidgetTitle() { return "Sticky"; }
        @Override protected void updateWidget(Context context, AppWidgetManager mgr, int id) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_small_summary);
            views.setTextViewText(R.id.widget_title, "STICKY");
            views.setTextViewText(R.id.widget_count, String.valueOf(getStickyCount(context)));
            views.setTextViewText(R.id.widget_subtitle, "Public Notes");

            Intent intent = new Intent(context, SecureBoxActivity.class);
            android.app.PendingIntent pi = android.app.PendingIntent.getActivity(context, 0, intent, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_root, pi);

            mgr.updateAppWidget(id, views);
        }
    }

    public static class Medium extends BaseWidgetProvider {
        @Override protected String getWidgetType() { return "sticky"; }
        @Override protected String getWidgetTitle() { return "Sticky Notes"; }
        @Override protected void updateWidget(Context context, AppWidgetManager mgr, int id) {
            mgr.updateAppWidget(id, getListRemoteViews(context, id, getWidgetType(), getWidgetTitle(), SecureBoxActivity.class));
        }
    }

    public static class Large extends BaseWidgetProvider {
        @Override protected String getWidgetType() { return "sticky"; }
        @Override protected String getWidgetTitle() { return "Sticky Notes"; }
        @Override protected void updateWidget(Context context, AppWidgetManager mgr, int id) {
            mgr.updateAppWidget(id, getListRemoteViews(context, id, getWidgetType(), getWidgetTitle(), SecureBoxActivity.class));
        }
    }
}
