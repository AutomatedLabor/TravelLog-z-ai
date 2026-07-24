package com.example.travellog.ui.activities;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travellog.R;
import com.example.travellog.data.db.entity.PlaceMedia;
import com.example.travellog.ui.viewmodel.PlaceViewModel;
import com.example.travellog.util.FileUtil;
import com.example.travellog.util.PermissionUtil;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.io.IOException;

public class VoiceRecordActivity extends AppCompatActivity {

    public static final String EXTRA_PLACE_ID = "extra_place_id";

    private String placeId;
    private PlaceViewModel viewModel;

    private ImageButton btnRecord;
    private TextView tvTimer;
    private TextView tvStatus;
    private Button btnSave;

    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private File audioFile;
    private long recordingStartTime = 0;
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRecording) {
                long elapsed = System.currentTimeMillis() - recordingStartTime;
                int seconds = (int) (elapsed / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                tvTimer.setText(String.format("%02d:%02d", minutes, seconds));
                timerHandler.postDelayed(this, 100);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_record);

        placeId = getIntent().getStringExtra(EXTRA_PLACE_ID);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        btnRecord = findViewById(R.id.btnRecord);
        tvTimer = findViewById(R.id.tvTimer);
        tvStatus = findViewById(R.id.tvStatus);
        btnSave = findViewById(R.id.btnSave);

        viewModel = new androidx.lifecycle.ViewModelProvider(this).get(PlaceViewModel.class);

        btnRecord.setOnClickListener(v -> toggleRecording());

        btnSave.setOnClickListener(v -> {
            if (audioFile != null && audioFile.exists()) {
                PlaceMedia media = new PlaceMedia();
                media.placeId = placeId;
                media.mediaType = PlaceMedia.TYPE_AUDIO;
                media.filePath = audioFile.getAbsolutePath();
                media.createdAt = System.currentTimeMillis();
                viewModel.insertMedia(media);
                Toast.makeText(this, R.string.voice_saved, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void toggleRecording() {
        if (!isRecording) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        if (!PermissionUtil.hasAudioPermission(this)) {
            PermissionUtil.requestAudioAndStorage(this);
            return;
        }

        try {
            audioFile = FileUtil.createAudioFile(this);
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();

            isRecording = true;
            recordingStartTime = System.currentTimeMillis();
            tvStatus.setText(R.string.voice_record_recording);
            tvStatus.setTextColor(getResources().getColor(R.color.recording, getTheme()));
            btnRecord.setImageResource(android.R.drawable.ic_media_pause);
            timerHandler.post(timerRunnable);

        } catch (IOException e) {
            Toast.makeText(this, "Failed to start recording: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
            } catch (Exception e) {
                // Handle runtime exception
            }
            mediaRecorder = null;
        }

        isRecording = false;
        timerHandler.removeCallbacks(timerRunnable);
        tvStatus.setText(R.string.recording_stopped);
        tvStatus.setTextColor(getResources().getColor(R.color.text_secondary, getTheme()));
        btnRecord.setImageResource(android.R.drawable.ic_btn_speak_now);
        btnSave.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            try {
                mediaRecorder.release();
            } catch (Exception e) {
                // Ignore
            }
            mediaRecorder = null;
        }
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted = grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED;
        if (granted) {
            toggleRecording();
        } else {
            Toast.makeText(this, R.string.permission_audio_denied, Toast.LENGTH_SHORT).show();
        }
    }
}
