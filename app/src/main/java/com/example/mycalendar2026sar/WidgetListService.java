package com.example.mycalendar2026sar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WidgetListService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context context;
    private final String type;
    private final List<String> items = new ArrayList<>();
    private final List<String> titles = new ArrayList<>();

    public WidgetRemoteViewsFactory(Context context, Intent intent) {
        this.context = context;
        this.type = intent.getStringExtra("type");
    }

    @Override public void onCreate() {}
    @Override public void onDestroy() { items.clear(); titles.clear(); }
    @Override public int getCount() { return items.size(); }
    @Override public RemoteViews getLoadingView() { return null; }
    @Override public int getViewTypeCount() { return 1; }
    @Override public long getItemId(int pos) { return pos; }
    @Override public boolean hasStableIds() { return true; }

    @Override
    public void onDataSetChanged() {
        items.clear();
        titles.clear();
        if ("today".equals(type)) loadToday();
        else if ("sticky".equals(type)) loadSticky();
        else if ("task".equals(type)) loadTask();
    }

    private void loadToday() {
        SharedPreferences prefs = context.getSharedPreferences("CalendarNotes", Context.MODE_PRIVATE);
        String todayKey = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(new java.util.Date());
        String notes = prefs.getString(todayKey, "");
        if (!notes.isEmpty()) {
            for (String n : notes.split("\n")) {
                if (!n.trim().isEmpty()) {
                    items.add(n);
                    titles.add(""); // No subtitle for today notes
                }
            }
        }
    }

    private void loadSticky() {
        SharedPreferences securePrefs = context.getSharedPreferences("SecureBoxNotes", Context.MODE_PRIVATE);
        SharedPreferences securityPrefs = context.getSharedPreferences("SecuritySettings", Context.MODE_PRIVATE);
        SharedPreferences catPrefs = context.getSharedPreferences("SecureBoxCategories", Context.MODE_PRIVATE);
        
        Map<String, ?> all = securePrefs.getAll();
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            String catKey = entry.getKey();
            if (!securityPrefs.getBoolean("cat_protected_" + catKey, false)) {
                String catName = catPrefs.getString(catKey, "Note");
                String val = entry.getValue().toString();
                if (!val.isEmpty()) {
                    for (String note : val.split("###NOTE_SEP###")) {
                        if (note.contains("###TITLE_SEP###")) {
                            String[] parts = note.split("###TITLE_SEP###");
                            titles.add(catName + ": " + parts[0]);
                            items.add(parts.length > 1 ? parts[1] : "");
                        } else {
                            titles.add(catName);
                            items.add(note);
                        }
                    }
                }
            }
        }
    }

    private void loadTask() {
        SharedPreferences prefs = context.getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE);
        String json = prefs.getString("tasks", null);
        if (json == null) return;
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                boolean done = obj.getBoolean("completed");
                items.add((done ? "✓ " : "□ ") + obj.getString("text"));
                titles.add("");
            }
        } catch (Exception ignored) {}
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position >= items.size()) return null;
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
        
        String t = titles.get(position);
        if (t != null && !t.isEmpty()) {
            rv.setTextViewText(R.id.item_title, t);
            rv.setViewVisibility(R.id.item_title, android.view.View.VISIBLE);
        } else {
            rv.setViewVisibility(R.id.item_title, android.view.View.GONE);
        }
        
        rv.setTextViewText(R.id.item_text, items.get(position));
        
        Intent fillInIntent = new Intent();
        rv.setOnClickFillInIntent(R.id.item_text, fillInIntent);
        return rv;
    }
}
