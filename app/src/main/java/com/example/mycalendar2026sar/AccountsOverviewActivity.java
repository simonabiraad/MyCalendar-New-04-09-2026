package com.example.mycalendar2026sar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Screen showing a list of all account cards with their current balance
 * and this month's cash-in / cash-out totals.
 * Displays separate independent Total Balance for each currency.
 */
public class AccountsOverviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout totalOverviewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_overview);

        recyclerView = findViewById(R.id.overviewRecyclerView);
        totalOverviewContainer = findViewById(R.id.totalOverviewContainer);

        findViewById(R.id.overviewBackButton).setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<Account> accounts = BalanceManager.loadAccounts(this);

        if (accounts.isEmpty()) {
            accounts.add(new Account("Expenses", 0.00, "USD"));
            BalanceManager.saveAccounts(this, accounts);
        }

        // Group total balances by currency independently without combining them
        Map<String, Double> currencyTotals = new LinkedHashMap<>();
        for (Account a : accounts) {
            String curr = a.getCurrency();
            if (curr == null || curr.trim().isEmpty()) {
                curr = "USD";
            }
            curr = curr.toUpperCase(Locale.US).trim();
            currencyTotals.put(curr, currencyTotals.getOrDefault(curr, 0.0) + a.getBalance());
        }

        totalOverviewContainer.removeAllViews();

        if (currencyTotals.isEmpty()) {
            currencyTotals.put("USD", 0.0);
        }

        int index = 0;
        for (Map.Entry<String, Double> entry : currencyTotals.entrySet()) {
            String curr = entry.getKey();
            double total = entry.getValue();

            LinearLayout block = new LinearLayout(this);
            block.setOrientation(LinearLayout.VERTICAL);
            if (index > 0) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.topMargin = (int) (12 * getResources().getDisplayMetrics().density);
                block.setLayoutParams(lp);
            }

            TextView labelTv = new TextView(this);
            labelTv.setText("Total Balance (" + curr + ")");
            labelTv.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            labelTv.setTextSize(13);

            TextView valueTv = new TextView(this);
            valueTv.setTypeface(null, android.graphics.Typeface.BOLD);
            valueTv.setTextColor(ContextCompat.getColor(this, R.color.light_green));
            valueTv.setTextSize(currencyTotals.size() > 1 ? 24 : 28);

            String formattedValue;
            if ("LBP".equalsIgnoreCase(curr)) {
                formattedValue = CurrencyFormatter.formatLbpAmount(total);
            } else {
                formattedValue = String.format(Locale.US, "%,.2f", total);
            }
            valueTv.setText(formattedValue);

            block.addView(labelTv);
            block.addView(valueTv);

            totalOverviewContainer.addView(block);
            index++;
        }

        // This-month cash-in / cash-out per account
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long monthStart = cal.getTimeInMillis();

        List<Transaction> all = TransactionDbHelper.getInstance(this).getAllTransactionsAscending();
        Map<String, double[]> monthTotals = new java.util.HashMap<>(); // name -> [in, out]
        for (Transaction t : all) {
            if (t.getTimestamp() < monthStart) continue;
            String acc = t.getAccount() == null ? "" : t.getAccount();
            if (acc.isEmpty()) acc = "Expenses";
            
            double[] totals = monthTotals.get(acc);
            if (totals == null) {
                totals = new double[]{0, 0};
                monthTotals.put(acc, totals);
            }
            if (t.isCashIn()) {
                totals[0] += t.getAmount();
            } else {
                totals[1] += t.getAmount();
            }
        }

        recyclerView.setAdapter(new OverviewAdapter(accounts, monthTotals, account -> {
            Intent intent = new Intent(this, ExpensesActivity.class);
            intent.putExtra("active_account", account.getName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }));
    }

    private static class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.ViewHolder> {
        private final List<Account> accounts;
        private final Map<String, double[]> monthTotals;
        private final OnAccountClickListener listener;

        interface OnAccountClickListener {
            void onAccountClick(Account account);
        }

        OverviewAdapter(List<Account> accounts, Map<String, double[]> monthTotals, OnAccountClickListener listener) {
            this.accounts = new ArrayList<>(accounts);
            this.monthTotals = monthTotals;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_account_summary_row, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Account account = accounts.get(position);
            holder.name.setText(account.getName());

            String curr = account.getCurrency();
            if (curr == null || curr.trim().isEmpty()) curr = "USD";

            if ("LBP".equalsIgnoreCase(curr)) {
                holder.balance.setText(CurrencyFormatter.formatLbpAmount(account.getBalance()));
            } else {
                holder.balance.setText(String.format(Locale.US, "%,.2f", account.getBalance()));
            }

            double[] totals = monthTotals.get(account.getName());
            double in = totals != null ? totals[0] : 0;
            double out = totals != null ? totals[1] : 0;

            if ("LBP".equalsIgnoreCase(curr)) {
                holder.in.setText("This month In: " + CurrencyFormatter.formatLbpAmount(in));
                holder.out.setText("This month Out: " + CurrencyFormatter.formatLbpAmount(out));
            } else {
                holder.in.setText(String.format(Locale.US, "This month In: %,.2f", in));
                holder.out.setText(String.format(Locale.US, "This month Out: %,.2f", out));
            }

            holder.itemView.setOnClickListener(v -> listener.onAccountClick(account));
        }

        @Override
        public int getItemCount() {
            return accounts.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, balance, in, out;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.rowAccountName);
                balance = itemView.findViewById(R.id.rowAccountBalance);
                in = itemView.findViewById(R.id.rowAccountIn);
                out = itemView.findViewById(R.id.rowAccountOut);
            }
        }
    }
}
