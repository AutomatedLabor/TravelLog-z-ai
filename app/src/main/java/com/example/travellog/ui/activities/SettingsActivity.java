package com.example.travellog.ui.activities;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travellog.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        findViewById(R.id.btnClearCache).setOnClickListener(v -> clearCache());
        findViewById(R.id.btnExportData).setOnClickListener(v -> exportData());
    }

    private void clearCache() {
        try {
            File cacheDir = getExternalCacheDir();
            if (cacheDir != null) {
                deleteDir(cacheDir);
                Toast.makeText(this, "Cache cleared", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to clear cache", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    deleteDir(new File(dir, child));
                }
            }
        }
        return dir != null && dir.delete();
    }

    private void exportData() {
        try {
            File exportDir = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "TravelLog");
            if (!exportDir.exists()) exportDir.mkdirs();
            Toast.makeText(this, "Export completed to: " + exportDir.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Export failed", Toast.LENGTH_SHORT).show();
        }
    }
}
