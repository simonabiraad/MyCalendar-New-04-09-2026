package com.example.mycalendar2026sar;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class WidgetUtils {

    public static void updateAllWidgets(Context context) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        
        Class<?>[] classes = {
            SARCalendarWidget4x4.class
        };

        for (Class<?> cls : classes) {
            int[] ids = mgr.getAppWidgetIds(new ComponentName(context, cls));
            if (ids.length > 0) {
                Intent intent = new Intent(context, cls);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                context.sendBroadcast(intent);
            }
        }
    }
}
