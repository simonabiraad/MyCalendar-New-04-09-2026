# Implementation Plan - Custom Settings Menu with Sub-Panel

This plan describes how to replace the existing `PopupMenu` with a custom menu implementation that supports a "Settings" button with a toggleable sub-panel appearing beside the main menu.

## User Review Required

> [!IMPORTANT]
> The standard `PopupMenu` will be replaced with a custom `View`-based menu to allow for the complex "side panel" interaction requested. This menu will be overlaid on the existing content.

## Proposed Changes

### Resources

#### [MODIFY] [strings.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/values/strings.xml)
- Add string for "Settings".
- Add string for "About" and "Exit" if missing from previous fixes (though they were in `main_popup_menu.xml`).

#### [NEW] [ic_arrow_down.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/drawable/ic_arrow_down.xml)
- A simple downward arrow vector for the Settings toggle.

### Layouts

#### [NEW] [layout_custom_menu.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/layout_custom_menu.xml)
- A vertical list of menu items following the design in the screenshot.
- Includes: New Note, New Voice Note, Events, New Sticky Note, Secure Box, Expenses, **Settings** (with arrow), Privacy Policy, About, Exit.

#### [NEW] [layout_settings_panel.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/layout_settings_panel.xml)
- The sub-panel containing: Change Password, Notification Settings, Toggle Quick Note Bar, Themes, Change Color, Change Font, Backup Data, Print.

#### [MODIFY] [activity_main.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_main.xml)
- Add a `View` (dimmer) to handle background clicks and menu dismissal.
- Include `layout_custom_menu.xml` and `layout_settings_panel.xml` at the end of the root `ConstraintLayout`.

### Logic

#### [MODIFY] [MainActivity.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/java/com/example/mycalendar2026sar/MainActivity.java)
- Remove `PopupMenu` implementation for `mainMenuButton`.
- Initialize custom menu views and the Settings panel.
- Implement toggle logic for the main menu and sub-panel.
- Wire item clicks to the existing action methods (e.g., `showNewNoteDialog`, `launchSecureBox`, `showThemeOptionsDialog`, etc.).

## Verification Plan

### Automated Tests
- Build the project to ensure no resource or compilation errors.

### Manual Verification
1. Open the app and tap the menu button (top left).
2. Verify the custom menu appears on the left.
3. Tap the Settings row/arrow.
4. Verify the Settings sub-panel appears directly to the right of the main menu.
5. Verify all Settings options are present in the correct order.
6. Tap the Settings arrow again and verify the sub-panel hides.
7. Tap a background area and verify the menu closes.
8. Verify each menu item triggers its intended action.
