package com.example.mycalendar2026sar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NotificationUtils {

    public static void scheduleNotification(Context context, NotificationEvent event) {
        if (event == null || event.getDate() == null || event.getStartTime() == null) return;

        Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            java.util.Date date = sdf.parse(event.getDate() + " " + event.getStartTime());
            if (date != null) calendar.setTime(date);
            else return;
        } catch (Exception e) {
            return;
        }

        // Adjust for reminder time
        applyReminderOffset(calendar, event.getReminder());

        if (calendar.before(Calendar.getInstance())) return;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("noteText", event.getTitle());
        intent.putExtra("eventId", event.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) event.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private static void applyReminderOffset(Calendar calendar, String reminder) {
        if ("5 minutes before".equals(reminder)) calendar.add(Calendar.MINUTE, -5);
        else if ("10 minutes before".equals(reminder)) calendar.add(Calendar.MINUTE, -10);
        else if ("15 minutes before".equals(reminder)) calendar.add(Calendar.MINUTE, -15);
        else if ("30 minutes before".equals(reminder)) calendar.add(Calendar.MINUTE, -30);
        else if ("1 hour before".equals(reminder)) calendar.add(Calendar.HOUR_OF_DAY, -1);
        else if ("1 day before".equals(reminder)) calendar.add(Calendar.DAY_OF_YEAR, -1);
    }

    public static void cancelNotification(Context context, long eventId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) eventId, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public static void scheduleNextOccurrence(Context context, NotificationEvent event) {
        if (event == null || "None".equalsIgnoreCase(event.getRepeat()) || "Does not repeat".equalsIgnoreCase(event.getRepeat())) return;

        Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            java.util.Date date = sdf.parse(event.getDate() + " " + event.getStartTime());
            if (date != null) calendar.setTime(date);
            else return;
        } catch (Exception e) {
            return;
        }

        String repeat = event.getRepeat();
        if ("Every day".equalsIgnoreCase(repeat)) calendar.add(Calendar.DAY_OF_YEAR, 1);
        else if ("Every week".equalsIgnoreCase(repeat)) calendar.add(Calendar.WEEK_OF_YEAR, 1);
        else if ("Every month".equalsIgnoreCase(repeat)) calendar.add(Calendar.MONTH, 1);
        else if ("Every year".equalsIgnoreCase(repeat)) calendar.add(Calendar.YEAR, 1);
        else return; 

        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        event.setDate(sdfDate.format(calendar.getTime()));
        
        TransactionDbHelper.getInstance(context).updateNotification(event);
        scheduleNotification(context, event);
    }
}
