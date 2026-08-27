# Implementation Plan - Redesign Account Summary

Redesign the **Account Summary** screen to match the user's specific reference image, featuring a detailed table report with filtering and account selection. Crucially, this only affects the Account Summary page; the Summary and Expenses pages remain unchanged.

## User Review Required

> [!IMPORTANT]
> - The **Account Summary** will be converted from a card-based account list to a **detailed table report** showing daily/periodic breakdowns (Date, Cash In, Cash Out, Savings).
> - It will include an **Account Picker** in the title to switch between different accounts (e.g., "Cash Book").
> - The design will feature oval filter buttons (All, Weekly, etc.), a gray navigation bar, and a boxed footer for totals.

## Proposed Changes

### UI Components

#### [NEW] [item_summary_table_row.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/res/layout/item_summary_table_row.xml)
- Define a table-style row layout with 4 columns: Date, Cash In (Green), Cash Out (Red), and Savings (White).

#### [MODIFY] [activity_account_summary.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/res/layout/activity_account_summary.xml)
- **Toolbar**: Add back arrow, Title with dropdown arrow, Calendar icon, and Report icon.
- **Filters**: Add oval buttons for All, Weekly, Monthly, Yearly.
- **Sub-header**: Add a gray banner for the current filter/period label.
- **Table Headers**: Add labels for Date, Cash In, Cash Out, Savings.
- **RecyclerView**: Update to display the table rows.
- **Footer**: Add the boxed layout for Total Cash In, Total Cash Out, and Balance.

### Activity Logic

#### [MODIFY] [AccountSummaryActivity.java](file:///C:/Users/simon/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/java/com/example/mycalendar2026sar/AccountSummaryActivity.java)
- **Account Selection**: Implement a `PopupMenu` click listener on the title to select accounts.
- **Filtering**: Implement logic for All, Weekly, Monthly, Yearly filters.
- **Data Aggregation**: Group transactions for the selected account by date (or period) and calculate daily totals for In, Out, and Savings.
- **Footer Updates**: Dynamically update the grand totals at the bottom.

## Verification Plan

### Manual Verification
- Open **Menu -> Account Summary**.
- Verify the interface exactly matches Image 1.
- Select different accounts from the title dropdown and ensure data updates correctly.
- Test the filter buttons (All, Weekly, Monthly, Yearly) and verify the period labels and table data change.
- Verify colors: Cash In is Green, Cash Out is Red. Savings/Balance is Green for positive and Red for negative.
- Confirm that **Menu -> Summary** still opens the old card-based view (or whatever its current state is).
