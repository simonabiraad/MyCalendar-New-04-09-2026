package com.example.mycalendar2026sar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;

public class EventsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<DateGroup> groupedEvents = new ArrayList<>();
    private TransactionDbHelper dbHelper;

    public static class DateGroup {
        String date;
        List<NotificationEvent> events;

        DateGroup(String date, List<NotificationEvent> events) {
            this.date = date;
            this.events = events;
        }
    }

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
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            intent.putExtra("date", sdf.format(new Date())); 
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
        groupedEvents.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        List<NotificationEvent> allEvents = new ArrayList<>();
        Cursor c = db.query(TransactionDbHelper.TABLE_NOTIFICATIONS, null, TransactionDbHelper.COL_NOTIF_DELETED + "=0", null, null, null, TransactionDbHelper.COL_NOTIF_DATE + " DESC, " + TransactionDbHelper.COL_NOTIF_START_TIME + " ASC");
        if (c != null) {
            while (c.moveToNext()) {
                allEvents.add(readNotification(c));
            }
            c.close();
        }

        // Grouping logic
        Map<String, List<NotificationEvent>> map = new LinkedHashMap<>();
        for (NotificationEvent event : allEvents) {
            String date = event.getDate();
            map.computeIfAbsent(date, k -> new ArrayList<>()).add(event);
        }

        for (Map.Entry<String, List<NotificationEvent>> entry : map.entrySet()) {
            groupedEvents.add(new DateGroup(entry.getKey(), entry.getValue()));
        }

        adapter = new EventAdapter(groupedEvents);
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
        private List<DateGroup> items;

        EventAdapter(List<DateGroup> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_group, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DateGroup group = items.get(position);
            
            // Format Date
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = sdf.parse(group.date);
                if (date != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    
                    SimpleDateFormat dayPillSdf = new SimpleDateFormat("EEE", Locale.getDefault());
                    holder.dayOfWeekPill.setText(dayPillSdf.format(date));
                    
                    // Set pill color cyclically
                    int[] pillColors = {0xFF4CAF50, 0xFF2196F3, 0xFFFF9800, 0xFFE91E63};
                    if (holder.dayOfWeekPill.getBackground() != null) {
                        holder.dayOfWeekPill.getBackground().setTint(pillColors[position % pillColors.length]);
                    }

                    holder.dayNumber.setText(String.format(Locale.getDefault(), "%02d", cal.get(Calendar.DAY_OF_MONTH)));
                    
                    SimpleDateFormat monthYearSdf = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());
                    holder.monthYear.setText(monthYearSdf.format(date));
                }
            } catch (Exception e) {
                holder.dayNumber.setText("?");
                holder.monthYear.setText(group.date);
            }

            // Bind Events
            holder.eventsContainer.removeAllViews();
            
            for (int i = 0; i < group.events.size(); i++) {
                NotificationEvent event = group.events.get(i);
                View detailView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_event_details, holder.eventsContainer, false);
                
                TextView title = detailView.findViewById(R.id.detailTitle);
                TextView location = detailView.findViewById(R.id.detailLocation);
                TextView time = detailView.findViewById(R.id.detailTime);
                View locationLayout = detailView.findViewById(R.id.locationLayout);
                View priorityLine = detailView.findViewById(R.id.priorityLine);

                title.setText(event.getTitle());
                if (event.getLocation() != null && !event.getLocation().isEmpty()) {
                    location.setText(event.getLocation());
                    locationLayout.setVisibility(View.VISIBLE);
                } else {
                    locationLayout.setVisibility(View.GONE);
                }
                time.setText(event.getStartTime());
                
                // Priority Logic for line color
                int priorityColor = Color.GREEN;
                if ("High".equalsIgnoreCase(event.getPriority())) priorityColor = Color.RED;
                else if ("Medium".equalsIgnoreCase(event.getPriority())) priorityColor = Color.YELLOW;
                
                if (priorityLine != null) {
                    priorityLine.setBackgroundColor(priorityColor);
                }

                detailView.setOnClickListener(v -> {
                    Intent intent = new Intent(EventsActivity.this, NotificationDetailsActivity.class);
                    intent.putExtra("mode", "view");
                    intent.putExtra("eventId", event.getId());
                    startActivity(intent);
                });

                holder.eventsContainer.addView(detailView);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView dayOfWeekPill, dayNumber, monthYear;
            LinearLayout eventsContainer;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                dayOfWeekPill = itemView.findViewById(R.id.dayOfWeekPill);
                dayNumber = itemView.findViewById(R.id.dayNumber);
                monthYear = itemView.findViewById(R.id.monthYear);
                eventsContainer = itemView.findViewById(R.id.eventsContainer);
            }
        }
    }
}

