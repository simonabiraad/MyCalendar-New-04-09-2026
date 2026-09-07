# Layout and Interaction Refinement for New Event Screen

This plan details the changes to restrict selection triggers to specific UI elements (arrows/text) and optimize the bottom action bar's size.

## Proposed Changes

### UI Layout

#### [MODIFY] [activity_notification_edit.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_notification_edit.xml)
- **Date Field**: Wrap the `editDate` TextView in a container with the border and set the TextView to `wrap_content` so only the text itself is clickable.
- **Dropdowns (Priority, Repeat, Reminder)**: Replace standard `Spinner` widgets with a custom layout containing a `TextView` (for the value) and an `ImageView` (for the arrow). The selection trigger will be limited to the arrow.
- **Bottom Bar**: Change `editBottomActions` width to `wrap_content` and reduce padding to minimize the frame size as requested.

### Logic Implementation

#### [MODIFY] [NotificationDetailsActivity.java](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/java/com/example/mycalendar2026sar/NotificationDetailsActivity.java)
- Update `setupEditUI` to reference the new `TextView` and `ImageView` components.
- Implement `showSelectionMenu` helper method to display `PopupMenu` when dropdown arrows are clicked.
- Update data binding and saving logic to work with `TextView` values instead of `Spinner` selections.

## Verification Plan

### Manual Verification
1. **Date Selection**: Click on the date text to ensure it opens the picker, then click in the empty space within the border to verify it *does not* open.
2. **Dropdowns**: Click on the "Priority", "Repeat", and "Reminder" text to ensure they *do not* open the menu. Click the small down arrows to verify they *do* open the selection menu.
3. **Bottom Bar**: Verify the Cancel/Save bar at the bottom is shrunk to fit the buttons and positioned at the bottom right.
4. **Saving**: Create/Edit an event and verify all selections are saved correctly to the database.
