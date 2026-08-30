package com.example.mycalendar2026sar;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class CountryManager {

    public static class Country {
        public String name;
        public String code; // ISO code
        public String currency;
        public String flag;

        public Country(String name, String code, String currency, String flag) {
            this.name = name;
            this.code = code;
            this.currency = currency;
            this.flag = flag;
        }

        @NonNull
        @Override
        public String toString() {
            return flag + " " + name + " (" + code + ")";
        }
    }

    private static final String PREF_NAME = "CountryPrefs";
    private static final String KEY_COUNTRY_CODE = "selected_country_code";

    public static List<Country> getCountries() {
        List<Country> list = new ArrayList<>();
        // Standard mapping of Country -> Currency
        list.add(new Country("Lebanon", "LB", "LBP", "🇱🇧"));
        list.add(new Country("United States", "US", "USD", "🇺🇸"));
        list.add(new Country("United Kingdom", "GB", "GBP", "🇬🇧"));
        list.add(new Country("European Union", "EU", "EUR", "🇪🇺"));
        list.add(new Country("Afghanistan", "AF", "AFN", "🇦🇫"));
        list.add(new Country("Albania", "AL", "ALL", "🇦🇱"));
        list.add(new Country("Algeria", "DZ", "DZD", "🇩🇿"));
        list.add(new Country("Angola", "AO", "AOA", "🇦🇴"));
        list.add(new Country("Antarctica", "AQ", "AQD", "🇦🇶"));
        list.add(new Country("Argentina", "AR", "ARS", "🇦🇷"));
        list.add(new Country("Armenia", "AM", "AMD", "🇦🇲"));
        list.add(new Country("Aruba", "AW", "AWG", "🇦🇼"));
        list.add(new Country("Australia", "AU", "AUD", "🇦🇺"));
        list.add(new Country("Austria", "AT", "EUR", "🇦🇹"));
        list.add(new Country("Azerbaijan", "AZ", "AZN", "🇦🇿"));
        list.add(new Country("Bahamas", "BS", "BSD", "🇧🇸"));
        list.add(new Country("Bahrain", "BH", "BHD", "🇧🇭"));
        list.add(new Country("Bangladesh", "BD", "BDT", "🇧🇩"));
        list.add(new Country("Barbados", "BB", "BBD", "🇧🇧"));
        list.add(new Country("Belarus", "BY", "BYN", "🇧🇾"));
        list.add(new Country("Belgium", "BE", "EUR", "🇧🇪"));
        list.add(new Country("Brazil", "BR", "BRL", "🇧🇷"));
        list.add(new Country("Canada", "CA", "CAD", "🇨🇦"));
        list.add(new Country("China", "CN", "CNY", "🇨🇳"));
        list.add(new Country("Egypt", "EG", "EGP", "🇪🇬"));
        list.add(new Country("France", "FR", "EUR", "🇫🇷"));
        list.add(new Country("Germany", "DE", "EUR", "🇩🇪"));
        list.add(new Country("India", "IN", "INR", "🇮🇳"));
        list.add(new Country("Iraq", "IQ", "IQD", "🇮🇶"));
        list.add(new Country("Italy", "IT", "EUR", "🇮🇹"));
        list.add(new Country("Japan", "JP", "JPY", "🇯🇵"));
        list.add(new Country("Jordan", "JO", "JOD", "🇯🇴"));
        list.add(new Country("Kuwait", "KW", "KWD", "🇰🇼"));
        list.add(new Country("Qatar", "QA", "QAR", "🇶🇦"));
        list.add(new Country("Russia", "RU", "RUB", "🇷🇺"));
        list.add(new Country("Saudi Arabia", "SA", "SAR", "🇸🇦"));
        list.add(new Country("Spain", "ES", "EUR", "🇪🇸"));
        list.add(new Country("Switzerland", "CH", "CHF", "🇨🇭"));
        list.add(new Country("Syria", "SY", "SYP", "🇸🇾"));
        list.add(new Country("Turkey", "TR", "TRY", "🇹🇷"));
        list.add(new Country("United Arab Emirates", "AE", "AED", "🇦🇪"));

        list.sort((c1, d2) -> c1.name.compareToIgnoreCase(d2.name));
        return list;
    }

    public static void saveSelectedCountry(Context context, Country country) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_COUNTRY_CODE, country.code)
                .apply();
    }

    public static Country getSelectedCountry(Context context) {
        String code = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_COUNTRY_CODE, "LB"); // Default Lebanon
        for (Country c : getCountries()) {
            if (c.code.equals(code)) return c;
        }
        return new Country("Lebanon", "LB", "LBP", "🇱🇧");
    }

    public static String getSelectedCurrencySymbol(Context context) {
        return getSelectedCountry(context).currency;
    }
}
