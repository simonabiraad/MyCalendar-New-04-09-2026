# Implementation Plan - Dark Mode and Border for Accounts Dialog

The user wants to redesign the "Accounts" dialog to use a dark mode theme and add a border to the "ADD ACCOUNTS" button.

## Proposed Changes

### [UI Design]

#### [MODIFY] [dialog_accounts.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/res/layout/dialog_accounts.xml)
- Change root `LinearLayout` background from `@color/hunter_green` to `@drawable/dialog_background_rounded` (which is already dark).
- Change title `TextView` color from `black` to `white`.
- Upgrade `addAccountButton` from `Button` to `com.google.android.material.button.MaterialButton`.
- Add `app:strokeColor="@color/white"` and `app:strokeWidth="2dp"` to `addAccountButton` to provide the requested border.

#### [MODIFY] [item_account.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/res/layout/item_account.xml)
- Change `accountName` and `accountBalance` `TextView` colors from `black` to `white`.
- Change `accountIcon` tint from `black` to `white`.
- (Optional) Adjust the separator view color if necessary for better contrast on dark background.

## Verification Plan

### Automated Tests
- I will check the XML files for any syntax errors using `analyze_file`.
- I will try to render the layout preview using `render_compose_preview` (if applicable, though these are XML views, I can check if they compile).

### Manual Verification
- The user should deploy the app and open the "Accounts" dialog to verify:
    1. The dialog background is dark.
    2. The text is readable (white).
    3. The "ADD ACCOUNTS" button has a white border.
