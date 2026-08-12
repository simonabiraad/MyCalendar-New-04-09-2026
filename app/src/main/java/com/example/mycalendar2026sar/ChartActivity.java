package com.example.mycalendar2026sar;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChartActivity extends AppCompatActivity {

    private PieChart pieChart, pieChartAccount;
    private LineChart lineChart;
    private LinearLayout detailsContainer, detailsContainerAccount;
    private TransactionDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        dbHelper = TransactionDbHelper.getInstance(this);
        pieChart = findViewById(R.id.pieChart);
        pieChartAccount = findViewById(R.id.pieChartAccount);
        lineChart = findViewById(R.id.lineChart);
        detailsContainer = findViewById(R.id.detailsContainer);
        detailsContainerAccount = findViewById(R.id.detailsContainerAccount);
        ImageButton backButton = findViewById(R.id.chartBackButton);

        backButton.setOnClickListener(v -> finish());

        setupCharts();
    }

    private void setupCharts() {
        List<Transaction> transactions = dbHelper.getAllTransactionsAscending();
        setupPieChartTotalAccounts();
        setupPieChart(transactions);
        setupLineChart(transactions);
    }

    private void setupPieChartTotalAccounts() {
        List<Account> accounts = loadAccounts();
        Map<String, Double> accountBalances = new HashMap<>();
        double totalBalance = 0;

        for (Account a : accounts) {
            double balance = a.getBalance();
            accountBalances.put(a.getName(), balance);
            totalBalance += balance;
        }

        if (accountBalances.isEmpty()) {
            pieChartAccount.setNoDataText("No account data available");
            return;
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : accountBalances.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        int[] colors = {
                Color.parseColor("#4285F4"), // Blue
                Color.parseColor("#34A853"), // Green
                Color.parseColor("#FBBC05"), // Orange
                Color.parseColor("#EA4335"), // Red
                Color.parseColor("#8E24AA"), // Purple
                Color.parseColor("#00ACC1"), // Teal
                Color.parseColor("#795548")  // Brown
        };
        dataSet.setColors(colors);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTypeface(Typeface.DEFAULT_BOLD);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChartAccount));
        pieChartAccount.setData(data);
        pieChartAccount.setUsePercentValues(true);
        pieChartAccount.getDescription().setEnabled(false);
        pieChartAccount.setDrawHoleEnabled(true);
        pieChartAccount.setHoleRadius(65f);
        pieChartAccount.setTransparentCircleRadius(70f);
        pieChartAccount.setHoleColor(Color.BLACK);
        pieChartAccount.setDrawEntryLabels(false);
        pieChartAccount.getLegend().setEnabled(false);

        pieChartAccount.setCenterText(generateCenterText("Total accounts", totalBalance));

        pieChartAccount.animateY(1200);
        pieChartAccount.invalidate();

        populateDetails(detailsContainerAccount, accountBalances, totalBalance, colors, true);
    }

    private List<Account> loadAccounts() {
        List<Account> list = new ArrayList<>();
        try {
            String json = getSharedPreferences("ExpensesPrefs", MODE_PRIVATE)
                    .getString("AccountList", null);
            if (json != null) {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    list.add(new Account(obj.getString("name"), obj.getDouble("balance")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void setupPieChart(List<Transaction> transactions) {
        Map<String, Double> categoryTotals = new HashMap<>();
        double totalSpent = 0;
        
        for (Transaction t : transactions) {
            if (!t.isCashIn()) {
                String category = t.getTitle();
                double amount = t.getAmount();
                categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
                totalSpent += amount;
            }
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        int[] colors = {
            Color.parseColor("#4285F4"), // Blue
            Color.parseColor("#34A853"), // Green
            Color.parseColor("#FBBC05"), // Orange
            Color.parseColor("#EA4335"), // Red
            Color.parseColor("#8E24AA"), // Purple
            Color.parseColor("#00ACC1"), // Teal
            Color.parseColor("#795548")  // Brown
        };
        dataSet.setColors(colors);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTypeface(Typeface.DEFAULT_BOLD);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(65f);
        pieChart.setTransparentCircleRadius(70f);
        pieChart.setHoleColor(Color.BLACK);
        pieChart.setDrawEntryLabels(false);
        pieChart.getLegend().setEnabled(false);
        
        // Center text: Total spent
        pieChart.setCenterText(generateCenterText("Total spent", totalSpent));
        
        pieChart.animateY(1200);
        pieChart.invalidate();

        populateDetails(detailsContainer, categoryTotals, totalSpent, colors, false);
    }

    private SpannableString generateCenterText(String label, double total) {
        String top = label + "\n";
        String bottom = String.format(Locale.getDefault(), "%.2f $", total);
        SpannableString s = new SpannableString(top + bottom);
        s.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0, top.length(), 0);
        s.setSpan(new RelativeSizeSpan(1.6f), 0, top.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, top.length(), 0);
        
        s.setSpan(new ForegroundColorSpan(Color.WHITE), top.length(), s.length(), 0);
        s.setSpan(new RelativeSizeSpan(3.8f), top.length(), s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), top.length(), s.length(), 0);
        return s;
    }

    private void populateDetails(LinearLayout container, Map<String, Double> totals, double grandTotal, int[] palette, boolean isAccount) {
        container.removeAllViews();
        List<Map.Entry<String, Double>> list = new ArrayList<>(totals.entrySet());
        // Sort by amount descending
        Collections.sort(list, (a, b) -> b.getValue().compareTo(a.getValue()));

        int colorIndex = 0;
        for (Map.Entry<String, Double> entry : list) {
            View row = LayoutInflater.from(this).inflate(R.layout.item_chart_detail, container, false);
            
            ImageView icon = row.findViewById(R.id.catIcon);
            TextView name = row.findViewById(R.id.catName);
            TextView percent = row.findViewById(R.id.catPercent);
            TextView amount = row.findViewById(R.id.catAmount);
            ProgressBar progress = row.findViewById(R.id.catProgress);

            int color = palette[colorIndex % palette.length];
            colorIndex++;

            name.setText(entry.getKey());
            double val = entry.getValue();
            int p = (int) Math.round((val / grandTotal) * 100);
            
            percent.setText(p + "%");
            amount.setText(String.format(Locale.getDefault(), "%.2f $", val));
            
            if (isAccount) {
                icon.setImageResource(R.drawable.ic_menu_accounts_color);
            } else {
                icon.setImageResource(getIconForCategory(entry.getKey()));
            }
            icon.setImageTintList(ColorStateList.valueOf(color));
            
            progress.setProgressTintList(ColorStateList.valueOf(color));
            progress.setProgress(p);

            container.addView(row);
        }
    }

    private int getIconForCategory(String category) {
        String low = category.toLowerCase();
        if (low.contains("food") || low.contains("restaurant")) return R.drawable.ic_cat_food_color;
        if (low.contains("rent")) return R.drawable.ic_cat_rent_color;
        if (low.contains("bill")) return R.drawable.ic_cat_bill_color;
        if (low.contains("fuel")) return R.drawable.ic_cat_fuel_color;
        if (low.contains("shop")) return R.drawable.ic_cat_shop_color;
        if (low.contains("health") || low.contains("medicine")) return R.drawable.ic_cat_health_color;
        if (low.contains("travel") || low.contains("flight")) return R.drawable.ic_cat_travel_color;
        if (low.contains("mobile") || low.contains("phone")) return R.drawable.ic_cat_mobile_color;
        if (low.contains("transport")) return R.drawable.ic_cat_transport_color;
        if (low.contains("others")) return R.drawable.ic_cat_other_color;
        return R.drawable.ic_report_logo;
    }

    private void setupLineChart(List<Transaction> transactions) {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        
        double runningBalance = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        
        Map<String, Double> dailyBalances = new HashMap<>();
        List<String> dateKeys = new ArrayList<>();
        
        for (Transaction t : transactions) {
            runningBalance += t.getSignedAmount();
            String dateKey = sdf.format(new Date(t.getTimestamp()));
            if (!dailyBalances.containsKey(dateKey)) {
                dateKeys.add(dateKey);
            }
            dailyBalances.put(dateKey, runningBalance);
        }

        for (int i = 0; i < dateKeys.size(); i++) {
            String key = dateKeys.get(i);
            entries.add(new Entry(i, dailyBalances.get(key).floatValue()));
            labels.add(key);
        }

        LineDataSet dataSet = new LineDataSet(entries, "Balance Trend");
        dataSet.setColor(Color.parseColor("#34A853"));
        dataSet.setCircleColor(Color.parseColor("#34A853"));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(40);
        dataSet.setFillColor(Color.parseColor("#34A853"));

        LineData data = new LineData(dataSet);
        lineChart.setData(data);
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        lineChart.getXAxis().setTextColor(Color.LTGRAY);
        lineChart.getXAxis().setGranularity(1f);
        lineChart.getAxisLeft().setTextColor(Color.LTGRAY);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setTextColor(Color.LTGRAY);
        lineChart.animateX(1000);
        lineChart.invalidate();
    }
}
