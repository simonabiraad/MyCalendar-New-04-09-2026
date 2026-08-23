package com.example.mycalendar2026sar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Read-only totals screen: cash in / cash out / net for this month, this
 * year, and all time, computed from the shared transaction database.
 */
public class SummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        findViewById(R.id.summaryBackButton).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<Transaction> all = TransactionDbHelper.getInstance(this).getAllTransactionsAscending();

        Calendar now = Calendar.getInstance();
        Calendar monthStart = (Calendar) now.clone();
        monthStart.set(Calendar.DAY_OF_MONTH, 1);
        zeroTime(monthStart);

        Calendar yearStart = (Calendar) now.clone();
        yearStart.set(Calendar.DAY_OF_YEAR, 1);
        zeroTime(yearStart);

        double monthIn = 0, monthOut = 0;
        double yearIn = 0, yearOut = 0;
        double allIn = 0, allOut = 0;

        for (Transaction t : all) {
            boolean inMonth = t.getTimestamp() >= monthStart.getTimeInMillis();
            boolean inYear = t.getTimestamp() >= yearStart.getTimeInMillis();

            if (t.isCashIn()) {
                allIn += t.getAmount();
                if (inYear) yearIn += t.getAmount();
                if (inMonth) monthIn += t.getAmount();
            } else {
                allOut += t.getAmount();
                if (inYear) yearOut += t.getAmount();
                if (inMonth) monthOut += t.getAmount();
            }
        }

        bindRow(R.id.monthRow, monthIn, monthOut);
        bindRow(R.id.yearRow, yearIn, yearOut);
        bindRow(R.id.allTimeRow, allIn, allOut);
    }

    private void bindRow(int rowId, double in, double out) {
        View row = findViewById(rowId);
        TextView inText = row.findViewById(R.id.statInText);
        TextView outText = row.findViewById(R.id.statOutText);
        TextView netText = row.findViewById(R.id.statNetText);

        inText.setText(String.format(Locale.US, "%,.2f", in));
        outText.setText(String.format(Locale.US, "%,.2f", out));
        netText.setText(String.format(Locale.US, "%,.2f", in - out));
    }

    private void zeroTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
}
