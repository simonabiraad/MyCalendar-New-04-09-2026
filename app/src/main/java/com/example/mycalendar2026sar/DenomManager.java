package com.example.mycalendar2026sar;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class DenomManager {
    private static final String PREF_NAME = "DenomPrefs";

    public static class Denomination {
        public double value;
        public boolean enabled;

        public Denomination(double value, boolean enabled) {
            this.value = value;
            this.enabled = enabled;
        }
    }

    public static List<Denomination> getDenominations(Context context, String countryCode) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String key = "denom_list_" + countryCode;
        String json = prefs.getString(key, null);
        List<Denomination> list = new ArrayList<>();
        
        if (json == null) {
            // Default lists based on country
            double[] defaults;
            if ("LB".equals(countryCode)) {
                defaults = new double[]{100000, 50000, 20000, 10000, 5000, 1000, 500, 250};
            } else {
                defaults = new double[]{100, 50, 20, 10, 5, 2, 1, 0.50, 0.25, 0.10, 0.05, 0.01};
            }
            
            for (double d : defaults) list.add(new Denomination(d, true));
            saveDenominations(context, countryCode, list);
        } else {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    list.add(new Denomination(obj.getDouble("value"), obj.getBoolean("enabled")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static void saveDenominations(Context context, String countryCode, List<Denomination> list) {
        try {
            String key = "denom_list_" + countryCode;
            JSONArray array = new JSONArray();
            for (Denomination d : list) {
                JSONObject obj = new JSONObject();
                obj.put("value", d.value);
                obj.put("enabled", d.enabled);
                array.put(obj);
            }
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    .edit().putString(key, array.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
