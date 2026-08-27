package com.example.mycalendar2026sar;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Account Summary screen: Table-style report for a selected account.
 * Matches Image 1 from user request. Features account selection,
 * periodic filtering (All, Weekly, Monthly, Yearly), and grand totals.
 */
public class AccountSummaryActivity extends AppCompatActivity {

    private static final int FILTER_ALL = 0;
    private static final int FILTER_WEEKLY = 1;
    private static final int FILTER_MONTHLY = 2;
    private static final int FILTER_YEARLY = 3;

    private int currentFilter = FILTER_ALL;
    private final Calendar currentBaseDate = Calendar.getInstance();
    private String selectedAccount = "Account Summary";

    private RecyclerView recyclerView;
    private TextView emptyText, filterLabel, accountTitle;
    private TextView footerCashIn, footerCashOut, footerBalance;
    private Button btnAll, btnWeekly, btnMonthly, btnYearly;

    private SummaryAdapter adapter;
    private final List<SummaryRow> summaryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_summary);

        recyclerView = findViewById(R.id.accountSummaryRecyclerView);
        emptyText = findViewById(R.id.accountSummaryEmptyText);
        filterLabel = findViewById(R.id.summaryFilterLabel);
        accountTitle = findViewById(R.id.summaryAccountTitle);
        
        footerCashIn = findViewById(R.id.footerCashIn);
        footerCashOut = findViewById(R.id.footerCashOut);
        footerBalance = findViewById(R.id.footerBalance);

        btnAll = findViewById(R.id.tabAll);
        btnWeekly = findViewById(R.id.tabWeekly);
        btnMonthly = findViewById(R.id.tabMonthly);
        btnYearly = findViewById(R.id.tabYearly);

        findViewById(R.id.accountSummaryBackButton).setOnClickListener(v -> finish());
        findViewById(R.id.btnSummaryCalendar).setOnClickListener(v -> showDatePicker());
        findViewById(R.id.summaryAccountPicker).setOnClickListener(this::showAccountPicker);

        btnAll.setOnClickListener(v -> { currentFilter = FILTER_ALL; refresh(); });
        btnWeekly.setOnClickListener(v -> { currentFilter = FILTER_WEEKLY; refresh(); });
        btnMonthly.setOnClickListener(v -> { currentFilter = FILTER_MONTHLY; refresh(); });
        btnYearly.setOnClickListener(v -> { currentFilter = FILTER_YEARLY; refresh(); });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SummaryAdapter(summaryList);
        recyclerView.setAdapter(adapter);

        refresh();
    }

    private void showAccountPicker(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenu().add("Account Summary");
        List<Account> accounts = BalanceManager.loadAccounts(this);
        for (Account a : accounts) popup.getMenu().add(a.getName());
        
        popup.setOnMenuItemClickListener(item -> {
            selectedAccount = item.getTitle().toString();
            accountTitle.setText(selectedAccount);
            refresh();
            return true;
        });
        popup.show();
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, day) -> {
            currentBaseDate.set(year, month, day);
            if (currentFilter == FILTER_ALL) currentFilter = FILTER_MONTHLY;
            refresh();
        }, currentBaseDate.get(Calendar.YEAR), currentBaseDate.get(Calendar.MONTH), currentBaseDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void refresh() {
        updateTabsUI();
        updateRangeLabel();
        loadData();
    }

    private void updateTabsUI() {
        Button[] btns = {btnAll, btnWeekly, btnMonthly, btnYearly};
        int[] filters = {FILTER_ALL, FILTER_WEEKLY, FILTER_MONTHLY, FILTER_YEARLY};
        for (int i = 0; i < btns.length; i++) {
            if (currentFilter == filters[i]) {
                btns[i].setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                btns[i].setTextColor(Color.BLACK);
            } else {
                btns[i].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#333333")));
                btns[i].setTextColor(Color.WHITE);
            }
        }
    }

    private void updateRangeLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        Calendar start = (Calendar) currentBaseDate.clone();
        Calendar end = (Calendar) currentBaseDate.clone();

        if (currentFilter == FILTER_ALL) {
            filterLabel.setText("All");
            return;
        }

        switch (currentFilter) {
            case FILTER_WEEKLY:
                start.set(Calendar.DAY_OF_WEEK, start.getFirstDayOfWeek());
                end.setTimeInMillis(start.getTimeInMillis());
                end.add(Calendar.DAY_OF_YEAR, 6);
                filterLabel.setText(sdf.format(start.getTime()) + " -> " + sdf.format(end.getTime()));
                break;
            case FILTER_MONTHLY:
                start.set(Calendar.DAY_OF_MONTH, 1);
                end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
                filterLabel.setText(sdf.format(start.getTime()) + " -> " + sdf.format(end.getTime()));
                break;
            case FILTER_YEARLY:
                start.set(Calendar.DAY_OF_YEAR, 1);
                end.set(Calendar.DAY_OF_YEAR, end.getActualMaximum(Calendar.DAY_OF_YEAR));
                filterLabel.setText(sdf.format(start.getTime()) + " -> " + sdf.format(end.getTime()));
                break;
        }
    }

    private void loadData() {
        summaryList.clear();
        List<Transaction> all = TransactionDbHelper.getInstance(this).getAllTransactionsAscending();
        
        long startTs = 0, endTs = Long.MAX_VALUE;
        if (currentFilter != FILTER_ALL) {
            Calendar c = (Calendar) currentBaseDate.clone();
            c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0); c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0);
            if (currentFilter == FILTER_WEEKLY) {
                c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
                startTs = c.getTimeInMillis();
                c.add(Calendar.DAY_OF_YEAR, 7);
                endTs = c.getTimeInMillis() - 1;
            } else if (currentFilter == FILTER_MONTHLY) {
                c.set(Calendar.DAY_OF_MONTH, 1);
                startTs = c.getTimeInMillis();
                c.add(Calendar.MONTH, 1);
                endTs = c.getTimeInMillis() - 1;
            } else if (currentFilter == FILTER_YEARLY) {
                c.set(Calendar.DAY_OF_YEAR, 1);
                startTs = c.getTimeInMillis();
                c.add(Calendar.YEAR, 1);
                endTs = c.getTimeInMillis() - 1;
            }
        }

        // Grouping logic
        SimpleDateFormat rowSdf;
        switch (currentFilter) {
            case FILTER_YEARLY: rowSdf = new SimpleDateFormat("MMM yyyy", Locale.getDefault()); break;
            case FILTER_ALL: rowSdf = new SimpleDateFormat("yyyy", Locale.getDefault()); break;
            default: rowSdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()); break;
        }

        Map<String, SummaryRow> map = new TreeMap<>(Collections.reverseOrder());
        double totalIn = 0, totalOut = 0;

        for (Transaction t : all) {
            if ("Monthly Income".equalsIgnoreCase(t.getTitle())) continue;
            if (currentFilter != FILTER_ALL && (t.getTimestamp() < startTs || t.getTimestamp() > endTs)) continue;
            if (!selectedAccount.equals("Account Summary") && !selectedAccount.equalsIgnoreCase(t.getAccount())) continue;

            String key = rowSdf.format(new Date(t.getTimestamp()));
            SummaryRow s = map.get(key);
            if (s == null) {
                s = new SummaryRow(key);
                map.put(key, s);
            }
            if (t.isCashIn()) {
                s.in += t.getAmount();
                totalIn += t.getAmount();
            } else {
                s.out += t.getAmount();
                totalOut += t.getAmount();
            }
        }

        summaryList.addAll(map.values());
        adapter.notifyDataSetChanged();

        footerCashIn.setText(String.format(Locale.US, "%,.2f", totalIn));
        footerCashOut.setText(String.format(Locale.US, "%,.2f", totalOut));
        double balance = totalIn - totalOut;
        footerBalance.setText(String.format(Locale.US, "%,.2f", balance));
        footerBalance.setTextColor(balance >= 0 ? ContextCompat.getColor(this, R.color.light_green) : ContextCompat.getColor(this, R.color.expense_red));

        emptyText.setVisibility(summaryList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private static class SummaryRow {
        String date;
        double in = 0, out = 0;
        SummaryRow(String d) { this.date = d; }
    }

    private static class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.VH> {
        private final List<SummaryRow> list;
        SummaryAdapter(List<SummaryRow> l) { this.list = l; }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_summary_table_row, p, false));
        }

        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            SummaryRow s = list.get(pos);
            h.date.setText(s.date);
            h.in.setText(String.format(Locale.US, "%,.2f", s.in));
            h.out.setText(String.format(Locale.US, "%,.2f", s.out));
            double savings = s.in - s.out;
            h.savings.setText(String.format(Locale.US, "%,.2f", savings));
            h.savings.setTextColor(savings >= 0 ? ContextCompat.getColor(h.itemView.getContext(), R.color.light_green) : ContextCompat.getColor(h.itemView.getContext(), R.color.expense_red));
        }

        @Override public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView date, in, out, savings;
            VH(View v) {
                super(v);
                date = v.findViewById(R.id.rowDate);
                in = v.findViewById(R.id.rowCashIn);
                out = v.findViewById(R.id.rowCashOut);
                savings = v.findViewById(R.id.rowSavings);
            }
        }
    }
}
