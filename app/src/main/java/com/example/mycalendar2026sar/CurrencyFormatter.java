package com.example.mycalendar2026sar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CurrencyFormatter {

    public static String formatLbpAmount(double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator('.');
        if (amount == (long) amount) {
            DecimalFormat formatter = new DecimalFormat("#,##0", symbols);
            return formatter.format((long) amount);
        } else {
            DecimalFormat formatter = new DecimalFormat("#,##0.##", symbols);
            return formatter.format(amount);
        }
    }

    public static String formatAmount(double amount, String currency) {
        if ("LBP".equalsIgnoreCase(currency)) {
            return formatLbpAmount(amount) + " LBP";
        } else {
            return String.format(Locale.US, "%,.2f %s", amount, currency);
        }
    }
}
