package com.example.mycalendar2026sar;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class DenomManager {
    private static final String PREF_NAME = "DenomPrefs";
    private static final String KEY_DENOMS = "denom_list";

    public static class Denomination {
        public int value;
        public boolean enabled;

        public Denomination(int value, boolean enabled) {
            this.value = value;
            this.enabled = enabled;
        }
    }

    public static List<Denomination> getDenominations(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_DENOMS, null);
        List<Denomination> list = new ArrayList<>();
        if (json == null) {
            // Default list
            int[] defaults = {100000, 50000, 20000, 10000, 5000, 1000, 500, 250};
            for (int d : defaults) list.add(new Denomination(d, true));
            saveDenominations(context, list);
        } else {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    list.add(new Denomination(obj.getInt("value"), obj.getBoolean("enabled")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static void saveDenominations(Context context, List<Denomination> list) {
        try {
            JSONArray array = new JSONArray();
            for (Denomination d : list) {
                JSONObject obj = new JSONObject();
                obj.put("value", d.value);
                obj.put("enabled", d.enabled);
                array.put(obj);
            }
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    .edit().putString(KEY_DENOMS, array.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
