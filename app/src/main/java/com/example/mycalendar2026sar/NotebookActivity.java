package com.example.mycalendar2026sar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Free-form notes scoped to Expenses (e.g. "ask landlord about deposit",
 * "switch electricity provider next month"). Stored as a JSON array in the
 * ExpensesPrefs-style SharedPreferences, following the same persistence
 * pattern BalanceManager uses for accounts - no separate DB table needed
 * for something this simple.
 */
public class NotebookActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ExpensesNotebook";
    private static final String KEY_NOTES = "Notes";

    private RecyclerView recyclerView;
    private TextView emptyText;
    private NoteAdapter adapter;
    private List<NotebookNote> notes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebook);

        recyclerView = findViewById(R.id.notebookRecyclerView);
        emptyText = findViewById(R.id.notebookEmptyText);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.notebookBackButton).setOnClickListener(v -> finish());
        findViewById(R.id.notebookAddButton).setOnClickListener(v -> showNoteDialog(null));

        loadNotes();
    }

    private void loadNotes() {
        notes = readNotesFromPrefs();
        // Newest first
        Collections.sort(notes, (a, b) -> Long.compare(b.timestamp, a.timestamp));
        adapter = new NoteAdapter(notes);
        recyclerView.setAdapter(adapter);
        boolean empty = notes.isEmpty();
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        emptyText.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    private List<NotebookNote> readNotesFromPrefs() {
        List<NotebookNote> list = new ArrayList<>();
        try {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String json = prefs.getString(KEY_NOTES, null);
            if (json != null) {
                JSONArray array = new JSONArray(json);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    list.add(new NotebookNote(
                            obj.getLong("id"),
                            obj.getString("title"),
                            obj.optString("body", ""),
                            obj.getLong("timestamp")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void saveNotesToPrefs() {
        try {
            JSONArray array = new JSONArray();
            for (NotebookNote note : notes) {
                JSONObject obj = new JSONObject();
                obj.put("id", note.id);
                obj.put("title", note.title);
                obj.put("body", note.body);
                obj.put("timestamp", note.timestamp);
                array.put(obj);
            }
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                    .edit()
                    .putString(KEY_NOTES, array.toString())
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNoteDialog(NotebookNote existing) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_notebook_note, null);
        EditText titleInput = dialogView.findViewById(R.id.noteTitleInput);
        EditText bodyInput = dialogView.findViewById(R.id.noteBodyInput);

        if (existing != null) {
            titleInput.setText(existing.title);
            bodyInput.setText(existing.body);
        }

        new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                .setTitle(existing == null ? "Add Note" : "Edit Note")
                .setView(dialogView)
                .setPositiveButton(existing == null ? "Add" : "Save", (d, w) -> {
                    String title = titleInput.getText().toString().trim();
                    String body = bodyInput.getText().toString().trim();
                    if (TextUtils.isEmpty(title)) {
                        Toast.makeText(this, "Title can't be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (existing == null) {
                        notes.add(new NotebookNote(System.currentTimeMillis(), title, body, System.currentTimeMillis()));
                    } else {
                        existing.title = title;
                        existing.body = body;
                        existing.timestamp = System.currentTimeMillis();
                    }
                    saveNotesToPrefs();
                    loadNotes();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDelete(NotebookNote note) {
        new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                .setTitle("Delete Note")
                .setMessage("Delete \"" + note.title + "\"?")
                .setPositiveButton("Delete", (d, w) -> {
                    notes.remove(note);
                    saveNotesToPrefs();
                    loadNotes();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private static class NotebookNote {
        long id;
        String title;
        String body;
        long timestamp;

        NotebookNote(long id, String title, String body, long timestamp) {
            this.id = id;
            this.title = title;
            this.body = body;
            this.timestamp = timestamp;
        }
    }

    private class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
        private final List<NotebookNote> items;

        NoteAdapter(List<NotebookNote> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notebook_note, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            NotebookNote note = items.get(position);
            holder.title.setText(note.title);
            holder.body.setText(note.body);
            holder.date.setText(DateFormat.format("dd MMM yyyy, hh:mm a", note.timestamp));

            holder.itemView.setOnClickListener(v -> showNoteDialog(note));
            holder.deleteButton.setOnClickListener(v -> confirmDelete(note));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView title, body, date;
            View deleteButton;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.noteRowTitle);
                body = itemView.findViewById(R.id.noteRowBody);
                date = itemView.findViewById(R.id.noteRowDate);
                deleteButton = itemView.findViewById(R.id.noteRowDeleteButton);
            }
        }
    }
}
