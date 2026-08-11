package com.example.mycalendar2026sar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TaskActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "TaskPrefs";
    private static final String KEY_TASKS = "tasks";

    private EditText taskInput;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<TaskItem> taskList = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    private final ActivityResultLauncher<Intent> voiceRecognitionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ArrayList<String> matches = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches != null && !matches.isEmpty()) {
                        String spokenText = matches.get(0);
                        String existingText = taskInput.getText().toString();
                        taskInput.setText(existingText.isEmpty() ? spokenText : existingText + " " + spokenText);
                        taskInput.setSelection(taskInput.getText().length());
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(TaskActivity.this, R.style.CustomAlertDialogTheme)
                        .setTitle("Leave Page")
                        .setMessage("Are you sure you want to leave this page?")
                        .setPositiveButton("Yes", (dialog, which) -> finish())
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        taskInput = findViewById(R.id.taskInput);
        recyclerView = findViewById(R.id.taskRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(layoutManager);

        findViewById(R.id.taskBackButton).setOnClickListener(v -> finish());
        findViewById(R.id.voiceTaskButton).setOnClickListener(v -> startVoiceRecognition());
        findViewById(R.id.addTaskButton).setOnClickListener(v -> addTask());

        findViewById(R.id.taskMenuButton).setOnClickListener(this::showTaskMenu);

        loadTasks();
        adapter = new TaskAdapter(taskList);
        recyclerView.setAdapter(adapter);

        setupDragAndDrop();
    }

    private void showTaskMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenu().add(0, 1, 0, "Share").setIcon(R.drawable.ic_menu_share_color);
        popup.getMenu().add(0, 2, 0, "Clear Completed").setIcon(R.drawable.ic_menu_trash_color);

        try {
            java.lang.reflect.Field field = popup.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            Object menuHelper = field.get(popup);
            java.lang.reflect.Method setForceIcons = menuHelper.getClass().getDeclaredMethod("setForceShowIcon", boolean.class);
            setForceIcons.invoke(menuHelper, true);
        } catch (Exception ignored) {}

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) shareTasks();
            else if (item.getItemId() == 2) showClearCompletedConfirm();
            return true;
        });
        popup.show();
    }

    private void shareTasks() {
        if (taskList.isEmpty()) {
            Toast.makeText(this, "No tasks to share", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder sb = new StringBuilder("SAR Tasks List:\n");
        for (int i = 0; i < taskList.size(); i++) {
            TaskItem item = taskList.get(i);
            sb.append(i + 1).append(". ").append(item.completed ? "[Done] " : "").append(item.text).append("\n");
        }
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share Tasks via"));
    }

    private void showClearCompletedConfirm() {
        boolean hasCompleted = false;
        for (TaskItem item : taskList) {
            if (item.completed) {
                hasCompleted = true;
                break;
            }
        }
        if (!hasCompleted) {
            Toast.makeText(this, "No completed tasks to clear", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                .setTitle("Clear Completed")
                .setMessage("Delete all tasks that are checked?")
                .setPositiveButton("Clear", (dialog, which) -> {
                    List<TaskItem> remaining = new ArrayList<>();
                    for (TaskItem item : taskList) {
                        if (!item.completed) remaining.add(item);
                    }
                    taskList.clear();
                    taskList.addAll(remaining);
                    saveTasks();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Completed tasks cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupDragAndDrop() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                if (fromPosition != RecyclerView.NO_POSITION && toPosition != RecyclerView.NO_POSITION) {
                    Collections.swap(taskList, fromPosition, toPosition);
                    adapter.notifyItemMoved(fromPosition, toPosition);
                    saveTasks();
                    return true;
                }
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your task...");
        try {
            voiceRecognitionLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Voice recognition not supported", Toast.LENGTH_SHORT).show();
        }
    }

    private void addTask() {
        String text = taskInput.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show();
            return;
        }

        TaskItem newItem = new TaskItem(text, false);
        taskList.add(newItem);
        saveTasks();
        adapter.notifyItemInserted(taskList.size() - 1);
        taskInput.setText("");
    }

    private void loadTasks() {
        taskList.clear();
        String json = sharedPreferences.getString(KEY_TASKS, null);
        if (json != null) {
            try {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    taskList.add(new TaskItem(
                            obj.getString("text"),
                            obj.getBoolean("completed")
                    ));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveTasks() {
        try {
            JSONArray array = new JSONArray();
            for (TaskItem item : taskList) {
                JSONObject obj = new JSONObject();
                obj.put("text", item.text);
                obj.put("completed", item.completed);
                array.put(obj);
            }
            sharedPreferences.edit().putString(KEY_TASKS, array.toString()).apply();
            WidgetUtils.updateAllWidgets(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class TaskItem {
        String text;
        boolean completed;

        TaskItem(String text, boolean completed) {
            this.text = text;
            this.completed = completed;
        }
    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
        private List<TaskItem> items;

        TaskAdapter(List<TaskItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TaskItem item = items.get(position);
            holder.taskText.setText(item.text);
            holder.checkBox.setOnCheckedChangeListener(null); // Clear listener before setting checked state
            holder.checkBox.setChecked(item.completed);
            
            updateStrikethrough(holder.taskText, item.completed);

            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.completed = isChecked;
                updateStrikethrough(holder.taskText, isChecked);
                saveTasks();
            });
        }

        private void updateStrikethrough(TextView textView, boolean completed) {
            if (completed) {
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                textView.setTextColor(0xFFAAAAAA); // Muted color for completed tasks
            } else {
                textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                textView.setTextColor(0xFFFFFFFF); // White for active tasks
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            CheckBox checkBox;
            TextView taskText;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                checkBox = itemView.findViewById(R.id.taskCheckBox);
                taskText = itemView.findViewById(R.id.taskText);
            }
        }
    }
}
