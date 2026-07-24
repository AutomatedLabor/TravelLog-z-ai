package com.example.travellog.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.travellog.R;
import com.example.travellog.data.db.entity.PlaceNote;
import com.example.travellog.ui.viewmodel.PlaceViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditNoteActivity extends AppCompatActivity {

    public static final String EXTRA_PLACE_ID = "extra_place_id";
    public static final String EXTRA_NOTE_ID = "extra_note_id";

    private String placeId;
    private String noteId;
    private boolean isEditing = false;

    private PlaceViewModel viewModel;
    private TextInputEditText etTitle;
    private TextInputEditText etContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        placeId = getIntent().getStringExtra(EXTRA_PLACE_ID);
        noteId = getIntent().getStringExtra(EXTRA_NOTE_ID);
        isEditing = noteId != null;

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(isEditing ? R.string.note_edit_title : R.string.note_add_title);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        viewModel = new ViewModelProvider(this).get(PlaceViewModel.class);

        MaterialButton btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveNote());

        if (isEditing) {
            loadNoteData();
        }
    }

    private void loadNoteData() {
        viewModel.getNotesForPlace(placeId).observe(this, notes -> {
            if (notes == null) return;
            for (PlaceNote note : notes) {
                if (note.id.equals(noteId)) {
                    etTitle.setText(note.title);
                    etContent.setText(note.content);
                    break;
                }
            }
        });
    }

    private void saveNote() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String content = etContent.getText() != null ? etContent.getText().toString().trim() : "";

        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Please enter a title or content", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditing) {
            PlaceNote note = new PlaceNote();
            note.id = noteId;
            note.placeId = placeId;
            note.title = title.isEmpty() ? "Untitled Note" : title;
            note.content = content;
            note.updatedAt = System.currentTimeMillis();
            viewModel.updateNote(note);
        } else {
            PlaceNote note = new PlaceNote();
            note.placeId = placeId;
            note.title = title.isEmpty() ? "Untitled Note" : title;
            note.content = content;
            note.createdAt = System.currentTimeMillis();
            note.updatedAt = System.currentTimeMillis();
            viewModel.insertNote(note);
        }

        Toast.makeText(this, R.string.note_saved, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}
