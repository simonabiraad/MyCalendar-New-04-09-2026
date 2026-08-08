# Modern Widget System: Today Notes, Sticky Notes, and Tasks

This plan introduces 3 new types of home screen widgets, each available in 3 sizes (Small, Medium, Large), providing a total of 9 widget options for the user.

## Proposed Widgets

### 1. Today Notes Widget
Displays notes saved in the personal calendar for the current day.
- **Small (2x2)**: Shows the current date and a count of notes.
- **Medium (4x2)**: Shows a scrollable list of today's notes.
- **Large (4x4)**: Shows a larger scrollable list of today's notes.

### 2. Sticky Notes Widget
Displays "All Notes" from the Secure Box (skipping password-protected categories).
- **Small (2x2)**: Shows a sticky-note icon and total count of public notes.
- **Medium (4x2)**: Shows a grid/list of public sticky notes.
- **Large (4x4)**: Shows a larger grid/list of public sticky notes.

### 3. Tasks Widget
Displays items from the Task list.
- **Small (2x2)**: Shows a task icon and count of pending tasks.
- **Medium (4x2)**: Shows a scrollable list of tasks with check status.
- **Large (4x4)**: Shows a larger scrollable list of tasks.

## Technical Implementation

### Providers
I will create 9 distinct `AppWidgetProvider` components in the manifest to allow the user to choose the specific type and size from the system widget picker.

- **Today**: `TodayWidgetSmall`, `TodayWidgetMedium`, `TodayWidgetLarge`
- **Sticky**: `StickyWidgetSmall`, `StickyWidgetMedium`, `StickyWidgetLarge`
- **Task**: `TaskWidgetSmall`, `TaskWidgetMedium`, `TaskWidgetLarge`

### Layouts
- New XML layouts for each size to ensure they look great on all screen widths.
- List-based widgets (Medium/Large) will use a unified `WidgetListService` to fetch data asynchronously.

### Data Security
- Sticky note widgets will **exclude** notes from password-protected categories to ensure privacy on the home screen.

## Proposed Changes

### [Resources - Layouts]
- `widget_today_small.xml`, `widget_today_list.xml`
- `widget_sticky_small.xml`, `widget_sticky_list.xml`
- `widget_task_small.xml`, `widget_task_list.xml`

### [Java - Logic]
- Create `BaseWidgetProvider` to share common update logic.
- Create 9 specific provider classes.
- Create `WidgetListService` and `WidgetRemoteViewsFactory` to handle list data for all types.

### [Manifest]
- Register all 9 widget providers with their respective `xml` info files.

## Verification Plan

### Manual Verification
1.  Open the home screen widget picker.
2.  Verify there are 3 main categories under "SAR Calendar": Today Notes, Sticky Notes, and Tasks.
3.  Verify each category offers Small, Medium, and Large options.
4.  Add one of each size and verify:
    -   **Today**: Shows only today's notes.
    -   **Sticky**: Shows all notes except protected ones.
    -   **Tasks**: Shows the task list correctly.
5.  Check off a task in the app and verify the widget updates.
