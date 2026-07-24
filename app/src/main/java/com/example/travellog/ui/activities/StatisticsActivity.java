package com.example.travellog.ui.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travellog.R;
import com.example.travellog.data.db.entity.Place;
import com.example.travellog.data.model.Category;
import com.example.travellog.ui.adapters.PlaceListAdapter;
import com.example.travellog.ui.viewmodel.PlaceViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class StatisticsActivity extends AppCompatActivity {

    private PlaceViewModel viewModel;
    private PlaceListAdapter mostVisitedAdapter;
    private PlaceListAdapter topRatedAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(PlaceViewModel.class);

        // Setup most visited list
        RecyclerView rvMostVisited = findViewById(R.id.recyclerViewMostVisited);
        mostVisitedAdapter = new PlaceListAdapter(this, (place, position) -> {
            // Click handler
        });
        rvMostVisited.setLayoutManager(new LinearLayoutManager(this));
        rvMostVisited.setAdapter(mostVisitedAdapter);

        // Setup top rated list
        RecyclerView rvTopRated = findViewById(R.id.recyclerViewTopRated);
        topRatedAdapter = new PlaceListAdapter(this, (place, position) -> {
            // Click handler
        });
        rvTopRated.setLayoutManager(new LinearLayoutManager(this));
        rvTopRated.setAdapter(topRatedAdapter);

        // Observe data
        viewModel.getPlaceCount().observe(this, count -> {
            if (count != null) {
                ((android.widget.TextView) findViewById(R.id.tvTotalPlaces)).setText(String.valueOf(count));
            }
        });

        viewModel.getFavoritePlaces().observe(this, places -> {
            if (places != null) {
                ((android.widget.TextView) findViewById(R.id.tvTotalFavorites)).setText(String.valueOf(places.size()));
            }
        });

        viewModel.getAllCategories().observe(this, categories -> {
            if (categories != null) {
                ((android.widget.TextView) findViewById(R.id.tvTotalCategories)).setText(String.valueOf(categories.size()));
            }
        });

        viewModel.getMostVisited().observe(this, places -> {
            mostVisitedAdapter.setPlaces(places);
        });

        viewModel.getTopRated().observe(this, places -> {
            topRatedAdapter.setPlaces(places);
        });
    }
}
