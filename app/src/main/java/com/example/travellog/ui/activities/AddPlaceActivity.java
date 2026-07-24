package com.example.travellog.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.travellog.R;
import com.example.travellog.data.db.entity.Place;
import com.example.travellog.data.model.Category;
import com.example.travellog.ui.viewmodel.PlaceViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddPlaceActivity extends AppCompatActivity {

    public static final String EXTRA_PLACE_ID = "extra_place_id";

    private PlaceViewModel viewModel;
    private boolean isEditing = false;
    private String editPlaceId = null;

    private TextInputEditText etName;
    private TextInputEditText etDescription;
    private AutoCompleteTextView actvCategory;
    private TextInputEditText etAddress;
    private TextInputEditText etPhone;
    private TextInputEditText etWebsite;
    private TextInputEditText etTags;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Check if editing
        editPlaceId = getIntent().getStringExtra(EXTRA_PLACE_ID);
        isEditing = editPlaceId != null;

        if (isEditing) {
            getSupportActionBar().setTitle(R.string.edit_place);
        } else {
            getSupportActionBar().setTitle(R.string.add_place);
        }

        initViews();
        setupCategoryDropdown();
        viewModel = new ViewModelProvider(this).get(PlaceViewModel.class);

        if (isEditing) {
            loadPlaceData();
        }

        // Save button
        MaterialButton btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> savePlace());
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        actvCategory = findViewById(R.id.actvCategory);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etWebsite = findViewById(R.id.etWebsite);
        etTags = findViewById(R.id.etTags);
        ratingBar = findViewById(R.id.ratingBar);
    }

    private void setupCategoryDropdown() {
        String[] categories = Category.getCategoryNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categories);
        actvCategory.setAdapter(adapter);
        actvCategory.setText(Category.getCategoryName(Category.OTHER), false);
    }

    private void loadPlaceData() {
        viewModel.getPlaceById(editPlaceId).observe(this, place -> {
            if (place == null) return;
            etName.setText(place.name);
            etDescription.setText(place.description);
            actvCategory.setText(Category.getCategoryName(place.category), false);
            etAddress.setText(place.address);
            etPhone.setText(place.phone);
            etWebsite.setText(place.website);
            etTags.setText(place.tags);
            ratingBar.setRating(place.rating);
        });
    }

    private void savePlace() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        if (TextUtils.isEmpty(name)) {
            TextInputLayout tilName = findViewById(R.id.tilName);
            tilName.setError(getString(R.string.place_name_required));
            return;
        }

        String description = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        String categoryStr = actvCategory.getText() != null ? actvCategory.getText().toString().trim() : "";
        String address = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String website = etWebsite.getText() != null ? etWebsite.getText().toString().trim() : "";
        String tags = etTags.getText() != null ? etTags.getText().toString().trim() : "";
        float rating = ratingBar.getRating();

        // Find category ID from name
        String categoryId = Category.OTHER;
        for (Category cat : Category.ALL_CATEGORIES) {
            if (cat.name.equalsIgnoreCase(categoryStr)) {
                categoryId = cat.id;
                break;
            }
        }

        if (isEditing) {
            // Update existing place
            Place place = viewModel.getPlaceByIdSync(editPlaceId);
            if (place != null) {
                place.name = name;
                place.description = description;
                place.category = categoryId;
                place.address = address;
                place.phone = phone;
                place.website = website;
                place.tags = tags;
                place.rating = rating;
                place.updatedAt = System.currentTimeMillis();
                viewModel.updatePlace(place);
                Toast.makeText(this, R.string.place_saved, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        } else {
            // Create new place
            Place place = new Place();
            place.name = name;
            place.description = description;
            place.category = categoryId;
            place.address = address;
            place.phone = phone;
            place.website = website;
            place.tags = tags;
            place.rating = rating;
            place.isFavorite = false;
            place.visitCount = 0;
            place.createdAt = System.currentTimeMillis();
            place.updatedAt = System.currentTimeMillis();

            viewModel.insertPlace(place);
            Toast.makeText(this, R.string.place_saved, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }
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
