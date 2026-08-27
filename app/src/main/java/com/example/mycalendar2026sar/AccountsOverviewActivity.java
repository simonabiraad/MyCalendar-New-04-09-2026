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
 * Screen showing a list of all account cards with their current balance
 * and this month's cash-in / cash-out totals.
 * Opens when clicking the "Expenses" button to allow account selection.
 */
public class AccountsOverviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView totalBalanceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_overview);

        recyclerView = findViewById(R.id.overviewRecyclerView);
        totalBalanceText = findViewById(R.id.totalOverviewBalance);

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
            accounts.add(new Account("Expenses", 0.00));
            BalanceManager.saveAccounts(this, accounts);
        }

        double totalBalance = 0;
        for (Account a : accounts) {
            totalBalance += a.getBalance();
        }
        totalBalanceText.setText(String.format(Locale.US, "%,.2f", totalBalance));

        // This-month cash-in / cash-out per account
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
        private final java.util.Map<String, double[]> monthTotals;
        private final OnAccountClickListener listener;

        interface OnAccountClickListener {
            void onAccountClick(Account account);
        }

        OverviewAdapter(List<Account> accounts, java.util.Map<String, double[]> monthTotals, OnAccountClickListener listener) {
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
            holder.balance.setText(String.format(Locale.US, "%,.2f", account.getBalance()));

            double[] totals = monthTotals.get(account.getName());
            double in = totals != null ? totals[0] : 0;
            double out = totals != null ? totals[1] : 0;
            holder.in.setText(String.format(Locale.US, "This month In: %,.2f", in));
            holder.out.setText(String.format(Locale.US, "This month Out: %,.2f", out));

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
