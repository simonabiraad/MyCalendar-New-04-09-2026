# Change Voice and Notification Emojis to Colored Logos

This plan describes how to replace the emoji-based icons in the application's header with "real" colored logos (vector drawables) as requested.

## User Review Required

> [!IMPORTANT]
> The top right buttons in `MainActivity` and `ExpensesActivity` will be changed from `Button` widgets (using emojis like 🎙 and 🔔) to `ImageButton` widgets using colored vector drawables. This improves the visual consistency with the bottom bar.

## Proposed Changes

### Layouts

#### [MODIFY] [activity_main.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_main.xml)
- Change `aiAssistantButton` from `Button` to `ImageButton`.
- Change `notificationSettingsButton` from `Button` to `ImageButton`.
- Remove `android:text` (🎙 and 🔔).
- Set `android:src="@drawable/ic_notif_voice_color"` for the voice button.
- Set `android:src="@drawable/ic_menu_reminder_color"` for the notification button.
- Set `android:background="?attr/selectableItemBackgroundBorderless"` to remove the grey box background.
- Adjust padding and scale type for better appearance.

#### [MODIFY] [activity_expenses.xml](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_expenses.xml)
- Change `aiAssistantButton` from `Button` to `ImageButton`.
- Remove `android:text="🎙"`.
- Set `android:src="@drawable/ic_notif_voice_color"`.
- Set `android:background="?attr/selectableItemBackgroundBorderless"`.

---

### Java Code

#### [MODIFY] [MainActivity.java](file:///C:/Users/simon/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/java/com/example/mycalendar2026sar/MainActivity.java)
- Update variable types from `Button` to `ImageButton` for `aiBtn` and `notifyBtn`.
- Remove code that applies font settings or background tints to these specific buttons, as they will now be transparent-background icons.

## Verification Plan

### Manual Verification
- Deploy the app to the device.
- Check the top right of the main screen to see the colored microphone and yellow bell.
- Check the `ExpensesActivity` to see the colored microphone.
- Verify that clicking these icons still triggers the correct actions (Voice Assistant and Notification Settings).
