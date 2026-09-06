# Implementation Plan - Grouped Event List Design

This plan describes how to redesign the `EventsActivity` list to match the visual style of the target image, grouping events by date and using a modern, clean layout.

## Proposed Changes

### Resources

#### [NEW] [ic_location.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/drawable/ic_location.xml)
- Location icon for event details.

#### [NEW] [ic_clock.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/drawable/ic_clock.xml)
- Clock icon for event time.

#### [NEW] [weekday_pill_bg.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/drawable/weekday_pill_bg.xml)
- Rounded background for the weekday text.

### Layouts

#### [NEW] [item_event_group.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/item_event_group.xml)
- Root: `LinearLayout` (horizontal).
- **Left Column** (Date Info):
    - `TextView` (Weekday pill).
    - `TextView` (Large Day Number).
    - `TextView` (Month, Year).
- **Right Column**:
    - `LinearLayout` (vertical) `id="eventsList"` to hold multiple event details.
- `View` (Horizontal divider at the bottom).

#### [NEW] [item_event_detail.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/item_event_detail.xml)
- Root: `LinearLayout` (horizontal).
- **Priority Line**: `View` (1.5dp width, colored by priority).
- **Details Area**: `LinearLayout` (vertical).
    - `TextView` (Title).
    - `LinearLayout` (horizontal) for Location (Icon + Text).
    - `LinearLayout` (horizontal) for Time (Icon + Text).

### Logic

#### [MODIFY] [EventsActivity.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/java/com/example/mycalendar2026sar/EventsActivity.java)
- Update `loadAllEvents()`:
    - Group the `eventList` by date into a `List<DayGroup>` where `DayGroup` contains the date string and a list of `NotificationEvent`s.
- Update `EventAdapter`:
    - Use `item_event_group.xml` for each item.
    - Inside `onBindViewHolder`, parse the date to extract weekday, day, month, and year.
    - Dynamically inflate and add `item_event_detail.xml` views to the `eventsList` container for each event in that day group.
    - Implement priority coloring: High (Red), Medium (Yellow), Low (Green).

## Verification Plan

### Automated Tests
- Build the project to ensure no layout or compilation errors.

### Manual Verification
1. Open the **Events** page.
2. Verify that events are grouped by date.
3. Confirm the date column on the left matches the target design (Weekday pill, Large Number, Month/Year).
4. Confirm the vertical priority lines are correctly colored.
5. Verify that multiple events on the same day appear underneath each other in the same date group.
6. Verify that tapping an event still opens the details view.
