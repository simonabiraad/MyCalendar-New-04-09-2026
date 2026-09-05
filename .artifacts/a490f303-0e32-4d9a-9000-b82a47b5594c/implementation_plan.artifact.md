# Implementation Plan - Calendar Refinement and Gap Removal

This plan describes how to dynamically adjust the calendar row count, update date colors for contrast, and remove the gap between the calendar and the notes section.

## Proposed Changes

### Layouts

#### [MODIFY] [activity_main.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/activity_main.xml)
- **`calendarGrid`**:
    - Change `layout_height` from `@dimen/_360sdp` to `wrap_content` or a height that will be set programmatically.
- **`remarkLabel`**:
    - Reduce `layout_marginTop` from `16dp` to `4dp` to move the notes section upward.

### Logic

#### [MODIFY] [MainActivity.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/java/com/example/mycalendar2026sar/MainActivity.java)
- **`updateCalendar()`**:
    - Implement logic to add exactly 35 days (5 rows) or 42 days (6 rows) depending on whether the current month extends into the 6th row.
    - Programmatically update `calendarGrid` height to match the number of rows (5 rows = 305dp, 6 rows = 366dp including borders).
- **`CalendarAdapter#getView()`**:
    - Set text color to `Color.WHITE` for dates in previous/next months.
    - Ensure background color for other month cells is pure black (`#000000`).
    - Ensure current month dates use the designated color (`color_note_text`).
    - Confirm the "Today" highlight is correctly centered using `Gravity.CENTER`.

## Verification Plan

### Automated Tests
- Build the project to ensure no resource or compilation errors.

### Manual Verification
1. Open the Notification Menu for September 2026.
2. Verify that the 6th row (starting with 5, 6, 7...) is gone.
3. Verify that dates like 31 (Aug) and 1, 2, 3, 4 (Oct) are white and clearly visible on a black background.
4. Verify the "Note for..." section sits directly underneath the calendar with a minimal gap.
5. Verify September 5 (Today) has a green outline with the number perfectly centered.
6. Check a month that *requires* 6 rows (e.g. August 2026 if week starts Monday) to ensure no days are cut off.
