# Implementation Plan - Redesign Accounts List Dialog

The goal is to redesign the "Accounts" list dialog to match the professional dark-themed visual style of the app, including white text, an outlined "ADD ACCOUNTS" button, and a dark rounded background.

## User Review Required

> [!IMPORTANT]
> - The dialog background will change from green to dark gray/black.
> - The "ADD ACCOUNTS" button will now have a green border instead of a solid green fill.
> - All text and icons within the list will be updated to be legible in dark mode.

## Proposed Changes

### [Component Name] UI Resources

#### [NEW] `drawable/button_outline_green.xml`
- A shape drawable with a green stroke and rounded corners for the "Add Accounts" action.

### [Component Name] Dialog Layouts

#### [MODIFY] [dialog_accounts.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/res/layout/dialog_accounts.xml)
- Change root background to `@drawable/dialog_background_rounded`.
- Set title text color to `@color/white`.
- Update `addAccountButton`:
    - Set background to `@drawable/button_outline_green`.
    - Set text color to `@color/light_green`.
    - Set `textAllCaps="true"` for a professional look.

#### [MODIFY] [item_account.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/res/layout/item_account.xml)
- Change `accountName` and `accountBalance` text color to `@color/white`.
- Change `accountIcon` tint to `@color/text_secondary`.
- Update edit mode icons (Move Up/Down) to have white tints for better visibility.

### [Component Name] Logic Integration

#### [MODIFY] [ExpensesActivity.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/java/com/example/mycalendar2026sar/ExpensesActivity.java)
- In `showAccountsDialog()`:
    - Set the dialog window background to transparent to properly show the rounded corners of the custom layout.

## Verification Plan

### Automated Tests
- Build the project to ensure XML validity and resource mapping.

### Manual Verification
1. Open **Expenses** -> Tap the top button to open the **Accounts** list.
2. Verify the dialog matches the "Dark Mode" aesthetic (Dark background, white text).
3. Verify the **ADD ACCOUNTS** button is outlined with a green border.
4. Verify account names and balances are clearly visible in the list.
