# Standardize Top Action Icons Across SAR Calendar

This plan describes how to standardize all top action icons (Back, Plus, Menu, More, Edit) across all pages of the application to follow the professional, logo-style design used for the Voice icon.

## User Review Required

> [!IMPORTANT]
> - All top action buttons will be converted to `ImageButton` widgets with transparent backgrounds (`?attr/selectableItemBackgroundBorderless`).
> - The icons will use colored vector drawables instead of plain white tints or emojis.
> - A consistent size of `@dimen/icon_size_medium` (24-32dp) and padding of `4dp` will be applied.
> - New colored drawables will be created for Back, Menu, and More actions to ensure a unified design language.

## Proposed Changes

### New Drawables

#### [NEW] [ic_back_color.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/drawable/ic_back_color.xml)
- A professional blue back arrow logo.

#### [NEW] [ic_menu_color.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/drawable/ic_menu_color.xml)
- A professional colored hamburger menu logo.

#### [NEW] [ic_more_color.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/drawable/ic_more_color.xml)
- A professional colored "three dots" more options logo.

---

### Layout Modifications

The following layouts will be updated to replace existing header buttons/icons with the standardized `ImageButton` style:

#### [MODIFY] [activity_main.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_main.xml)
- Update `mainMenuButton` to use `ic_menu_color`.
- Standardize size and remove tint.

#### [MODIFY] [activity_task.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_task.xml)
- Update `taskBackButton` to `ic_back_color` and standardize size.
- Update `taskMenuButton` to use `ic_more_color`.
- Update `addTaskButton` to use `ic_notif_plus_color`.

#### [MODIFY] [activity_events.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_events.xml)
- Update `eventBackButton` to `ic_back_color`.
- Update `addEventHeaderButton` to `ic_notif_plus_color`.

#### [MODIFY] [activity_notification_details.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_notification_details.xml)
- Update `backButton` to `ic_back_color`.
- Update `editTopButton` styling.
- Update `moreOptionsButton` to `ic_more_color`.

#### [MODIFY] [activity_notification_edit.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_notification_edit.xml)
- Update `btnBackEdit` to `ic_back_color`.

#### [MODIFY] [activity_secure_box.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_secure_box.xml)
- Update `backButton` to `ic_back_color`.
- Change `addCategoryHeaderButton` from `Button` (+) to `ImageButton` with `ic_notif_plus_color`.

#### [MODIFY] [activity_notebook.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_notebook.xml)
- Update `notebookBackButton` to `ic_back_color`.
- Update `notebookAddButton` to `ic_notif_plus_color`.

#### [MODIFY] [activity_chart.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_chart.xml)
- Update `chartBackButton` to `ic_back_color`.

#### [MODIFY] [activity_summary.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_summary.xml)
- Update `summaryBackButton` to `ic_back_color`.

#### [MODIFY] [activity_account_summary.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_account_summary.xml)
- Update `accountSummaryBackButton` to `ic_back_color`.

#### [MODIFY] [activity_expenses_calendar.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_expenses_calendar.xml)
- Update `backButton` and `moreButton`.

#### [MODIFY] [activity_accounts_overview.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_accounts_overview.xml)
- Update `backButton` to `ic_back_color`.

#### [MODIFY] [activity_add_transaction.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_add_transaction.xml)
- Update `backButton` to `ic_back_color`.

#### [MODIFY] [activity_cash_calculator.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_cash_calculator.xml)
- Update `backButton` to `ic_back_color`.

#### [MODIFY] [activity_report_all.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_report_all.xml)
- Update `backButton` to `ic_back_color`.

---

### Java Code Modifications

#### [MODIFY] [MainActivity.java](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/java/com/example/mycalendar2026sar/MainActivity.java)
- Ensure menu button styling is handled.

#### [MODIFY] [SecureBoxActivity.java](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/java/com/example/mycalendar2026sar/SecureBoxActivity.java)
- Update `addCategoryHeaderButton` reference from `Button` to `ImageButton` to avoid casting errors.

## Verification Plan

### Manual Verification
- Navigate through all pages (Tasks, Events, Notifications, Secure Box, Notebook, etc.).
- Verify that all top action icons have consistent sizing, spacing, and colored logo designs.
- Ensure that clicking these icons still performs the correct action.
