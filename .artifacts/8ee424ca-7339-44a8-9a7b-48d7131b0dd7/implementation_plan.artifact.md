# Implementation Plan - Account-Specific Totals in Expenses

This plan ensures that individual account screens in the Expenses section show their own separate "Total Cash In", "Total Cash Out", and "Balance" at the bottom, while preserving the existing global "Expenses" summary functionality.

## User Review Required

> [!IMPORTANT]
> The bottom totals for individual accounts will now be calculated based on the transactions visible in that account's ledger for the selected period, rather than showing the overall account balance. The "Expenses" summary view will remain unchanged.

## Proposed Changes

### Expenses Module

#### [MODIFY] [ExpensesActivity.java](file:///C:/Users/simon/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/java/com/example/mycalendar2026sar/ExpensesActivity.java)

- In `refreshTransactionsList()`:
    - Update the loop that groups transactions to calculate `cashIn` for all accounts (removing the `isSummaryMode` check for income summation).
    - Update the footer logic:
        - For individual accounts (`!isSummaryMode`), set the label to "TOTAL Cash In" (or "Total Cash In").
        - Use the calculated `cashIn` and `cashOut` sums to display "Total Cash In", "Total Cash Out", and "Balance".
        - The balance will be calculated as `cashIn - cashOut`.
    - Ensure the "Expenses" summary mode (`isSummaryMode == true`) continues to use its existing logic for global aggregation.

## Verification Plan

### Manual Verification
1.  **Expenses Summary Check**: Open the Expenses section. By default, it should show aggregated totals for all accounts. Verify "TOTAL Cash In", "TOTAL Cash Out", and "Balance" reflect the global state.
2.  **Individual Account Check**:
    - Select an account (e.g., "Main" or a custom account).
    - Observe the footer. It should now show "TOTAL Cash In", "TOTAL Cash Out", and "Balance".
    - Add a "Cash In" transaction (e.g., 500) and a "Cash Out" transaction (e.g., 200) to this specific account.
    - Verify the footer shows:
        - Total Cash In: 500.00
        - Total Cash Out: 200.00
        - Balance: 300.00
3.  **Account Independence Check**: Switch to a different individual account. Verify that its footer totals are independent of the transactions added to the first account.
4.  **Filter Check**: Apply a date filter (e.g., "Today"). Verify the account-specific totals update to reflect only transactions within that period for that account.
