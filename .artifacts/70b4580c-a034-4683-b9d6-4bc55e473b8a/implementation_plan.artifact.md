# Implementation Plan - Notification Details Page & Calendar Integration

This plan outlines the steps to implement a dedicated **Notification Details Page** and integrate it with the existing calendar system, providing a robust event management experience.

## Goal Description
Upgrade the existing note/reminder system to a full-featured "Notification Event" system. Users will be able to:
- Tap a day in the calendar to see a dedicated "Day Details" page.
- Tap an event to open a "Notification Details Page" with rich fields (Priority, Status, Recurrence, Reminder, Attachments, etc.).
- Maintain synchronization between Notifications and Tasks.
- Ensure all data persists in the local database.

## Proposed Changes

### 1. Data Layer & Models

#### [MODIFY] [TransactionDbHelper.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/java/com/example/mycalendar2026sar/TransactionDbHelper.java)
- Add a new table `notifications`.
- Columns: `id`, `title`, `notes`, `date`, `start_time`, `end_time`, `priority`, `status`, `repeat`, `reminder`, `location`, `attachments`, `voice_note_path`, `history`.
- Update `onCreate` and `onUpgrade`.

#### [NEW] [NotificationEvent.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/java/com/example/mycalendar2026sar/NotificationEvent.java)
- Data model for notification events.

### 2. UI Components & Layouts

#### [NEW] [activity_day_details.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_day_details.xml)
- Layout for the dedicated Day page (lists events for the day).

#### [NEW] [activity_notification_details.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_notification_details.xml)
- Comprehensive layout for the Notification Details Page including all requested fields and quick actions.

#### [NEW] [item_notification_event.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/item_notification_event.xml)
- Row layout for events in the Day page.

### 3. Activities

#### [NEW] [DayDetailsActivity.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/java/com/example/mycalendar2026sar/DayDetailsActivity.java)
- Logic to display events for a selected date.

#### [NEW] [NotificationDetailsActivity.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/java/com/example/mycalendar2026sar/NotificationDetailsActivity.java)
- Logic to view/edit/delete/convert notification events.
- Handles voice notes, attachments, and reminders.

### 4. Integration & Logic

#### [MODIFY] [MainActivity.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/java/com/example/mycalendar2026sar/MainActivity.java)
- Update `CalendarAdapter` to launch `DayDetailsActivity` on tap.
- Update `notificationSettingsButton` to lead to a custom "Notification Calendar" view if required, or keep current flow if the calendar is the home screen.

#### [MODIFY] [ReminderReceiver.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/java/com/example/mycalendar2026sar/ReminderReceiver.java)
- Update to handle the new `NotificationEvent` model and display detailed notifications.

#### [MODIFY] [TaskActivity.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/java/com/example/mycalendar2026sar/TaskActivity.java)
- Add logic to accept data from a Notification when converting.

## Verification Plan

### Automated Tests
- Unit tests for recurrence logic and status transitions.
- Build verification to ensure no regressions in existing features.

### Manual Verification
- Deploy to device/emulator.
- Navigate to a day, add an event.
- Edit all fields (Priority, Status, Repeat) and verify persistence.
- Record a voice note and attach a file.
- Verify that reminders trigger at the correct time.
- Verify synchronization with the Task system.
