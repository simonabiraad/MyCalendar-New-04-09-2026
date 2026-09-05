package com.example.mycalendar2026sar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;

public class EventsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<NotificationEvent> eventList = new ArrayList<>();
    private TransactionDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        dbHelper = TransactionDbHelper.getInstance(this);
        recyclerView = findViewById(R.id.allEventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadAllEvents();

        findViewById(R.id.addEventButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationDetailsActivity.class);
            intent.putExtra("mode", "add");
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            intent.putExtra("date", sdf.format(new java.util.Date())); 
            startActivity(intent);
        });

        findViewById(R.id.eventBackButton).setOnClickListener(v -> finish());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showLeaveConfirmation();
            }
        });
    }

    private void showLeaveConfirmation() {
        new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                .setTitle("Leave Page")
                .setMessage("Are you sure you want to leave this page?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllEvents();
    }

    private void loadAllEvents() {
        eventList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Get all events from the notifications table
        Cursor c = db.query(TransactionDbHelper.TABLE_NOTIFICATIONS, null, TransactionDbHelper.COL_NOTIF_DELETED + "=0", null, null, null, TransactionDbHelper.COL_NOTIF_DATE + " DESC");
        if (c != null) {
            while (c.moveToNext()) {
                eventList.add(readNotification(c));
            }
            c.close();
        }
        adapter = new EventAdapter(eventList);
        recyclerView.setAdapter(adapter);
    }

    private NotificationEvent readNotification(Cursor c) {
        return new NotificationEvent(
                c.getLong(c.getColumnIndexOrThrow(TransactionDbHelper.COL_NOTIF_ID)),
                c.getString(c.getColumnIndexOrThrow(TransactionDbHelper.COL_NOTIF_TITLE)),
                c.getString(c.getColumnIndexOrThrow(TransactionDbHelper.COL_NOTIF_NOTES)),
                c.getString(c.getColumnIndexOrThrow(TransactionDbHelper.COL_NOTIF_DATE)),
                c.getString(c.getColumnIndexOrThrow(TransactionDbHelper.COL_NOTIF_START_TIME)),
                c.getString(c.getColumnIndexOrThrow(TransactionDbHelper.COL_NOTIF_END_TIME)),
                c.getString(c.getColumnIndexOrThrow(TransactionDbHelper.COL_NOTIF_PRIORITY)),
                c.getString(c.getColumnIndexOrThrow(TransactionDbHelper.COL_NOTIF_STATUS)),
                c.getString(c.getColumnIndexOrThrow(TransactionDbHelper.COL_NOTIF_REPEAT)),
                c.getString(c.getColumnIndexOrThrow(TransactionDbHelper.COL_NOTIF_REMINDER)),
                c.getString(c.getColumnIndexOrThrow(TransactionDbHelper.COL_NOTIF_LOCATION)),
                c.getString(c.getColumnIndexOrThrow(TransactionDbHelper.COL_NOTIF_ATTACHMENTS)),
                c.getString(c.getColumnIndexOrThrow(TransactionDbHelper.COL_NOTIF_VOICE_PATH)),
                c.getString(c.getColumnIndexOrThrow(TransactionDbHelper.COL_NOTIF_HISTORY))
        );
    }

    private class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
        private List<NotificationEvent> items;

        EventAdapter(List<NotificationEvent> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_event, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            NotificationEvent event = items.get(position);
            holder.title.setText(event.getTitle());
            holder.date.setVisibility(View.VISIBLE);
            holder.date.setText(event.getDate());
            holder.time.setText(event.getStartTime() + " - " + event.getEndTime());
            holder.status.setText(event.getStatus());

            int priorityColor = Color.GREEN;
            if ("High".equalsIgnoreCase(event.getPriority())) priorityColor = Color.RED;
            else if ("Medium".equalsIgnoreCase(event.getPriority())) priorityColor = Color.YELLOW;
            holder.priorityIndicator.setBackgroundColor(priorityColor);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(EventsActivity.this, NotificationDetailsActivity.class);
                intent.putExtra("mode", "view");
                intent.putExtra("eventId", event.getId());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView title, date, time, status;
            View priorityIndicator;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.eventTitle);
                date = itemView.findViewById(R.id.eventDate); // Note: I should add this to item layout if it's for all dates
                time = itemView.findViewById(R.id.eventTime);
                status = itemView.findViewById(R.id.eventStatus);
                priorityIndicator = itemView.findViewById(R.id.priorityIndicator);
            }
        }
    }
}
