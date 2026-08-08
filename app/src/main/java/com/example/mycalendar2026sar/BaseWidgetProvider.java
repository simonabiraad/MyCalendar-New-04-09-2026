package com.example.mycalendar2026sar;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public abstract class BaseWidgetProvider extends AppWidgetProvider {

    protected abstract String getWidgetType();
    protected abstract String getWidgetTitle();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    protected void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        // Find which layout to use (Summary vs List)
        // Since we have 9 providers, we can assume the provider class knows its size type
        // For simplicity, we check if the layout contains widget_list
    }

    protected RemoteViews getListRemoteViews(Context context, int appWidgetId, String type, String title, Class<?> targetActivity) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_view);
        views.setTextViewText(R.id.widget_header, title);

        Intent intent = new Intent(context, WidgetListService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra("type", type);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        views.setRemoteAdapter(R.id.widget_list, intent);
        views.setEmptyView(R.id.widget_list, R.id.empty_view);

        Intent mainIntent = new Intent(context, targetActivity);
        PendingIntent pi = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_root, pi);

        return views;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            int[] ids = mgr.getAppWidgetIds(new ComponentName(context, getClass()));
            mgr.notifyAppWidgetViewDataChanged(ids, R.id.widget_list);
            onUpdate(context, mgr, ids);
        }
    }
}
