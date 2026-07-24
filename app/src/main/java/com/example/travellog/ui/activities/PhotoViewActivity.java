package com.example.travellog.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travellog.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;

public class PhotoViewActivity extends AppCompatActivity {

    public static final String EXTRA_FILE_PATH = "extra_file_path";

    private ImageView ivPhotoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        ivPhotoView = findViewById(R.id.ivPhotoView);

        String filePath = getIntent().getStringExtra(EXTRA_FILE_PATH);
        if (filePath != null) {
            try {
                File file = new File(filePath);
                if (file.exists()) {
                    ivPhotoView.setImageURI(Uri.fromFile(file));
                }
            } catch (Exception e) {
                ivPhotoView.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }

        // Click to close
        ivPhotoView.setOnClickListener(v -> finish());
    }
}
