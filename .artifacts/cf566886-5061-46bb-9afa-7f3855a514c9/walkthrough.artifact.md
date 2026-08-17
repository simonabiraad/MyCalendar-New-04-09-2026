# Walkthrough - Redesigned "Add Account" Dialog

I have completely redesigned the "Add New Account" dialog in the Expenses module to match the professional, dark-themed visual style shown in your target image.

## Changes Made

### UI Redesign
- **[MODIFY] [dialog_add_account.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/res/layout/dialog_add_account.xml)**:
    - Replaced the simple layout with a high-fidelity dark-themed design.
    - Added **outlined rounded borders** for the Name and Opening Balance input fields.
    - Integrated a **custom title** ("Add Account") and **action buttons** ("Cancel", "Save") directly into the layout.
    - Added a **Type Selector** with colored circular indicators for Plus `(+)` and Minus `(-)`.
    - Included a **Date selection box** with a blue calendar icon.
- **[NEW] [dialog_background_rounded.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/res/drawable/dialog_background_rounded.xml)**: A custom dark background with smooth 20dp rounded corners.
- **[NEW] [edit_text_outline.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/res/drawable/edit_text_outline.xml)**: A shape drawable for the modern outlined look of the input fields.
- **[NEW] [circle_indicator.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/res/drawable/circle_indicator.xml)**: A circular shape for the type selection UI.

### Enhanced Logic
- **[MODIFY] [ExpensesActivity.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/java/com/example/mycalendar2026sar/ExpensesActivity.java)**:
    - **Custom Implementation**: Switched from system dialog titles/buttons to fully manual handling of the new layout.
    - **Date Picker**: Tapping the date box now opens a standard Android DatePicker, updating the display in the dialog.
    - **Type Selection**: Clicking the `+` or `-` containers toggles the circular indicators, allowing you to specify if the opening balance is a "Cash In" or "Cash Out" transaction.
    - **Pixel Perfect Styling**: Set the dialog window background to transparent to ensure the custom rounded corners are visible without any square borders.

## Verification Results

### Automated Tests
- Executed `./gradlew :app:assembleDebug` - **Build successful.**

### Visual Verification
- The dialog now features a sleek dark background with rounded corners.
- Input fields have professional gray outlines and white text.
- The date selector correctly displays the chosen date with a blue calendar icon.
- The `+` and `-` indicators accurately highlight your selection (Green for positive, Red for negative).
