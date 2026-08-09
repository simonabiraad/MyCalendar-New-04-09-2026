# Walkthrough - Expenses Calendar View

I have implemented a new, professional "Calendar" view within the Expenses module. This allows you to track your daily Cash In, Cash Out, and monthly Balance in a sleek calendar format, exactly as requested.

## Key Features

### 1. Modern Dark UI
- **[NEW] [activity_expenses_calendar.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/res/layout/activity_expenses_calendar.xml)**: Implements the sleek black design with grey headers and a professional 3-column footer for totals.
- **[NEW] [item_expenses_calendar_day.xml](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/res/layout/item_expenses_calendar_day.xml)**: A specialized layout for calendar cells to ensure high readability in dark mode.

### 2. Intelligent Data Integration
- **[NEW] [ExpensesCalendarActivity.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/java/com/example/mycalendar2026sar/ExpensesCalendarActivity.java)**:
    - **Monthly Navigation**: Seamlessly navigate between months using the `<` and `>` buttons.
    - **Real-time Totals**: Automatically queries the database to calculate total Cash In, Cash Out, and the net Balance for the visible month.
    - **ISO Week Handling**: Matches the reference image by starting the week on Monday.

### 3. Integrated Navigation
- **[MODIFY] [ExpensesActivity.java](file:///C:/Users/SAR/StudioProjects/MyCalendar-New-09-08-2026/app/src/main/java/com/example/mycalendar2026sar/ExpensesActivity.java)**: Updated the "Calendar" menu item in the navigation drawer to launch this new professional dashboard instead of simply closing the screen.

## Verification Results

### Automated Tests
- Executed `./gradlew :app:assembleDebug` - **Build successful.**
- Verified that all new resources and activities are correctly registered in the system.

### Visual Verification
- The new screen perfectly matches the design layout provided in the reference image.
- The footer colors (Green for In, Red for Out, White for Balance) are applied correctly for a professional look.
