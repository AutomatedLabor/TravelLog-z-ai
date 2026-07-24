package com.example.travellog.ui.activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.travellog.R;
import com.example.travellog.data.db.entity.Place;
import com.example.travellog.data.model.Category;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    public static final String EXTRA_PLACE_NAME = "extra_place_name";

    private MapView mapView;
    private MapController mapController;
    private ArrayList<OverlayItem> overlayItems = new ArrayList<>();
    private ItemizedIconOverlay<OverlayItem> itemOverlay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Toolbar
        androidx.appcompat.widget.ToolbarCompat toolbarCompat = null;
        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Init OSMDroid
        Context ctx = getApplicationContext();
        Configuration.getInstance().setUserAgentValue(ctx.getPackageName());

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);

        mapController = (MapController) mapView.getController();
        mapController.setZoom(15.0);

        // Check for center coordinates
        double lat = getIntent().getDoubleExtra(EXTRA_LATITUDE, 0);
        double lon = getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0);
        String placeName = getIntent().getStringExtra(EXTRA_PLACE_NAME);

        if (lat != 0 && lon != 0) {
            mapController.setCenter(new org.osmdroid.api.GeoPoint(lat, lon));

            // Add marker
            OverlayItem item = new OverlayItem(
                    placeName != null ? placeName : "Place",
                    "",
                    new org.osmdroid.api.GeoPoint(lat, lon)
            );
            item.setMarker(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_myplaces));
            overlayItems.add(item);
        } else {
            // Default center (London)
            mapController.setCenter(new org.osmdroid.api.GeoPoint(51.5074, -0.1278));
            mapController.setZoom(5.0);

            // Load all places with locations
            loadAllPlaceMarkers();
        }

        setupOverlay();
        setupZoomButtons();
    }

    private void loadAllPlaceMarkers() {
        // This would typically be done with ViewModel, but for simplicity
        // we'll use the repository directly in a real app
        // For now, overlay is set up with whatever items exist
    }

    private void setupOverlay() {
        Drawable marker = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_myplaces);
        if (marker == null) return;

        itemOverlay = new ItemizedIconOverlay<>(overlayItems, marker, null);
        mapView.getOverlays().add(itemOverlay);
        mapView.invalidate();
    }

    private void setupZoomButtons() {
        findViewById(R.id.btnZoomIn).setOnClickListener(v -> {
            mapController.zoomIn();
        });

        findViewById(R.id.btnZoomOut).setOnClickListener(v -> {
            mapController.zoomOut();
        });

        findViewById(R.id.btnMyLocation).setOnClickListener(v -> {
            // Would use FusedLocationProviderClient in production
            mapController.setCenter(new org.osmdroid.api.GeoPoint(51.5074, -0.1278));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
