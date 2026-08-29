package com.example.mycalendar2026sar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CashCalculatorActivity extends AppCompatActivity {

    private TextView headerTotalDisplay, footerTotalCount, footerGrandTotal;
    private final Map<Integer, Integer> rowData = new HashMap<>();
    private final int[] denominations = {100000, 50000, 20000, 10000, 5000, 1000, 500, 250};
    private final Map<Integer, View> rowViews = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_calculator);

        headerTotalDisplay = findViewById(R.id.headerTotalDisplay);
        footerTotalCount = findViewById(R.id.footerTotalCount);
        footerGrandTotal = findViewById(R.id.footerGrandTotal);
        findViewById(R.id.cashCalcBackButton).setOnClickListener(v -> finish());

        setupRows();

        findViewById(R.id.btnCashCalcDelete).setOnClickListener(v -> resetAll());
        findViewById(R.id.btnCashCalcShare).setOnClickListener(v -> shareBreakdown());
    }

    private void setupRows() {
        rowViews.put(100000, findViewById(R.id.row_100000));
        rowViews.put(50000, findViewById(R.id.row_50000));
        rowViews.put(20000, findViewById(R.id.row_20000));
        rowViews.put(10000, findViewById(R.id.row_10000));
        rowViews.put(5000, findViewById(R.id.row_5000));
        rowViews.put(1000, findViewById(R.id.row_1000));
        rowViews.put(500, findViewById(R.id.row_500));
        rowViews.put(250, findViewById(R.id.row_250));

        for (int denom : denominations) {
            View row = rowViews.get(denom);
            if (row == null) continue;

            TextView txtDenom = row.findViewById(R.id.txtDenomination);
            txtDenom.setText(String.format(Locale.US, "%,d", denom));

            EditText editCount = row.findViewById(R.id.editCount);
            TextView txtRowTotal = row.findViewById(R.id.txtRowTotal);
            Button btnPlus = row.findViewById(R.id.btnPlus);
            Button btnMinus = row.findViewById(R.id.btnMinus);

            editCount.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    int val = 0;
                    try {
                        if (s.length() > 0) val = Integer.parseInt(s.toString());
                    } catch (NumberFormatException ignored) {}
                    rowData.put(denom, val);
                    txtRowTotal.setText(String.format(Locale.US, "%,d", (long) val * denom));
                    calculateGrandTotal();
                }
            });

            btnPlus.setOnClickListener(v -> {
                Integer current = rowData.get(denom);
                int qty = (current != null) ? current : 0;
                editCount.setText(String.valueOf(qty + 1));
            });

            btnMinus.setOnClickListener(v -> {
                Integer current = rowData.get(denom);
                int qty = (current != null) ? current : 0;
                if (qty > 0) editCount.setText(String.valueOf(qty - 1));
            });
        }
    }

    private void calculateGrandTotal() {
        long totalAmount = 0;
        int totalQty = 0;
        for (int denom : denominations) {
            Integer q = rowData.get(denom);
            int qty = (q != null) ? q : 0;
            totalAmount += (long) qty * denom;
            totalQty += qty;
        }
        headerTotalDisplay.setText(String.format(Locale.US, "%,d", totalAmount));
        footerTotalCount.setText(String.valueOf(totalQty));
        footerGrandTotal.setText(String.format(Locale.US, "%,d", totalAmount));
    }

    private void resetAll() {
        for (int denom : denominations) {
            View row = rowViews.get(denom);
            if (row != null) {
                EditText editCount = row.findViewById(R.id.editCount);
                editCount.setText("");
            }
            rowData.put(denom, 0);
        }
        calculateGrandTotal();
        Toast.makeText(this, "Reset complete", Toast.LENGTH_SHORT).show();
    }

    private void shareBreakdown() {
        StringBuilder sb = new StringBuilder("Cash Calculator Breakdown:\n\n");
        long total = 0;
        for (int denom : denominations) {
            int qty = rowData.getOrDefault(denom, 0);
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
