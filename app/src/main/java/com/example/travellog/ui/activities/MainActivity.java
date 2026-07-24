package com.example.travellog.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.travellog.R;
import com.example.travellog.TravelLogApp;
import com.example.travellog.data.db.entity.Place;
import com.example.travellog.data.model.Category;
import com.example.travellog.ui.adapters.PlaceListAdapter;
import com.example.travellog.ui.viewmodel.PlaceViewModel;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements PlaceListAdapter.OnPlaceClickListener {

    private RecyclerView recyclerView;
    private PlaceListAdapter adapter;
    private PlaceViewModel viewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View emptyState;
    private String selectedCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar));

        // Init views
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        emptyState = findViewById(R.id.emptyState);

        // Setup RecyclerView
        adapter = new PlaceListAdapter(this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(PlaceViewModel.class);

        // Observe places
        viewModel.getAllPlaces().observe(this, places -> {
            List<Place> filtered = filterByCategory(places);
            adapter.setPlaces(filtered);
            updateEmptyState(filtered);
        });

        // Pull to refresh
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.accent, getTheme()));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
        });

        // FAB
        findViewById(R.id.fabAdd).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddPlaceActivity.class);
            startActivityForResult(intent, 100);
        });

        // Setup category chips
        setupCategoryChips();
    }

    private void setupCategoryChips() {
        androidx.appcompat.widget.LinearLayoutCompat chipContainer = null;

        // Find the chip group
        com.google.android.material.chip.ChipGroup chipGroup = findViewById(R.id.categoryChipGroup);
        if (chipGroup == null) return;

        chipGroup.removeAllViews();

        // All chip
        Chip allChip = new Chip(this);
        allChip.setText(R.string.all_categories);
        allChip.setCheckable(true);
        allChip.setChecked(true);
        allChip.setOnClickListener(v -> {
            selectedCategory = null;
            deselectOtherChips(chipGroup, null);
            allChip.setChecked(true);
            refreshList();
        });
        chipGroup.addView(allChip);

        // Category chips
        for (Category cat : Category.ALL_CATEGORIES) {
            Chip chip = new Chip(this);
            chip.setText(cat.name);
            chip.setCheckable(true);
            chip.setChipBackgroundColor(cat.colorRes);
            chip.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
            chip.setOnClickListener(v -> {
                selectedCategory = cat.id;
                deselectOtherChips(chipGroup, chip);
                chip.setChecked(true);
                refreshList();
            });
            chipGroup.addView(chip);
        }
    }

    private void deselectOtherChips(com.google.android.material.chip.ChipGroup group, Chip selected) {
        for (int i = 0; i < group.getChildCount(); i++) {
            Chip chip = (Chip) group.getChildAt(i);
            if (chip != selected) {
                chip.setChecked(false);
            }
        }
    }

    private void refreshList() {
        if (selectedCategory == null) {
            viewModel.getAllPlaces().observe(this, places -> {
                adapter.setPlaces(places);
                updateEmptyState(places);
            });
        } else {
            viewModel.getPlacesByCategory(selectedCategory).observe(this, places -> {
                adapter.setPlaces(places);
                updateEmptyState(places);
            });
        }
    }

    private List<Place> filterByCategory(List<Place> places) {
        if (selectedCategory == null || selectedCategory.isEmpty()) {
            return places;
        }
        List<Place> filtered = new ArrayList<>();
        for (Place p : places) {
            if (selectedCategory.equals(p.category)) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    private void updateEmptyState(List<Place> places) {
        if (places == null || places.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.setSearchQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.setSearchQuery(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_map) {
            startActivity(new Intent(this, MapActivity.class));
            return true;
        } else if (id == R.id.action_stats) {
            startActivity(new Intent(this, StatisticsActivity.class));
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPlaceClick(Place place, int position) {
        viewModel.incrementVisitCount(place.id);
        Intent intent = new Intent(this, PlaceDetailActivity.class);
        intent.putExtra(PlaceDetailActivity.EXTRA_PLACE_ID, place.id);
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(Place place, int position) {
        viewModel.toggleFavorite(place.id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Place was added or edited, refresh handled by LiveData
        }
    }
}
