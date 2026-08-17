package com.example.mycalendar2026sar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Read-only overview of every account: current balance plus this month's
 * cash-in / cash-out totals for that account. Pulls accounts from
 * BalanceManager and transactions from TransactionDbHelper, so it always
 * matches whatever is shown in the Accounts dialog and the main ledger.
 */
public class AccountSummaryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView totalBalanceText;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_summary);

        recyclerView = findViewById(R.id.accountSummaryRecyclerView);
        totalBalanceText = findViewById(R.id.totalBalanceText);
        emptyText = findViewById(R.id.accountSummaryEmptyText);

        findViewById(R.id.accountSummaryBackButton).setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<Account> accounts = BalanceManager.loadAccounts(this);

        // Pre-populate if empty
        if (accounts.isEmpty()) {
            accounts.add(new Account("Expenses", 0.00));
            BalanceManager.saveAccounts(this, accounts);
        }

        double totalBalance = 0;
        for (Account a : accounts) {
            totalBalance += a.getBalance();
        }
        totalBalanceText.setText(String.format(Locale.getDefault(), "%.2f", totalBalance));

        recyclerView.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);

        // This-month cash-in / cash-out per account, computed from the shared
        // transaction database so it stays consistent with the ledger.
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long monthStart = cal.getTimeInMillis();

        List<Transaction> all = TransactionDbHelper.getInstance(this).getAllTransactionsAscending();
        java.util.Map<String, double[]> monthTotals = new java.util.HashMap<>(); // name -> [in, out]
        for (Transaction t : all) {
            if (t.getTimestamp() < monthStart) continue;
            String acc = t.getAccount() == null ? "" : t.getAccount();
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

        recyclerView.setAdapter(new SummaryAdapter(accounts, monthTotals, account -> {
            Intent intent = new Intent(this, ExpensesActivity.class);
            intent.putExtra("active_account", account.getName());
            startActivity(intent);
        }));
    }

    private static class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.ViewHolder> {
        private final List<Account> accounts;
        private final java.util.Map<String, double[]> monthTotals;
        private final OnAccountClickListener listener;

        interface OnAccountClickListener {
            void onAccountClick(Account account);
        }

        SummaryAdapter(List<Account> accounts, java.util.Map<String, double[]> monthTotals, OnAccountClickListener listener) {
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
            holder.balance.setText(String.format(Locale.getDefault(), "%.2f", account.getBalance()));

            double[] totals = monthTotals.get(account.getName());
            double in = totals != null ? totals[0] : 0;
            double out = totals != null ? totals[1] : 0;
            holder.in.setText(String.format(Locale.getDefault(), "This month In: %.2f", in));
            holder.out.setText(String.format(Locale.getDefault(), "This month Out: %.2f", out));

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
