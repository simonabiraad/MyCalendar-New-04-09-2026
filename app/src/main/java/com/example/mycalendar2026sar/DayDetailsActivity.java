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

public class DayDetailsActivity extends AppCompatActivity {

    private String selectedDate;
    private TransactionDbHelper dbHelper;
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private List<NotificationEvent> eventList = new ArrayList<>();
    private TextView noEventsText, dayTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_details);

        selectedDate = getIntent().getStringExtra("selectedDate");
        dbHelper = TransactionDbHelper.getInstance(this);

        dayTitle = findViewById(R.id.dayTitle);
        dayTitle.setText(selectedDate);

        noEventsText = findViewById(R.id.noEventsText);
        recyclerView = findViewById(R.id.eventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.addEventButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationDetailsActivity.class);
            intent.putExtra("mode", "add");
            intent.putExtra("date", selectedDate);
            startActivity(intent);
        });

        loadEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    private void loadEvents() {
        eventList = dbHelper.getNotificationsByDate(selectedDate);
        if (eventList.isEmpty()) {
            noEventsText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noEventsText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new EventAdapter(eventList);
            recyclerView.setAdapter(adapter);
        }
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
            String titleText = event.getTitle();
            if (event.isAllDay()) {
                titleText += " (All Day)";
            }
            holder.title.setText(titleText);
            String time = event.getStartTime() + (event.getEndTime().isEmpty() ? "" : " - " + event.getEndTime());
            holder.time.setText(time.isEmpty() ? "No time set" : time);
            holder.status.setText(event.getStatus());

            // Event Color indicator logic
            String eventColorStr = event.getEventColor();
            if (holder.eventColorIndicator != null) {
                if (eventColorStr != null && !eventColorStr.isEmpty() && !"Default".equalsIgnoreCase(eventColorStr)) {
                    int indicatorColor = Color.parseColor("#34A853"); // Default green
                    if ("Red".equalsIgnoreCase(eventColorStr)) indicatorColor = Color.parseColor("#EA4335");
                    else if ("Blue".equalsIgnoreCase(eventColorStr)) indicatorColor = Color.parseColor("#4285F4");
                    else if ("Green".equalsIgnoreCase(eventColorStr)) indicatorColor = Color.parseColor("#34A853");
                    else if ("Yellow".equalsIgnoreCase(eventColorStr)) indicatorColor = Color.parseColor("#FBBC05");
                    else if ("Purple".equalsIgnoreCase(eventColorStr)) indicatorColor = Color.parseColor("#8E24AA");

                    holder.eventColorIndicator.setBackgroundTintList(android.content.res.ColorStateList.valueOf(indicatorColor));
                    holder.eventColorIndicator.setVisibility(View.VISIBLE);
                } else {
                    holder.eventColorIndicator.setVisibility(View.GONE);
                }
            }

            int priorityColor = Color.GREEN;
            if ("High".equalsIgnoreCase(event.getPriority())) priorityColor = Color.RED;
            else if ("Medium".equalsIgnoreCase(event.getPriority())) priorityColor = Color.YELLOW;
            holder.priorityIndicator.setBackgroundColor(priorityColor);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(DayDetailsActivity.this, NotificationDetailsActivity.class);
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
            TextView title, time, status;
            View priorityIndicator, eventColorIndicator;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.eventTitle);
                time = itemView.findViewById(R.id.eventTime);
                status = itemView.findViewById(R.id.eventStatus);
                priorityIndicator = itemView.findViewById(R.id.priorityIndicator);
                eventColorIndicator = itemView.findViewById(R.id.eventColorIndicator);
            }
        }
    }
}
