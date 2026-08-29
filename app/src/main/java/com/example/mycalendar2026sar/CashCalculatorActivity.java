package com.example.mycalendar2026sar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CashCalculatorActivity extends AppCompatActivity {

    private TextView headerTotalDisplay, footerTotalCount, footerGrandTotal;
    private View onlineStatusIndicator;
    private LinearLayout dynamicRowsContainer;
    
    private final Map<Integer, Integer> rowData = new HashMap<>();
    private final Map<Integer, View> rowViews = new HashMap<>();
    private List<DenomManager.Denomination> denominations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_calculator);

        headerTotalDisplay = findViewById(R.id.headerTotalDisplay);
        footerTotalCount = findViewById(R.id.footerTotalCount);
        footerGrandTotal = findViewById(R.id.footerGrandTotal);
        onlineStatusIndicator = findViewById(R.id.onlineStatusIndicator);
        dynamicRowsContainer = findViewById(R.id.dynamicRowsContainer);

        findViewById(R.id.cashCalcBackButton).setOnClickListener(v -> finish());
        findViewById(R.id.btnCashCalcDelete).setOnClickListener(v -> resetAll());
        findViewById(R.id.btnCashCalcShare).setOnClickListener(v -> shareBreakdown());
        findViewById(R.id.cashCalcMoreButton).setOnClickListener(this::showMoreMenu);

        refreshDenominations();
        updateOnlineStatusIndicator();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDenominations();
    }

    private void refreshDenominations() {
        denominations = DenomManager.getDenominations(this);
        dynamicRowsContainer.removeAllViews();
        rowViews.clear();
        
        for (DenomManager.Denomination d : denominations) {
            if (!d.enabled) continue;
            
            View row = LayoutInflater.from(this).inflate(R.layout.item_cash_denom_row, dynamicRowsContainer, false);
            dynamicRowsContainer.addView(row);
            rowViews.put(d.value, row);

            TextView txtDenom = row.findViewById(R.id.txtDenomination);
            txtDenom.setText(String.format(Locale.US, "%,d", d.value));

            EditText editCount = row.findViewById(R.id.editCount);
            TextView txtRowTotal = row.findViewById(R.id.txtRowTotal);
            Button btnPlus = row.findViewById(R.id.btnPlus);
            Button btnMinus = row.findViewById(R.id.btnMinus);

            Integer currentQty = rowData.get(d.value);
            int existingQty = (currentQty != null) ? currentQty : 0;
            if (existingQty > 0) editCount.setText(String.valueOf(existingQty));
            txtRowTotal.setText(String.format(Locale.US, "%,d", (long) existingQty * d.value));

            editCount.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    int val = 0;
                    try {
                        if (s.length() > 0) val = Integer.parseInt(s.toString());
                    } catch (NumberFormatException ignored) {}
                    rowData.put(d.value, val);
                    txtRowTotal.setText(String.format(Locale.US, "%,d", (long) val * d.value));
                    calculateGrandTotal();
                }
            });

            btnPlus.setOnClickListener(v -> {
                Integer current = rowData.get(d.value);
                int qty = (current != null) ? current : 0;
                editCount.setText(String.valueOf(qty + 1));
            });

            btnMinus.setOnClickListener(v -> {
                Integer current = rowData.get(d.value);
                int qty = (current != null) ? current : 0;
                if (qty > 0) editCount.setText(String.valueOf(qty - 1));
            });
        }
        calculateGrandTotal();
    }

    private void calculateGrandTotal() {
        long totalAmount = 0;
        int totalQty = 0;
        for (Map.Entry<Integer, Integer> entry : rowData.entrySet()) {
            boolean found = false;
            for(DenomManager.Denomination d : denominations) {
                if (d.value == entry.getKey() && d.enabled) {
                    found = true;
                    break;
                }
            }
            if (found) {
                totalAmount += (long) entry.getValue() * entry.getKey();
                totalQty += entry.getValue();
            }
        }
        headerTotalDisplay.setText(String.format(Locale.US, "%,d", totalAmount));
        footerTotalCount.setText(String.valueOf(totalQty));
        footerGrandTotal.setText(String.format(Locale.US, "%,d", totalAmount));
    }

    private void updateOnlineStatusIndicator() {
        boolean isOnline = getSharedPreferences("SpeechSettings", MODE_PRIVATE)
                .getBoolean("is_online", false);
        onlineStatusIndicator.setVisibility(View.VISIBLE);
        onlineStatusIndicator.setBackgroundResource(isOnline ? R.drawable.status_dot_on : R.drawable.status_dot_off);
    }

    private void showMoreMenu(View v) {
        androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.menu_cash_calculator, popup.getMenu());

        boolean isOnline = getSharedPreferences("SpeechSettings", MODE_PRIVATE)
                .getBoolean("is_online", false);
        popup.getMenu().findItem(R.id.action_show_online).setChecked(isOnline);

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_show_online) {
                boolean newStatus = !item.isChecked();
                item.setChecked(newStatus);
                getSharedPreferences("SpeechSettings", MODE_PRIVATE)
                        .edit().putBoolean("is_online", newStatus).apply();
                updateOnlineStatusIndicator();
                Toast.makeText(this, newStatus ? "Online" : "Offline", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.action_add_remove_denom) {
                startActivity(new Intent(this, AddRemoveCurrencyActivity.class));
                return true;
            } else if (id == R.id.action_select_currency) {
                Toast.makeText(this, "Select Currency feature coming soon", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void resetAll() {
        rowData.clear();
        refreshDenominations();
        Toast.makeText(this, "Reset complete", Toast.LENGTH_SHORT).show();
    }

    private void shareBreakdown() {
        StringBuilder sb = new StringBuilder("Cash Calculator Breakdown:\n\n");
        long total = 0;
        List<Integer> keys = new ArrayList<>(rowViews.keySet());
        keys.sort(Collections.reverseOrder());

        for (int denom : keys) {
            Integer q = rowData.get(denom);
            int qty = (q != null) ? q : 0;
            if (qty > 0) {
                long rowTotal = (long) qty * denom;
                sb.append(String.format(Locale.US, "%,d x %d = %,d\n", denom, qty, rowTotal));
                total += rowTotal;
            }
        }
        sb.append("\nTotal: ").append(String.format(Locale.US, "%,d", total));

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        startActivity(Intent.createChooser(intent, "Share via"));
    }
}
