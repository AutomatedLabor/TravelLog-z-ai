package com.example.travellog.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travellog.R;
import com.example.travellog.data.db.entity.Place;
import com.example.travellog.ui.adapters.PlaceListAdapter;
import com.example.travellog.ui.viewmodel.PlaceViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private RecyclerView recyclerView;
    private PlaceListAdapter adapter;
    private PlaceViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        etSearch = findViewById(R.id.etSearch);
        recyclerView = findViewById(R.id.recyclerView);
        View tvEmpty = findViewById(R.id.tvEmpty);

        adapter = new PlaceListAdapter(this, new PlaceListAdapter.OnPlaceClickListener() {
            @Override
            public void onPlaceClick(Place place, int position) {
                Intent intent = new Intent(SearchActivity.this, PlaceDetailActivity.class);
                intent.putExtra(PlaceDetailActivity.EXTRA_PLACE_ID, place.id);
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(Place place, int position) {
                viewModel.toggleFavorite(place.id);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(PlaceViewModel.class);

        viewModel.getSearchResults().observe(this, places -> {
            adapter.setPlaces(places);
            if (places == null || places.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String query = etSearch.getText() != null ? etSearch.getText().toString().trim() : "";
            viewModel.setSearchQuery(query);
            return true;
        });
    }
}
