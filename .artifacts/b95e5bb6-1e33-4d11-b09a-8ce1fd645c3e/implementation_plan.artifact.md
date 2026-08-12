# Implementation Plan - Convert Account Chart to Wealth Distribution

Modify the first donut chart in the Analytics screen to show the current balance distribution across all accounts (Wealth Distribution) instead of spending per account.

## User Review Required

> [!IMPORTANT]
> - **Data Shift**: The first chart will now represent your **Current Account Balances** (how much money you have in each account) rather than how much you spent from them.
> - **Center Text**: The text in the center of the first donut will change from "Total spent" to **"Total accounts"**.
> - **Total Value**: The total displayed will be the sum of all account balances.
> - **Consistency**: The "Spending by Category" chart will remain unchanged, showing your actual expenses.

## Proposed Changes

### Layout Changes

#### [MODIFY] [activity_chart.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/res/layout/activity_chart.xml)
- Update the section header from "Spending by Account" to **"Total Accounts Breakdown"**.

### Activity Changes

#### [MODIFY] [ChartActivity.java](file:///C:/Users/simon/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/java/com/example/mycalendar2026sar/ChartActivity.java)
- **Add Account Loader**: Implement `loadAccountsFromPrefs()` to fetch real-time account balances from shared preferences.
- **Update `setupCharts`**: Change `setupPieChartAccount(transactions)` to just `setupPieChartAccount()` since it will now fetch its own state data.
- **Refactor `generateCenterText`**: Allow passing a custom label (e.g., "Total accounts" or "Total spent").
- **Rewrite `setupPieChartAccount`**:
    - Use account balances to calculate proportions and total wealth.
    - Set the center text to "Total accounts".
    - Populate the detailed list underneath with account balances and their share percentages.

## Verification Plan

### Manual Verification
1.  **Header Check**: Confirm the top section is now titled "Total Accounts Breakdown".
2.  **Center Text**: Verify the first donut says **"Total accounts"** in the center.
3.  **Data Check**:
    - Confirm the values in the first chart match your account balances (Cash, Bank, etc.) in the Expenses screen.
    - Confirm the second chart still correctly shows your **Spending by Category**.
4.  **Visuals**: Ensure account colors and percentages are clearly displayed inside the segments.
