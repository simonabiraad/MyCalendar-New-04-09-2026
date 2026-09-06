package com.example.mycalendar2026sar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import java.io.File;
import java.io.IOException;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Date;

public class NotificationDetailsActivity extends AppCompatActivity {

    private NotificationEvent currentEvent;
    private long eventId = -1;
    private String mode = "view"; // "view", "edit", "add"
    private TransactionDbHelper dbHelper;

    private TextView topTitle, detailTitle, detailStatus, detailDate, detailTime, detailPriority, detailRepeat, detailReminder, detailLocation, detailNotes;
    private ImageButton backButton, editTopButton, moreOptionsButton, playVoiceBtn, deleteVoiceBtn;
    private Button completeAction, snoozeAction, editAction, deleteAction;
    private android.widget.LinearLayout historyContainer, voiceNoteContainer;
    private RecyclerView attachmentsRecyclerView;
    private AttachmentAdapter attachmentAdapter;
    private List<String> attachmentList = new ArrayList<>();
    
    private MediaRecorder recorder;
    private MediaPlayer player;
    private String voicePath = "";
    private boolean isRecording = false;

    private final androidx.activity.result.ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    android.net.Uri uri = result.getData().getData();
                    if (uri != null) {
                        addAttachment(uri.toString());
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_details);

        dbHelper = TransactionDbHelper.getInstance(this);
        eventId = getIntent().getLongExtra("eventId", -1);
        mode = getIntent().getStringExtra("mode");
        if (mode == null) mode = "view";

        initViews();
        setupClickListeners();

        if ("add".equals(mode)) {
            String date = getIntent().getStringExtra("date");
            currentEvent = new NotificationEvent(-1, "", "", date, "", "", "Medium", "Pending", "None", "None", "", "[]", "", "[]");
            setupEditUI();
        } else {
            loadEvent();
        }
    }

    private void setupEditUI() {
        setContentView(R.layout.activity_notification_edit);
        
        TextView editDate = findViewById(R.id.editDate);
        TextView editStartTime = findViewById(R.id.editStartTime);
        TextView editEndTime = findViewById(R.id.editEndTime);
        android.widget.EditText editTitle = findViewById(R.id.editTitle);
        android.widget.EditText editNotesField = findViewById(R.id.editNotes);
        android.widget.EditText editLocationField = findViewById(R.id.editLocation);
        android.widget.Spinner prioritySpinner = findViewById(R.id.prioritySpinner);
        android.widget.Spinner repeatSpinner = findViewById(R.id.repeatSpinner);
        android.widget.Spinner reminderSpinner = findViewById(R.id.reminderSpinner);
        Button btnAddAttachment = findViewById(R.id.btnAddAttachment);
        Button btnRecordVoice = findViewById(R.id.btnRecordVoice);
        TextView btnCancel = findViewById(R.id.btnCancelEdit);
        TextView btnSave = findViewById(R.id.btnSaveEdit);
        TextView headerTitle = findViewById(R.id.editTitleHeader);
        ImageButton btnBack = findViewById(R.id.btnBackEdit);

        headerTitle.setText(currentEvent.getId() == -1 ? "New Event" : "Edit Event");

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (currentEvent.getId() == -1) {
                    finish();
                } else {
                    initViews();
                    setupClickListeners();
                    bindData();
                }
            });
        }

        btnRecordVoice.setOnClickListener(v -> {
            if (!isRecording) {
                startRecording();
                btnRecordVoice.setText("Stop Recording");
                isRecording = true;
            } else {
                stopRecording();
                btnRecordVoice.setText("Record Voice Note");
                isRecording = false;
            }
        });

        btnAddAttachment.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            filePickerLauncher.launch(intent);
        });

        // Pre-fill
        editTitle.setText(currentEvent.getTitle());
        editNotesField.setText(currentEvent.getNotes());
        editLocationField.setText(currentEvent.getLocation());
        editDate.setText(currentEvent.getDate());
        editStartTime.setText(currentEvent.getStartTime());
        editEndTime.setText(currentEvent.getEndTime());

        // Set spinner selections
        setSpinnerSelection(prioritySpinner, currentEvent.getPriority(), R.array.priority_options);
        setSpinnerSelection(repeatSpinner, currentEvent.getRepeat(), R.array.repeat_options);
        setSpinnerSelection(reminderSpinner, currentEvent.getReminder(), R.array.reminder_options);

        editDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (d, y, m, day) -> {
                String date = String.format(Locale.getDefault(), "%02d/%02d/%d", day, m + 1, y);
                editDate.setText(date);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        editStartTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (t, h, min) -> {
                editStartTime.setText(String.format(Locale.getDefault(), "%02d:%02d", h, min));
            }, 10, 0, false).show();
        });

        editEndTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (t, h, min) -> {
                editEndTime.setText(String.format(Locale.getDefault(), "%02d:%02d", h, min));
            }, 11, 0, false).show();
        });

        btnSave.setOnClickListener(v -> {
            currentEvent.setTitle(editTitle.getText().toString());
            currentEvent.setNotes(editNotesField.getText().toString());
            currentEvent.setLocation(editLocationField.getText().toString());
            currentEvent.setDate(editDate.getText().toString());
            currentEvent.setStartTime(editStartTime.getText().toString());
            currentEvent.setEndTime(editEndTime.getText().toString());
            currentEvent.setPriority(prioritySpinner.getSelectedItem().toString());
            currentEvent.setRepeat(repeatSpinner.getSelectedItem().toString());
            currentEvent.setReminder(reminderSpinner.getSelectedItem().toString());

            if (currentEvent.getId() == -1) {
                addHistoryLog("Created");
                long id = dbHelper.addNotification(currentEvent);
                currentEvent.setId(id);
                eventId = id;
            } else {
                addHistoryLog("Edited");
                dbHelper.updateNotification(currentEvent);
            }
            NotificationUtils.scheduleNotification(this, currentEvent);
            
            // Re-init main view
            initViews();
            setupClickListeners();
            bindData();
        });

        btnCancel.setOnClickListener(v -> {
            if (currentEvent.getId() == -1) {
                finish();
            } else {
                initViews();
                setupClickListeners();
                bindData();
            }
        });
    }

    private void setSpinnerSelection(android.widget.Spinner spinner, String value, int arrayRes) {
        String[] options = getResources().getStringArray(arrayRes);
        for (int i = 0; i < options.length; i++) {
            if (options[i].equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void initViews() {
        setContentView(R.layout.activity_notification_details);
        topTitle = findViewById(R.id.topTitle);
        detailTitle = findViewById(R.id.detailTitle);
        detailStatus = findViewById(R.id.detailStatus);
        detailDate = findViewById(R.id.detailDate);
        detailTime = findViewById(R.id.detailTime);
        detailPriority = findViewById(R.id.detailPriority);
        detailRepeat = findViewById(R.id.detailRepeat);
        detailReminder = findViewById(R.id.detailReminder);
        detailLocation = findViewById(R.id.detailLocation);
        detailNotes = findViewById(R.id.detailNotes);
        historyContainer = findViewById(R.id.historyContainer);
        voiceNoteContainer = findViewById(R.id.voiceNoteContainer);
        playVoiceBtn = findViewById(R.id.playVoiceBtn);
        deleteVoiceBtn = findViewById(R.id.deleteVoiceBtn);
        attachmentsRecyclerView = findViewById(R.id.attachmentsRecyclerView);
        attachmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        backButton = findViewById(R.id.backButton);
        editTopButton = findViewById(R.id.editTopButton);
        moreOptionsButton = findViewById(R.id.moreOptionsButton);

        completeAction = findViewById(R.id.completeAction);
        snoozeAction = findViewById(R.id.snoozeAction);
        editAction = findViewById(R.id.editAction);
        deleteAction = findViewById(R.id.deleteAction);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        editTopButton.setOnClickListener(v -> showEditDialog());
        editAction.setOnClickListener(v -> showEditDialog());

        completeAction.setOnClickListener(v -> updateStatus("Completed"));
        snoozeAction.setOnClickListener(v -> showSnoozeOptions());
        deleteAction.setOnClickListener(v -> showDeleteConfirmation());

        moreOptionsButton.setOnClickListener(v -> showMoreMenu());
    }

    private void loadEvent() {
        currentEvent = dbHelper.getNotificationById(eventId);
        if (currentEvent != null) {
            bindData();
        } else {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void bindData() {
        topTitle.setText(currentEvent.getTitle());
        detailTitle.setText(currentEvent.getTitle());
        detailStatus.setText(currentEvent.getStatus().toUpperCase());
        detailDate.setText(currentEvent.getDate());
        detailTime.setText(currentEvent.getStartTime() + " - " + currentEvent.getEndTime());
        detailPriority.setText("Priority: " + currentEvent.getPriority());
        detailRepeat.setText(currentEvent.getRepeat());
        detailReminder.setText(currentEvent.getReminder());
        detailLocation.setText(currentEvent.getLocation().isEmpty() ? "No location" : currentEvent.getLocation());
        detailNotes.setText(currentEvent.getNotes());

        if (currentEvent.getVoiceNotePath() != null && !currentEvent.getVoiceNotePath().isEmpty()) {
            voiceNoteContainer.setVisibility(View.VISIBLE);
            playVoiceBtn.setOnClickListener(v -> playVoice(currentEvent.getVoiceNotePath()));
            deleteVoiceBtn.setOnClickListener(v -> deleteVoice());
        } else {
            voiceNoteContainer.setVisibility(View.GONE);
        }

        loadHistory();
        loadAttachments();

        // Update status color
        if ("Completed".equalsIgnoreCase(currentEvent.getStatus())) {
            detailStatus.setTextColor(Color.GRAY);
        } else {
            detailStatus.setTextColor(Color.parseColor("#8BC34A"));
        }
    }

    private void loadAttachments() {
        attachmentList.clear();
        try {
            JSONArray array = new JSONArray(currentEvent.getAttachments());
            for (int i = 0; i < array.length(); i++) {
                attachmentList.add(array.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        attachmentAdapter = new AttachmentAdapter(attachmentList);
        attachmentsRecyclerView.setAdapter(attachmentAdapter);
    }

    private void addAttachment(String path) {
        attachmentList.add(path);
        updateAttachmentsInDb();
        loadAttachments();
        addHistoryLog("Attachment added");
    }

    private void updateAttachmentsInDb() {
        try {
            JSONArray array = new JSONArray(attachmentList);
            currentEvent.setAttachments(array.toString());
            dbHelper.updateNotification(currentEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadHistory() {
        historyContainer.removeAllViews();
        try {
            JSONArray history = new JSONArray(currentEvent.getHistory());
            for (int i = history.length() - 1; i >= 0; i--) {
                JSONObject log = history.getJSONObject(i);
                TextView logView = new TextView(this);
                logView.setText(log.getString("time") + ": " + log.getString("action"));
                logView.setTextColor(Color.LTGRAY);
                logView.setTextSize(14);
                logView.setPadding(0, 4, 0, 4);
                historyContainer.addView(logView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.ViewHolder> {
        private final List<String> items;

        AttachmentAdapter(List<String> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext());
            tv.setPadding(16, 8, 16, 8);
            tv.setBackgroundResource(R.drawable.task_input_border);
            tv.setTextColor(Color.WHITE);
            return new ViewHolder(tv);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String path = items.get(position);
            String name = path.substring(path.lastIndexOf("/") + 1);
            holder.textView.setText(name);
            holder.itemView.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(android.net.Uri.parse(path));
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(NotificationDetailsActivity.this, "Cannot open file", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() { return items.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView;
            }
        }
    }

    private void updateStatus(String status) {
        if (currentEvent == null) return;
        currentEvent.setStatus(status);
        addHistoryLog("Status changed to " + status);
        dbHelper.updateNotification(currentEvent);
        bindData();
        Toast.makeText(this, "Status updated to " + status, Toast.LENGTH_SHORT).show();
    }

    private void addHistoryLog(String action) {
        try {
            JSONArray history = new JSONArray(currentEvent.getHistory());
            JSONObject log = new JSONObject();
            log.put("action", action);
            log.put("time", new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()));
            history.put(log);
            currentEvent.setHistory(history.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playVoice(String path) {
        if (player != null) {
            player.release();
        }
        player = new MediaPlayer();
        try {
            player.setDataSource(path);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteVoice() {
        currentEvent.setVoiceNotePath("");
        dbHelper.updateNotification(currentEvent);
        bindData();
        addHistoryLog("Voice note deleted");
    }

    private void showEditDialog() {
        setupEditUI();
    }

    private void startRecording() {
        voicePath = getExternalCacheDir().getAbsolutePath() + "/voice_" + System.currentTimeMillis() + ".3gp";
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(voicePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
            currentEvent.setVoiceNotePath(voicePath);
        }
    }

    private void showSnoozeOptions() {
        String[] options = {"10 minutes", "30 minutes", "1 hour", "3 hours", "Tomorrow"};
        new AlertDialog.Builder(this)
                .setTitle("Snooze")
                .setItems(options, (dialog, which) -> {
                    Calendar cal = Calendar.getInstance();
                    if (which == 0) cal.add(Calendar.MINUTE, 10);
                    else if (which == 1) cal.add(Calendar.MINUTE, 30);
                    else if (which == 2) cal.add(Calendar.HOUR_OF_DAY, 1);
                    else if (which == 3) cal.add(Calendar.HOUR_OF_DAY, 3);
                    else if (which == 4) cal.add(Calendar.DAY_OF_YEAR, 1);

                    currentEvent.setStatus("Snoozed");
                    addHistoryLog("Snoozed for " + options[which]);
                    dbHelper.updateNotification(currentEvent);
                    
                    // Reschedule for snooze time
                    scheduleSnooze(cal);
                    
                    bindData();
                    Toast.makeText(this, "Snoozed for " + options[which], Toast.LENGTH_SHORT).show();
                }).show();
    }

    private void scheduleSnooze(Calendar time) {
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) getSystemService(android.content.Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("noteText", currentEvent.getTitle());
        intent.putExtra("eventId", currentEvent.getId());
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getBroadcast(this, (int) currentEvent.getId(), intent, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);
        }
    }

    private void showDeleteConfirmation() {
        if (!"None".equalsIgnoreCase(currentEvent.getRepeat()) && !"Does not repeat".equalsIgnoreCase(currentEvent.getRepeat())) {
            String[] options = {"Delete this occurrence", "Delete all recurring events"};
            new AlertDialog.Builder(this)
                    .setTitle("Recurring Notification")
                    .setItems(options, (dialog, which) -> {
                        NotificationUtils.cancelNotification(this, eventId);
                        dbHelper.deleteNotification(eventId);
                        finish();
                    }).show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Delete notification?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        NotificationUtils.cancelNotification(this, eventId);
                        dbHelper.deleteNotification(eventId);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    private void showMoreMenu() {
        android.widget.PopupMenu popup = new android.widget.PopupMenu(this, moreOptionsButton);
        popup.getMenu().add("Convert to Task");
        popup.setOnMenuItemClickListener(item -> {
            if ("Convert to Task".equals(item.getTitle())) {
                convertToTask();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void convertToTask() {
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra("fromNotification", true);
        intent.putExtra("title", currentEvent.getTitle());
        intent.putExtra("notes", currentEvent.getNotes());
        startActivity(intent);
        Toast.makeText(this, "Converted to Task", Toast.LENGTH_SHORT).show();
    }
}
