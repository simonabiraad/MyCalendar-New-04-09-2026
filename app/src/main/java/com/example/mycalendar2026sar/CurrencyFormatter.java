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

    public static String formatLiveText(String input, String currency) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        boolean isLbp = "LBP".equalsIgnoreCase(currency);

        if (isLbp) {
            String digitsOnly = input.replaceAll("[^0-9]", "");
            if (digitsOnly.isEmpty()) {
                return "";
            }
            try {
                long val = Long.parseLong(digitsOnly);
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                symbols.setGroupingSeparator('.');
                DecimalFormat formatter = new DecimalFormat("#,##0", symbols);
                return formatter.format(val);
            } catch (NumberFormatException e) {
                return formatHugeDigits(digitsOnly, '.');
            }
        } else {
            String clean = input.replaceAll("[^0-9.]", "");
            if (clean.isEmpty()) {
                return "";
            }

            int firstDot = clean.indexOf('.');
            String intPart;
            String decPart = "";

            if (firstDot != -1) {
                intPart = clean.substring(0, firstDot);
                String rest = clean.substring(firstDot + 1).replace(".", "");
                if (rest.length() > 2) {
                    rest = rest.substring(0, 2);
                }
                decPart = "." + rest;
            } else {
                intPart = clean;
            }

            if (intPart.isEmpty()) {
                return "0" + decPart;
            }

            String formattedInt;
            try {
                long val = Long.parseLong(intPart);
                DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                symbols.setGroupingSeparator(',');
                DecimalFormat formatter = new DecimalFormat("#,##0", symbols);
                formattedInt = formatter.format(val);
            } catch (NumberFormatException e) {
                formattedInt = formatHugeDigits(intPart, ',');
            }

            return formattedInt + decPart;
        }
    }

    private static String formatHugeDigits(String digits, char separator) {
        StringBuilder sb = new StringBuilder();
        int len = digits.length();
        for (int i = 0; i < len; i++) {
            if (i > 0 && (len - i) % 3 == 0) {
                sb.append(separator);
            }
            sb.append(digits.charAt(i));
        }
        return sb.toString();
    }
}
