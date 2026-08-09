# Implementation Plan - Expenses Calendar View

The goal is to implement a new "Calendar" view within the Expenses module that matches the design shown in the user's provided image. This view will allow users to see their financial data (Cash In, Cash Out, Balance) in a monthly calendar format.

## User Review Required

> [!IMPORTANT]
> I will be creating a new activity `ExpensesCalendarActivity` to host this view. Clicking "Calendar" in the Expenses drawer will now open this new activity instead of closing the Expenses module.

## Proposed Changes

### [Component Name] Layouts

#### [NEW] [activity_expenses_calendar.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/res/layout/activity_expenses_calendar.xml)
- Implement the UI shown in the first image:
    - **Toolbar**: Back button, Title "Calendar", and Overflow menu.
    - **Date Navigator**: Horizontal layout with `<` button, current month range text, and `>` button.
    - **Weekday Labels**: Row with "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun".
    - **Calendar Grid**: `GridView` to display days of the month.
    - **Totals Footer**: A table-like footer showing "Total Cash In", "Total Cash Out", and "Balance" with their respective sums.

#### [NEW] [item_expenses_calendar_day.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/res/layout/item_expenses_calendar_day.xml)
- A simple layout for each day cell in the calendar grid, showing the day number.

### [Component Name] Java Logic

#### [NEW] [ExpensesCalendarActivity.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/java/com/example/mycalendar2026sar/ExpensesCalendarActivity.java)
- Handle the calendar logic:
    - Populate the grid with the correct days for the selected month.
    - Implement month navigation (previous/next).
    - Query `TransactionDbHelper` to get all transactions for the visible month.
    - Calculate and display total Cash In, Cash Out, and Balance for the month in the footer.
    - (Optional/Future) Highlight days that have transactions.

#### [MODIFY] [ExpensesActivity.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/java/com/example/mycalendar2026sar/ExpensesActivity.java)
- Update the `NavigationView` listener: Change the behavior of `R.id.nav_calendar` to launch `ExpensesCalendarActivity` instead of calling `finish()`.

### [Component Name] Manifest

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/AndroidManifest.xml)
- Register `ExpensesCalendarActivity`.

## Verification Plan

### Automated Tests
- Run Gradle build to ensure no compilation errors.

### Manual Verification
- Open the Expenses module.
- Open the drawer and tap "Calendar".
- Verify the new Calendar screen opens and matches the design in the provided image.
- Verify that navigating between months works correctly.
- Verify that the totals at the bottom reflect the data in the database for that month.
