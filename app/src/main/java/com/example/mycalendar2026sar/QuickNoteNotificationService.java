package com.example.mycalendar2026sar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class QuickNoteNotificationService extends Service {

    public static final String ACTION_VOICE = "com.example.mycalendar2026sar.ACTION_VOICE";
    public static final String ACTION_NOTE = "com.example.mycalendar2026sar.ACTION_NOTE";
    public static final String ACTION_SECURE_BOX = "com.example.mycalendar2026sar.ACTION_SECURE_BOX";
    public static final String ACTION_TASK = "com.example.mycalendar2026sar.ACTION_TASK";
    public static final String ACTION_EXPENSES = "com.example.mycalendar2026sar.ACTION_EXPENSES";
    public static final String ACTION_SETTINGS = "com.example.mycalendar2026sar.ACTION_SETTINGS";
    public static final String ACTION_THEMES = "com.example.mycalendar2026sar.ACTION_THEMES";

    private static final String CHANNEL_ID = "quick_note_channel";
    private static final int NOTIF_ID = 1001;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        startForeground(NOTIF_ID, createNotification());
        return START_STICKY;
    }

    private Notification createNotification() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_widget);

        // Intent for voice
        Intent voiceIntent = new Intent(this, MainActivity.class);
        voiceIntent.setAction(ACTION_VOICE);
        voiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent voicePendingIntent = PendingIntent.getActivity(this, 0, voiceIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.notif_voice_btn, voicePendingIntent);

        // Intent for note
        Intent noteIntent = new Intent(this, MainActivity.class);
        noteIntent.setAction(ACTION_NOTE);
        noteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent notePendingIntent = PendingIntent.getActivity(this, 1, noteIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.notif_note_btn, notePendingIntent);

        // Intent for Secure Box
        Intent sbIntent = new Intent(this, MainActivity.class);
        sbIntent.setAction(ACTION_SECURE_BOX);
        sbIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent sbPendingIntent = PendingIntent.getActivity(this, 2, sbIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.notif_sb_btn, sbPendingIntent);

        // Intent for Task
        Intent taskIntent = new Intent(this, MainActivity.class);
        taskIntent.setAction(ACTION_TASK);
        taskIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent taskPendingIntent = PendingIntent.getActivity(this, 5, taskIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.notif_task_btn, taskPendingIntent);

        // Intent for Expenses
        Intent expIntent = new Intent(this, MainActivity.class);
        expIntent.setAction(ACTION_EXPENSES);
        expIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent expPendingIntent = PendingIntent.getActivity(this, 3, expIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.notif_expenses_btn, expPendingIntent);

        // Intent for Settings
        Intent settingsIntent = new Intent(this, MainActivity.class);
        settingsIntent.setAction(ACTION_SETTINGS);
        settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent settingsPendingIntent = PendingIntent.getActivity(this, 4, settingsIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.notif_settings_btn, settingsPendingIntent);

        // Intent for Themes
        Intent themesIntent = new Intent(this, MainActivity.class);
        themesIntent.setAction(ACTION_THEMES);
        themesIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent themesPendingIntent = PendingIntent.getActivity(this, 6, themesIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.notif_themes_btn, themesPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.notif_themes_text, themesPendingIntent);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_edit)
                .setCustomContentView(remoteViews)
                .setCustomBigContentView(remoteViews)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Quick Note Bar",
                NotificationManager.IMPORTANCE_LOW
        );
        channel.setDescription("Shows a persistent bar for quick note taking");
        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
