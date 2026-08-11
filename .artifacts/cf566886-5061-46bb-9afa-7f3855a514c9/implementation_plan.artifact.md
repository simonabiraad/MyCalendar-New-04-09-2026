# Implementation Plan - Task Voice Input and UI Expansion

The goal is to enhance the Task page by making the "Add New Task" area larger for easier multi-line input and adding a voice-to-text button for hands-free task creation.

## User Review Required

> [!IMPORTANT]
> The "Add a new task..." input box will now be taller to comfortably fit two lines of text. A new colored Voice button will be added next to the Add (+) button.

## Proposed Changes

### Task Layout

#### [MODIFY] [activity_task.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/res/layout/activity_task.xml)
- Update `taskInput` (`EditText`):
    - Set `android:minLines="2"` and `android:gravity="top"` to support two lines of text.
    - Adjust padding for better vertical alignment.
- Add `voiceTaskButton` (`ImageButton`):
    - Position it between `taskInput` and `addTaskButton`.
    - Use `@drawable/ic_menu_voice_note_color` for a professional, colored look.
    - Set `background="?attr/selectableItemBackgroundBorderless"`.

### Task Logic

#### [MODIFY] [TaskActivity.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/java/com/example/mycalendar2026sar/TaskActivity.java)
- Implement `voiceRecognitionLauncher` using `ActivityResultContracts.StartActivityForResult()`.
- Add `startVoiceRecognition()` method to launch the system speech recognizer.
- Set an `OnClickListener` for the new `voiceTaskButton` to trigger `startVoiceRecognition()`.
- Update the launcher callback to append the spoken text into the `taskInput` field.

## Verification Plan

### Automated Tests
- Perform a build to ensure XML and Java changes are valid.

### Manual Verification
- Open the Task page.
- Verify the input area is visibly taller.
- Type two lines of text to ensure it wraps correctly and remains readable.
- Tap the new Voice button.
- Speak a task and verify that the text correctly appears in the input box.
