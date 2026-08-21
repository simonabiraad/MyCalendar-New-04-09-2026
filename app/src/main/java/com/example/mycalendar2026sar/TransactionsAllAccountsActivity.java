package com.example.mycalendar2026sar;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TransactionsAllAccountsActivity extends AppCompatActivity {

    private static final int TAB_ALL = 0;
    private static final int TAB_DAILY = 1;
    private static final int TAB_WEEKLY = 2;
    private static final int TAB_MONTHLY = 3;
    private static final int TAB_YEARLY = 4;

    private int currentTab = TAB_ALL;
    private TransactionDbHelper dbHelper;
    private AllTxAdapter adapter;
    private RecyclerView recyclerView;
    private TextView labelCurrentTab, footerCashIn, footerCashOut, footerBalance;
    private Button btnAll, btnDaily, btnWeekly, btnMonthly, btnYearly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transactions_all_accounts);

        dbHelper = TransactionDbHelper.getInstance(this);

        btnAll = findViewById(R.id.tabAll);
        btnDaily = findViewById(R.id.tabDaily);
        btnWeekly = findViewById(R.id.tabWeekly);
        btnMonthly = findViewById(R.id.tabMonthly);
        btnYearly = findViewById(R.id.tabYearly);
        labelCurrentTab = findViewById(R.id.currentTabLabel);
        footerCashIn = findViewById(R.id.footerCashIn);
        footerCashOut = findViewById(R.id.footerCashOut);
        footerBalance = findViewById(R.id.footerBalance);
        recyclerView = findViewById(R.id.allTxRecyclerView);

        findViewById(R.id.allTxBackButton).setOnClickListener(v -> finish());

        btnAll.setOnClickListener(v -> { currentTab = TAB_ALL; refresh(); });
        btnDaily.setOnClickListener(v -> { currentTab = TAB_DAILY; refresh(); });
        btnWeekly.setOnClickListener(v -> { currentTab = TAB_WEEKLY; refresh(); });
        btnMonthly.setOnClickListener(v -> { currentTab = TAB_MONTHLY; refresh(); });
        btnYearly.setOnClickListener(v -> { currentTab = TAB_YEARLY; refresh(); });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AllTxAdapter();
        recyclerView.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.allTxToolbar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        refresh();
    }

    private void refresh() {
        updateTabButtonsUI();
        
        List<Transaction> all = dbHelper.getAllTransactionsAscending();
        List<Transaction> filtered = new ArrayList<>();
        
        double cashIn = 0, cashOut = 0;
        
        Calendar now = Calendar.getInstance();
        
        for (int i = all.size() - 1; i >= 0; i--) {
            Transaction t = all.get(i);
            if ("Monthly Income".equalsIgnoreCase(t.getTitle())) continue;

            if (matchesTabFilter(t, now)) {
                filtered.add(t);
                if (t.isCashIn()) {
                    cashIn += t.getAmount();
                } else {
                    cashOut += t.getAmount();
                }
            }
        }

        footerCashIn.setText(String.format(Locale.getDefault(), "%.2f", cashIn));
        footerCashOut.setText(String.format(Locale.getDefault(), "%.2f", cashOut));
        footerBalance.setText(String.format(Locale.getDefault(), "%.2f", cashIn - cashOut));

        // Group by Date for display
        List<AllTxItem> displayItems = new ArrayList<>();
        String lastDate = "";
        for (Transaction t : filtered) {
            String date = DateFormat.format("EEE, dd MMM yyyy", t.getTimestamp()).toString();
            if (!date.equals(lastDate)) {
                displayItems.add(new AllTxItem(date));
                lastDate = date;
            }
            displayItems.add(new AllTxItem(t));
        }

        adapter.setItems(displayItems);
    }

    private boolean matchesTabFilter(Transaction t, Calendar now) {
        if (currentTab == TAB_ALL) return true;

        Calendar txCal = Calendar.getInstance();
        txCal.setTimeInMillis(t.getTimestamp());

        switch (currentTab) {
            case TAB_DAILY:
                return now.get(Calendar.YEAR) == txCal.get(Calendar.YEAR) &&
                       now.get(Calendar.DAY_OF_YEAR) == txCal.get(Calendar.DAY_OF_YEAR);
            case TAB_WEEKLY:
                return now.get(Calendar.YEAR) == txCal.get(Calendar.YEAR) &&
                       now.get(Calendar.WEEK_OF_YEAR) == txCal.get(Calendar.WEEK_OF_YEAR);
            case TAB_MONTHLY:
                return now.get(Calendar.YEAR) == txCal.get(Calendar.YEAR) &&
                       now.get(Calendar.MONTH) == txCal.get(Calendar.MONTH);
            case TAB_YEARLY:
                return now.get(Calendar.YEAR) == txCal.get(Calendar.YEAR);
            default:
                return true;
        }
    }

    private void updateTabButtonsUI() {
        Button[] btns = {btnAll, btnDaily, btnWeekly, btnMonthly, btnYearly};
        String[] labels = {"All", "Daily", "Weekly", "Monthly", "Yearly"};
        
        for (int i = 0; i < btns.length; i++) {
            if (i == currentTab) {
                btns[i].setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                btns[i].setTextColor(Color.BLACK);
                labelCurrentTab.setText(labels[i]);
            } else {
                btns[i].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#333333")));
                btns[i].setTextColor(Color.WHITE);
            }
        }
    }

    private static class AllTxItem {
        static final int TYPE_HEADER = 0;
        static final int TYPE_ROW = 1;

        final int type;
        final String headerDate;
        final Transaction transaction;

        AllTxItem(String headerDate) {
            this.type = TYPE_HEADER;
            this.headerDate = headerDate;
            this.transaction = null;
        }

        AllTxItem(Transaction transaction) {
            this.type = TYPE_ROW;
            this.headerDate = null;
            this.transaction = transaction;
        }
    }

    private class AllTxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final List<AllTxItem> items = new ArrayList<>();

        void setItems(List<AllTxItem> newItems) {
            items.clear();
            items.addAll(newItems);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return items.get(position).type;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == AllTxItem.TYPE_HEADER) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_all_accounts_date_header, parent, false);
                return new HeaderViewHolder(v);
            } else {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_all_accounts_row, parent, false);
                return new RowViewHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            AllTxItem item = items.get(position);
            if (holder instanceof HeaderViewHolder) {
                ((HeaderViewHolder) holder).text.setText(item.headerDate);
            } else {
                RowViewHolder row = (RowViewHolder) holder;
                Transaction t = item.transaction;
                row.title.setText(t.getTitle());
                row.time.setText(DateFormat.format("hh:mm a", t.getTimestamp()));
                row.account.setText(t.getAccount() != null ? t.getAccount() : "---");
                row.amount.setText(String.format(Locale.getDefault(), "%.2f", t.getAmount()));
                row.amount.setTextColor(ContextCompat.getColor(TransactionsAllAccountsActivity.this,
                        t.isCashIn() ? R.color.income_green : R.color.expense_red));
            }
        }

        @Override public int getItemCount() { return items.size(); }

        class HeaderViewHolder extends RecyclerView.ViewHolder {
            TextView text;
            HeaderViewHolder(View v) { super(v); text = v.findViewById(R.id.headerDateText); }
        }

        class RowViewHolder extends RecyclerView.ViewHolder {
            TextView title, time, account, amount;
            RowViewHolder(View v) {
                super(v);
                title = v.findViewById(R.id.rowTitle);
                time = v.findViewById(R.id.rowTime);
                account = v.findViewById(R.id.rowAccount);
                amount = v.findViewById(R.id.rowAmount);
            }
        }
    }
}
