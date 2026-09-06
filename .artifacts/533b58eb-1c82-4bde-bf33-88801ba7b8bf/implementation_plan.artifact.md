# Redesign Calendar Events Layout

This plan outlines the steps to redesign the event list in `EventsActivity` to match the grouped date-based layout shown in the provided design.

## Proposed Changes

### [Component Name]

#### [MODIFY] [EventsActivity.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/java/com/example/mycalendar2026sar/EventsActivity.java)
- Define a `DateGroup` inner class to hold a date and its associated events.
- Update `loadAllEvents` to group `NotificationEvent` objects by date.
- Modify `EventAdapter` to use `item_event_group.xml`.
- In `onBindViewHolder`, dynamically inflate and add `item_event_details.xml` for each event in the `DateGroup`.
- Apply date formatting to show Day of Week, Day Number, and Month/Year.
- Map priorities to vertical line colors.

#### [NEW] [item_event_group.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/item_event_group.xml)
- Layout for a single date block containing one or more events.
- Includes the left-side date section (pill, large number, month/year) and a vertical separator.

#### [NEW] [item_event_details.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-04-09-2026/app/src/main/res/layout/item_event_details.xml)
- Layout for individual event details (title, location with icon, time with icon).

## Verification Plan

### Automated Tests
- Build the project to ensure no compilation errors.

### Manual Verification
- Deploy the app to a device or emulator.
- Navigate to the "Events" page.
- Verify that events are grouped by date.
- Verify that multiple events on the same day appear correctly under the same date section.
- Compare the UI with the target image (Image 2) to ensure alignment, colors, and icons are correct.
