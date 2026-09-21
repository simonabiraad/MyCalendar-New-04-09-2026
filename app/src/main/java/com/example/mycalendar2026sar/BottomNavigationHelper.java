package com.example.mycalendar2026sar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.widget.Button;

public class BottomNavigationHelper {

    public static void setupBottomNavigation(final Activity activity, int activeTabId) {
        Button homeBtn = activity.findViewById(R.id.navHomeButton);
        Button eventBtn = activity.findViewById(R.id.navEventButton);
        Button taskBtn = activity.findViewById(R.id.navTaskButton);
        Button secureBtn = activity.findViewById(R.id.navSecureBoxButton);
        Button expensesBtn = activity.findViewById(R.id.navExpensesButton);

        if (homeBtn == null) return;

        // Reset all to white
        int activeGreen = Color.parseColor("#8BC34A");
        homeBtn.setTextColor(Color.WHITE);
        eventBtn.setTextColor(Color.WHITE);
        taskBtn.setTextColor(Color.WHITE);
        secureBtn.setTextColor(Color.WHITE);
        expensesBtn.setTextColor(Color.WHITE);

        // Set active to green
        if (activeTabId == R.id.navHomeButton) homeBtn.setTextColor(activeGreen);
        else if (activeTabId == R.id.navEventButton) eventBtn.setTextColor(activeGreen);
        else if (activeTabId == R.id.navTaskButton) taskBtn.setTextColor(activeGreen);
        else if (activeTabId == R.id.navSecureBoxButton) secureBtn.setTextColor(activeGreen);
        else if (activeTabId == R.id.navExpensesButton) expensesBtn.setTextColor(activeGreen);

        homeBtn.setOnClickListener(v -> {
            if (!(activity instanceof MainActivity)) {
                Intent intent = new Intent(activity, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(intent);
            }
        });

        eventBtn.setOnClickListener(v -> {
            if (!(activity instanceof EventsActivity)) {
                Intent intent = new Intent(activity, EventsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(intent);
            }
        });

        taskBtn.setOnClickListener(v -> {
            if (!(activity instanceof TaskActivity)) {
                Intent intent = new Intent(activity, TaskActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(intent);
            }
        });

        secureBtn.setOnClickListener(v -> {
            if (!(activity instanceof SecureBoxActivity)) {
                Intent intent = new Intent(activity, SecureBoxActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(intent);
            }
        });

        expensesBtn.setOnClickListener(v -> {
            if (!(activity instanceof ExpensesActivity)) {
                Intent intent = new Intent(activity, ExpensesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(intent);
            }
        });
    }
}
