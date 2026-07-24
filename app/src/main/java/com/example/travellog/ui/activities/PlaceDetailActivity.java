package com.example.travellog.ui.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travellog.R;
import com.example.travellog.data.db.entity.Place;
import com.example.travellog.data.db.entity.PlaceMedia;
import com.example.travellog.data.db.entity.PlaceNote;
import com.example.travellog.data.model.Category;
import com.example.travellog.ui.adapters.MediaAdapter;
import com.example.travellog.ui.adapters.NoteListAdapter;
import com.example.travellog.ui.viewmodel.PlaceViewModel;
import com.example.travellog.util.FileUtil;
import com.example.travellog.util.PermissionUtil;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager2.widget.ViewPager2;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlaceDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PLACE_ID = "extra_place_id";

    private static final int REQUEST_CAPTURE_PHOTO = 200;
    private static final int REQUEST_PICK_PHOTO = 201;
    private static final int REQUEST_CAPTURE_VIDEO = 202;
    private static final int REQUEST_RECORD_AUDIO = 203;
    private static final int REQUEST_ADD_NOTE = 204;

    private PlaceViewModel viewModel;
    private String placeId;

    // Views
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView ivCoverImage;
    private ImageView btnFavorite;
    private ImageView btnShare;
    private FloatingActionButton fabAddMedia;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    // Fragments content
    private LinearLayout layoutOverview;
    private LinearLayout layoutMedia;
    private LinearLayout layoutNotes;

    private RecyclerView mediaRecyclerView;
    private RecyclerView notesRecyclerView;
    private MediaAdapter mediaAdapter;
    private NoteListAdapter noteAdapter;

    private TextView tvName;
    private TextView tvCategory;
    private TextView tvAddress;
    private TextView tvPhone;
    private TextView tvWebsite;
    private TextView tvDescription;
    private TextView tvTags;
    private RatingBar ratingBar;

    private Place currentPlace;
    private File currentPhotoFile;
    private File currentVideoFile;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        placeId = getIntent().getStringExtra(EXTRA_PLACE_ID);
        if (placeId == null) {
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupViewPager();
        setupAdapters();
        observePlace();
        observeMedia();
        observeNotes();
    }

    private void initViews() {
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        ivCoverImage = findViewById(R.id.ivCoverImage);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnShare = findViewById(R.id.btnShare);
        fabAddMedia = findViewById(R.id.fabAddMedia);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Overview tab content
        tvName = findViewById(R.id.tvName);
        tvCategory = findViewById(R.id.tvCategory);
        tvAddress = findViewById(R.id.tvAddress);
        tvPhone = findViewById(R.id.tvPhone);
        tvWebsite = findViewById(R.id.tvWebsite);
        tvDescription = findViewById(R.id.tvDescription);
        tvTags = findViewById(R.id.tvTags);
        ratingBar = findViewById(R.id.ratingBar);
    }

    private void setupToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnFavorite.setOnClickListener(v -> {
            if (currentPlace != null) {
                viewModel.toggleFavorite(currentPlace.id);
            }
        });

        btnShare.setOnClickListener(v -> sharePlace());

        fabAddMedia.setOnClickListener(v -> showMediaOptions());
    }

    private void setupViewPager() {
        // We'll handle tab switching manually with visibility
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_overview));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_media));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_notes));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupAdapters() {
        mediaAdapter = new MediaAdapter(this, new MediaAdapter.OnMediaClickListener() {
            @Override
            public void onMediaClick(PlaceMedia media, int position) {
                if (PlaceMedia.TYPE_PHOTO.equals(media.mediaType) || PlaceMedia.TYPE_RECEIPT.equals(media.mediaType)) {
                    Intent intent = new Intent(PlaceDetailActivity.this, PhotoViewActivity.class);
                    intent.putExtra(PhotoViewActivity.EXTRA_FILE_PATH, media.filePath);
                    startActivity(intent);
                } else if (PlaceMedia.TYPE_AUDIO.equals(media.mediaType)) {
                    playAudio(media);
                }
            }

            @Override
            public void onMediaLongClick(PlaceMedia media, int position) {
                confirmDeleteMedia(media);
            }
        });

        noteAdapter = new NoteListAdapter(this, new NoteListAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(PlaceNote note, int position) {
                Intent intent = new Intent(PlaceDetailActivity.this, EditNoteActivity.class);
                intent.putExtra(EditNoteActivity.EXTRA_PLACE_ID, placeId);
                intent.putExtra(EditNoteActivity.EXTRA_NOTE_ID, note.id);
                startActivityForResult(intent, REQUEST_ADD_NOTE);
            }

            @Override
            public void onNoteLongClick(PlaceNote note, int position) {
                confirmDeleteNote(note);
            }
        });
    }

    private void observePlace() {
        viewModel = new ViewModelProvider(this).get(PlaceViewModel.class);
        viewModel.getPlaceById(placeId).observe(this, place -> {
            if (place == null) {
                finish();
                return;
            }
            currentPlace = place;
            bindPlace(place);
        });
    }

    private void observeMedia() {
        viewModel.getMediaForPlace(placeId).observe(this, media -> {
            mediaAdapter.setMedia(media);
            updateMediaCount(media != null ? media.size() : 0);
        });
    }

    private void observeNotes() {
        viewModel.getNotesForPlace(placeId).observe(this, notes -> {
            noteAdapter.setNotes(notes);
            updateNoteCount(notes != null ? notes.size() : 0);
        });
    }

    private void bindPlace(Place place) {
        collapsingToolbar.setTitle(place.name);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(place.name);
        }

        tvName.setText(place.name);
        tvCategory.setText(Category.getCategoryName(place.category));
        tvAddress.setText(TextUtils.isEmpty(place.address) ? getString(R.string.no_address) : place.address);
        tvPhone.setText(TextUtils.isEmpty(place.phone) ? "" : place.phone);
        tvWebsite.setText(TextUtils.isEmpty(place.website) ? "" : place.website);
        tvDescription.setText(TextUtils.isEmpty(place.description) ? "" : place.description);
        tvTags.setText(TextUtils.isEmpty(place.tags) ? "" : place.tags);
        ratingBar.setRating(place.rating);

        // Favorite state
        if (place.isFavorite) {
            btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
        }

        // Cover image
        if (!TextUtils.isEmpty(place.coverImagePath)) {
            try {
                File imgFile = new File(place.coverImagePath);
                if (imgFile.exists()) {
                    ivCoverImage.setImageURI(Uri.fromFile(imgFile));
                }
            } catch (Exception e) {
                ivCoverImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }

        // Update tab layout with counts
        // Media and note counts updated by LiveData observers
    }

    private void switchTab(int position) {
        viewPager.setVisibility(View.GONE);

        // Remove all content views
        viewPager.removeAllViews();

        View contentView;
        switch (position) {
            case 0: // Overview
                contentView = LayoutInflater.from(this).inflate(R.layout.fragment_overview, viewPager, false);
                bindOverviewContent(contentView);
                break;
            case 1: // Media
                contentView = LayoutInflater.from(this).inflate(R.layout.fragment_media, viewPager, false);
                bindMediaContent(contentView);
                break;
            case 2: // Notes
                contentView = LayoutInflater.from(this).inflate(R.layout.fragment_notes, viewPager, false);
                bindNotesContent(contentView);
                break;
            default:
                contentView = new View(this);
        }
    }

    private void bindOverviewContent(View view) {
        // Rebind overview fields from currentPlace
        // (The overview data is already bound in bindPlace above)
    }

    private void bindMediaContent(View view) {
        RecyclerView rv = view.findViewById(R.id.recyclerView);
        if (rv == null) return;
        rv.setLayoutManager(new GridLayoutManager(this, 3));
        rv.setAdapter(mediaAdapter);
        mediaRecyclerView = rv;
    }

    private void bindNotesContent(View view) {
        RecyclerView rv = view.findViewById(R.id.recyclerView);
        if (rv == null) return;
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(noteAdapter);
        notesRecyclerView = rv;

        // Add note FAB
        FloatingActionButton fabNote = view.findViewById(R.id.fabAdd);
        if (fabNote != null) {
            fabNote.setOnClickListener(v -> {
                Intent intent = new Intent(this, EditNoteActivity.class);
                intent.putExtra(EditNoteActivity.EXTRA_PLACE_ID, placeId);
                startActivityForResult(intent, REQUEST_ADD_NOTE);
            });
        }
    }

    private void updateMediaCount(int count) {
        if (tabLayout.getTabCount() > 1) {
            TabLayout.Tab mediaTab = tabLayout.getTabAt(1);
            if (mediaTab != null) {
                mediaTab.setText(getString(R.string.tab_media) + " (" + count + ")");
            }
        }
    }

    private void updateNoteCount(int count) {
        if (tabLayout.getTabCount() > 2) {
            TabLayout.Tab notesTab = tabLayout.getTabAt(2);
            if (notesTab != null) {
                notesTab.setText(getString(R.string.tab_notes) + " (" + count + ")");
            }
        }
    }

    private void showMediaOptions() {
        String[] options = {
                getString(R.string.take_photo),
                getString(R.string.add_photo),
                getString(R.string.take_video),
                getString(R.string.record_voice),
                getString(R.string.capture_receipt)
        };

        new MaterialAlertDialogBuilder(this)
                .setTitle("Add Media")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: capturePhoto(); break;
                        case 1: pickPhoto(); break;
                        case 2: captureVideo(); break;
                        case 3: recordVoice(); break;
                        case 4: captureReceipt(); break;
                    }
                })
                .show();
    }

    private void capturePhoto() {
        if (!PermissionUtil.hasCameraPermission(this)) {
            PermissionUtil.requestCameraAndStorage(this);
            return;
        }
        try {
            currentPhotoFile = FileUtil.createPhotoFile(this);
            Uri photoUri = FileProvider.getUriForFile(this,
                    "com.example.travellog.fileprovider", currentPhotoFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, REQUEST_CAPTURE_PHOTO);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void captureReceipt() {
        if (!PermissionUtil.hasCameraPermission(this)) {
            PermissionUtil.requestCameraAndStorage(this);
            return;
        }
        try {
            currentPhotoFile = FileUtil.createReceiptFile(this);
            Uri photoUri = FileProvider.getUriForFile(this,
                    "com.example.travellog.fileprovider", currentPhotoFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, REQUEST_CAPTURE_PHOTO);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_PHOTO);
    }

    private void captureVideo() {
        if (!PermissionUtil.hasCameraPermission(this)) {
            PermissionUtil.requestCameraAndStorage(this);
            return;
        }
        try {
            currentVideoFile = FileUtil.createVideoFile(this);
            Uri videoUri = FileProvider.getUriForFile(this,
                    "com.example.travellog.fileprovider", currentVideoFile);
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
            startActivityForResult(intent, REQUEST_CAPTURE_VIDEO);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Video camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void recordVoice() {
        Intent intent = new Intent(this, VoiceRecordActivity.class);
        intent.putExtra(VoiceRecordActivity.EXTRA_PLACE_ID, placeId);
        startActivityForResult(intent, REQUEST_RECORD_AUDIO);
    }

    private void playAudio(PlaceMedia media) {
        try {
            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(media.filePath);
            player.prepare();
            player.start();
            Toast.makeText(this, "Playing audio...", Toast.LENGTH_SHORT).show();

            player.setOnCompletionListener(mp -> {
                mp.release();
            });
        } catch (Exception e) {
            Toast.makeText(this, "Could not play audio", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case REQUEST_CAPTURE_PHOTO:
                if (currentPhotoFile != null && currentPhotoFile.exists()) {
                    PlaceMedia media = new PlaceMedia();
                    media.placeId = placeId;
                    media.mediaType = PlaceMedia.TYPE_PHOTO;
                    media.filePath = currentPhotoFile.getAbsolutePath();
                    media.createdAt = System.currentTimeMillis();
                    viewModel.insertMedia(media);
                    updateCoverImage(media.filePath);
                    Toast.makeText(this, R.string.photo_saved, Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_PICK_PHOTO:
                if (data != null && data.getData() != null) {
                    try {
                        String path = data.getData().toString();
                        PlaceMedia media = new PlaceMedia();
                        media.placeId = placeId;
                        media.mediaType = PlaceMedia.TYPE_PHOTO;
                        media.filePath = path;
                        media.createdAt = System.currentTimeMillis();
                        viewModel.insertMedia(media);
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to add photo", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case REQUEST_CAPTURE_VIDEO:
                if (currentVideoFile != null && currentVideoFile.exists()) {
                    PlaceMedia media = new PlaceMedia();
                    media.placeId = placeId;
                    media.mediaType = PlaceMedia.TYPE_VIDEO;
                    media.filePath = currentVideoFile.getAbsolutePath();
                    media.createdAt = System.currentTimeMillis();
                    viewModel.insertMedia(media);
                    Toast.makeText(this, "Video saved", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_RECORD_AUDIO:
                // Audio is saved by VoiceRecordActivity
                break;

            case REQUEST_ADD_NOTE:
                // Note is saved by EditNoteActivity
                break;
        }
    }

    private void updateCoverImage(String filePath) {
        if (currentPlace != null && !TextUtils.isEmpty(filePath)) {
            currentPlace.coverImagePath = filePath;
            currentPlace.updatedAt = System.currentTimeMillis();
            viewModel.updatePlace(currentPlace);
            try {
                File file = new File(filePath);
                if (file.exists()) {
                    ivCoverImage.setImageURI(Uri.fromFile(file));
                }
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    private void confirmDeleteMedia(PlaceMedia media) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete)
                .setMessage(R.string.delete_media_confirmation)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    viewModel.deleteMedia(media);
                    FileUtil.deleteFile(media.filePath);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void confirmDeleteNote(PlaceNote note) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete)
                .setMessage(R.string.delete_note_confirmation)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    viewModel.deleteNote(note);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void sharePlace() {
        if (currentPlace == null) return;
        StringBuilder shareText = new StringBuilder();
        shareText.append(currentPlace.name);
        if (!TextUtils.isEmpty(currentPlace.address)) {
            shareText.append("\n").append(currentPlace.address);
        }
        if (!TextUtils.isEmpty(currentPlace.description)) {
            shareText.append("\n\n").append(currentPlace.description);
        }
        if (currentPlace.rating > 0) {
            shareText.append("\nRating: ").append(currentPlace.rating).append("/5");
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_edit) {
            if (currentPlace != null) {
                Intent intent = new Intent(this, AddPlaceActivity.class);
                intent.putExtra(AddPlaceActivity.EXTRA_PLACE_ID, currentPlace.id);
                startActivityForResult(intent, 100);
            }
            return true;
        } else if (id == R.id.action_share) {
            sharePlace();
            return true;
        } else if (id == R.id.action_map) {
            if (currentPlace != null && currentPlace.latitude != 0 && currentPlace.longitude != 0) {
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra(MapActivity.EXTRA_LATITUDE, currentPlace.latitude);
                intent.putExtra(MapActivity.EXTRA_LONGITUDE, currentPlace.longitude);
                intent.putExtra(MapActivity.EXTRA_PLACE_NAME, currentPlace.name);
                startActivity(intent);
            }
            return true;
        } else if (id == R.id.action_delete) {
            confirmDeletePlace();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmDeletePlace() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_place)
                .setMessage(R.string.delete_confirmation)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    viewModel.deletePlaceById(placeId);
                    finish();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtil.REQUEST_CAMERA) {
            boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!granted) {
                Toast.makeText(this, R.string.permission_camera_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
