package com.example.mycalendar2026sar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

/**
 * Settings for the Expenses section. The password-lock switch reads/writes
 * the same "SecuritySettings" prefs (exp_password_disabled / custom_password)
 * that MainActivity already uses to gate entry into Expenses, so toggling it
 * here actually changes whether a password/biometric prompt appears next
 * time Expenses is opened from the main menu.
 */
public class ExpensesSettingsActivity extends AppCompatActivity {

    private SharedPreferences securityPrefs;
    private Switch lockSwitch;
    private boolean updatingProgrammatically = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_settings);

        securityPrefs = getSharedPreferences("SecuritySettings", MODE_PRIVATE);
        lockSwitch = findViewById(R.id.lockExpensesSwitch);

        findViewById(R.id.settingsBackButton).setOnClickListener(v -> finish());

        TextView versionText = findViewById(R.id.appVersionText);
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionText.setText("Version " + versionName);
        } catch (Exception ignored) {
            // Keep the default "Version 1.0" text from the layout.
        }

        refreshSwitchState();
        lockSwitch.setOnCheckedChangeListener(this::onLockSwitchChanged);

        findViewById(R.id.clearDataRow).setOnClickListener(v -> confirmClearData());
    }

    private void refreshSwitchState() {
        updatingProgrammatically = true;
        boolean passwordDisabled = securityPrefs.getBoolean("exp_password_disabled", false);
        lockSwitch.setChecked(!passwordDisabled);
        updatingProgrammatically = false;
    }

    private void onLockSwitchChanged(CompoundButton button, boolean isChecked) {
        if (updatingProgrammatically) return;

        if (isChecked) {
            // Turning protection ON never needs proof - just require it from now on.
            securityPrefs.edit().putBoolean("exp_password_disabled", false).apply();
            Toast.makeText(this, "Expenses now requires a password.", Toast.LENGTH_SHORT).show();
        } else {
            // Turning protection OFF requires proving identity first, same as
            // the rest of the app's security toggles.
            verifyThenDisable();
        }
    }

    private void verifyThenDisable() {
        String customPass = securityPrefs.getString("custom_password", null);
        if (customPass != null) {
            EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                    .setTitle("Verify Identity")
                    .setMessage("Enter password to disable Expenses protection:")
                    .setView(input)
                    .setPositiveButton("Verify", (d, w) -> {
                        if (input.getText().toString().trim().equals(customPass)) {
                            disableProtection();
                        } else {
                            Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
                            refreshSwitchState();
                        }
                    })
                    .setNegativeButton("Cancel", (d, w) -> refreshSwitchState())
                    .setOnCancelListener(d -> refreshSwitchState())
                    .show();
        } else {
            Executor executor = ContextCompat.getMainExecutor(this);
            BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor,
                    new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationSucceeded(@androidx.annotation.NonNull BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            disableProtection();
                        }

                        @Override
                        public void onAuthenticationError(int errorCode, @androidx.annotation.NonNull CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                            refreshSwitchState();
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                            refreshSwitchState();
                        }
                    });

            BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Verify Identity")
                    .setSubtitle("Confirm to disable Expenses protection")
                    .setNegativeButtonText("Cancel")
                    .build();
            biometricPrompt.authenticate(promptInfo);
        }
    }

    private void disableProtection() {
        securityPrefs.edit().putBoolean("exp_password_disabled", true).apply();
        Toast.makeText(this, "Expenses protection disabled.", Toast.LENGTH_SHORT).show();
        refreshSwitchState();
    }

    private void confirmClearData() {
        new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                .setTitle("Clear All Expense Data")
                .setMessage("This permanently deletes every transaction (accounts are kept). This cannot be undone.")
                .setPositiveButton("Clear", (d, w) -> {
                    TransactionDbHelper.getInstance(this).clearAllTransactions();
                    Toast.makeText(this, "All transactions cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
