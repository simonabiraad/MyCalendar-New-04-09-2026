# Implementation Plan - Redesign New Event Screen

This plan describes the steps to redesign the "New Event" (and "Edit Event") screen in `NotificationDetailsActivity`. We will add a "Category" field and rearrange Priority, Reminder, and Repeater (Repeat) into a 2x2 grid using styled cards.

## User Review Required

> [!IMPORTANT]
> - The database version will be incremented to 6 to add the `category` column.
> - "Repeater" in the request refers to the existing "Repeat" field in the code.
> - The UI will use the `summary_border` drawable to match the "Start Date" and "End Date" cards.

## Proposed Changes

### Data Model & Persistence

#### [MODIFY] [NotificationEvent.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/java/com/example/mycalendar2026sar/NotificationEvent.java)
- Add `private String category` field.
- Update constructor to include `category`.
- Add getter and setter for `category`.

#### [MODIFY] [TransactionDbHelper.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/java/com/example/mycalendar2026sar/TransactionDbHelper.java)
- Add `COL_NOTIF_CATEGORY = "category"` constant.
- Increment `DB_VERSION` to `6`.
- Update `onCreate` to include `COL_NOTIF_CATEGORY` in the `notifications` table creation.
- Update `onUpgrade` to add the `category` column for users upgrading from version 5.
- Update `addNotification`, `updateNotification`, and `readNotification` to handle the `category` field.

---

### Resources

#### [MODIFY] [arrays.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/values/arrays.xml)
- Add `event_category_options` string-array with "Business" and "Meeting".

#### [MODIFY] [activity_notification_edit.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_notification_edit.xml)
- Rearrange the layout for Priority, Repeat, and Reminder.
- Add Category field.
- Implement 2x2 grid:
    - Row 1: Category | Priority
    - Row 2: Reminder | Repeat (Repeater)
- Each field will be inside a `LinearLayout` or `FrameLayout` with `android:background="@drawable/summary_border"`, containing a `TextView` for the value and an `ImageView` for the dropdown arrow.

---

### Logic

#### [MODIFY] [NotificationDetailsActivity.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/java/com/example/mycalendar2026sar/NotificationDetailsActivity.java)
- Update `setupEditUI` to:
    - Initialize new Category UI elements (`tvCategoryValue`, `btnCategoryArrow`).
    - Update logic for Priority, Repeat, and Reminder to use the new card-based IDs if they changed.
    - Set click listeners for the new cards/arrows to show the popup menu.
    - Update pre-fill logic to include `currentEvent.getCategory()`.
    - Update save logic (`btnSave.setOnClickListener`) to collect and save the Category value.
- Update `bindData` (view mode) to display the Category if desired (optional, but good for consistency).
- Ensure `showPopupMenu` handles the new Category options.

## Verification Plan

### Automated Tests
- N/A (Unit tests for DB might be added if infrastructure exists, but focus is on UI/Logic).

### Manual Verification
1. Open the "New Event" screen.
2. Verify the 2x2 grid layout for Category, Priority, Reminder, and Repeat.
3. Verify that each field is styled as a white-bordered card.
4. Tap the arrows in each card and verify the dropdown options appear.
5. Specifically check Category options: "Business" and "Meeting".
6. Save an event with a specific Category and reopen it to verify persistence.
7. Verify that Priority, Repeat, and Reminder still function as before (including custom options).
