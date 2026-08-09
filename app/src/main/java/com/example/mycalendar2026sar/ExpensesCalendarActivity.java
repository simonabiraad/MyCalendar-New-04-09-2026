package com.example.mycalendar2026sar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpensesCalendarActivity extends AppCompatActivity {

    private GridView calendarGrid;
    private TextView dateRangeText;
    private TextView totalCashInText, totalCashOutText, balanceText;
    private Calendar currentMonth;
    private CalendarAdapter adapter;
    private TransactionDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_expenses_calendar);

        dbHelper = TransactionDbHelper.getInstance(this);
        currentMonth = Calendar.getInstance();
        currentMonth.set(Calendar.DAY_OF_MONTH, 1);

        calendarGrid = findViewById(R.id.calendarGrid);
        dateRangeText = findViewById(R.id.dateRangeText);
        totalCashInText = findViewById(R.id.totalCashInText);
        totalCashOutText = findViewById(R.id.totalCashOutText);
        balanceText = findViewById(R.id.balanceText);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.prevMonth).setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, -1);
            updateUI();
        });
        findViewById(R.id.nextMonth).setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, 1);
            updateUI();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.expenses_calendar_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        updateUI();
    }

    private void updateUI() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        Calendar rangeEnd = (Calendar) currentMonth.clone();
        rangeEnd.set(Calendar.DAY_OF_MONTH, currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
        
        String range = sdf.format(currentMonth.getTime()) + " -> " + sdf.format(rangeEnd.getTime());
        dateRangeText.setText(range);

        updateCalendarGrid();
        updateTotals();
    }

    private void updateCalendarGrid() {
        ArrayList<Date> days = new ArrayList<>();
        Calendar tempCal = (Calendar) currentMonth.clone();
        
        // Start from Monday (ISO style as in image)
        tempCal.set(Calendar.DAY_OF_MONTH, 1);
        int dayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK); // Sun=1, Mon=2...
        int offset = dayOfWeek - Calendar.MONDAY;
        if (offset < 0) offset += 7;
        
        tempCal.add(Calendar.DAY_OF_MONTH, -offset);

        // Grid always shows 42 cells (6 weeks)
        while (days.size() < 42) {
            days.add(tempCal.getTime());
            tempCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        adapter = new CalendarAdapter(days, currentMonth.get(Calendar.MONTH));
        calendarGrid.setAdapter(adapter);
    }

    private void updateTotals() {
        Calendar start = (Calendar) currentMonth.clone();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        
        Calendar end = (Calendar) start.clone();
        end.set(Calendar.DAY_OF_MONTH, currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);

        double cashIn = 0, cashOut = 0;
        List<Transaction> all = dbHelper.getAllTransactionsAscending();
        for (Transaction t : all) {
            if (t.getTimestamp() >= start.getTimeInMillis() && t.getTimestamp() <= end.getTimeInMillis()) {
                if (t.getType().equals(Transaction.TYPE_CASH_IN)) {
                    cashIn += t.getAmount();
                } else if (t.getType().equals(Transaction.TYPE_CASH_OUT)) {
                    cashOut += t.getAmount();
                }
            }
        }

        totalCashInText.setText(String.format(Locale.getDefault(), "%.0f", cashIn));
        totalCashOutText.setText(String.format(Locale.getDefault(), "%.0f", cashOut));
        balanceText.setText(String.format(Locale.getDefault(), "%.0f", cashIn - cashOut));
    }

    private class CalendarAdapter extends BaseAdapter {
        private final List<Date> days;
        private final int month;

        CalendarAdapter(List<Date> days, int month) {
            this.days = days;
            this.month = month;
        }

        @Override public int getCount() { return days.size(); }
        @Override public Object getItem(int position) { return days.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ExpensesCalendarActivity.this).inflate(R.layout.item_expenses_calendar_day, parent, false);
            }
            
            Date date = days.get(position);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            TextView dayNumber = convertView.findViewById(R.id.dayNumber);
            dayNumber.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));

            if (cal.get(Calendar.MONTH) != month) {
                dayNumber.setTextColor(0xFF666666);
                convertView.setBackgroundColor(0xFF000000);
            } else {
                dayNumber.setTextColor(0xFFFFFFFF);
                convertView.setBackgroundColor(0xFF1A1A1A);
            }

            return convertView;
        }
    }
}
