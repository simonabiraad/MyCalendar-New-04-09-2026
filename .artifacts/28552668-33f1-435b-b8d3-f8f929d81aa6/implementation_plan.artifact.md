# Implementation Plan - Task Feature

Add a new "Task" feature to the application, allowing users to manage a list of tasks with completion status (checkbox and strikethrough). The feature will be accessible from the main screen and the custom notification bar.

## User Review Required

> [!IMPORTANT]
> - The "Task" button will be added to the main screen's button container and the custom notification bar for quick access.
> - Tasks will be persisted using `SharedPreferences` in JSON format, ensuring they are saved across app restarts.
> - The UI will follow the established "black background" and "white-bordered card" design language.

## Proposed Changes

### Resources

#### [NEW] [ic_menu_task_color.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-2-08-2026-claude/app/src/main/res/drawable/ic_menu_task_color.xml)
- Create a new vector drawable for the Task icon (a checkbox/list icon).

### UI & Layouts

#### [MODIFY] [activity_main.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-2-08-2026-claude/app/src/main/res/layout/activity_main.xml)
- Add a new `Button` (`taskButton`) in the `buttonContainer`, next to the "Secure Box" button.

#### [MODIFY] [notification_widget.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-2-08-2026-claude/app/src/main/res/layout/notification_widget.xml)
- Add a new `ImageView` (`notif_task_btn`) in the notification bar, next to the Secure Box button.

#### [NEW] [activity_task.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-2-08-2026-claude/app/src/main/res/layout/activity_task.xml)
- Define the new Task page:
    - Black background.
    - "Tasks" title.
    - A card (using `summary_border`) containing:
        - An input area (EditText + Add button).
        - A `RecyclerView` for the task list.

#### [NEW] [item_task.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-2-08-2026-claude/app/src/main/res/layout/item_task.xml)
- Layout for individual task items:
    - `CheckBox` (styled to look like ☐/☑).
    - `TextView` for the task text.

### Logic & Persistence

#### [NEW] [TaskActivity.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-2-08-2026-claude/app/src/main/java/com/example/mycalendar2026sar/TaskActivity.java)
- Activity to manage tasks:
    - Load/Save tasks from `SharedPreferences`.
    - Handle adding new tasks.
    - Implement a `TaskAdapter` with checkbox listeners.
    - Apply `Paint.STRIKE_THRU_TEXT_FLAG` to the TextView when the task is completed.

#### [MODIFY] [MainActivity.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-2-08-2026-claude/app/src/main/java/com/example/mycalendar2026sar/MainActivity.java)
- Set up the click listener for `taskButton`.
- Handle the new `ACTION_TASK` in `handleIntent` to open `TaskActivity` from a notification click.

#### [MODIFY] [QuickNoteNotificationService.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-2-08-2026-claude/app/src/main/java/com/example/mycalendar2026sar/QuickNoteNotificationService.java)
- Define `ACTION_TASK`.
- Configure `notif_task_btn` with a `PendingIntent`.

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-2-08-2026-claude/app/src/main/AndroidManifest.xml)
- Register `TaskActivity`.

## Verification Plan

### Automated Tests
- None.

### Manual Verification
- Deploy to a device/emulator.
- Verify "Task" button appears in the main screen's button list.
- Verify "Task" button appears in the persistent notification bar.
- Add multiple tasks in the Task page.
- Check and uncheck tasks, ensuring the strikethrough updates correctly.
- Close and reopen the app to ensure tasks are persisted.
