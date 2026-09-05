package com.example.mycalendar2026sar;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String noteText = intent.getStringExtra("noteText");
        long eventId = intent.getLongExtra("eventId", -1);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "calendar_reminder_channel";

        NotificationChannel channel = new NotificationChannel(channelId, "Calendar Reminders", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Reminders for your calendar notes");
        channel.enableVibration(true);
        channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null);
        notificationManager.createNotificationChannel(channel);

        Intent notifIntent = new Intent(context, NotificationDetailsActivity.class);
        notifIntent.putExtra("eventId", eventId);
        notifIntent.putExtra("mode", "view");
        notifIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), notifIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setColor(Color.GREEN)
                .setContentTitle("SAR Calendar Reminder")
                .setContentText(noteText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());

        // Handle recurrence
        if (eventId != -1) {
            NotificationEvent event = TransactionDbHelper.getInstance(context).getNotificationById(eventId);
            if (event != null) {
                NotificationUtils.scheduleNextOccurrence(context, event);
            }
        }
    }
}
