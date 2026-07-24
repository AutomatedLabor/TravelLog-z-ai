package com.example.travellog.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travellog.R;
import com.google.android.material.appbar.MaterialToolbar;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;

public class MapActivity extends AppCompatActivity {

    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    public static final String EXTRA_PLACE_NAME = "extra_place_name";

    private MapView mapView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        Configuration.getInstance().setUserAgentValue(getPackageName());

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(15.0);

        double lat = getIntent().getDoubleExtra(EXTRA_LATITUDE, 0);
        double lon = getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0);

        if (lat != 0 && lon != 0) {
            mapView.getController().setCenter(new org.osmdroid.util.GeoPoint(lat, lon));
        } else {
            mapView.getController().setCenter(new org.osmdroid.util.GeoPoint(51.5074, -0.1278));
            mapView.getController().setZoom(5.0);
        }

        findViewById(R.id.btnZoomIn).setOnClickListener(v -> mapView.getController().zoomIn());
        findViewById(R.id.btnZoomOut).setOnClickListener(v -> mapView.getController().zoomOut());
        findViewById(R.id.btnMyLocation).setOnClickListener(v ->
                mapView.getController().setCenter(new org.osmdroid.util.GeoPoint(51.5074, -0.1278))
        );
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
