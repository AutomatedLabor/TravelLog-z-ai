package com.example.travellog.ui.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travellog.R;
import com.example.travellog.data.db.entity.Place;
import com.example.travellog.data.model.Category;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder> {

    public interface OnPlaceClickListener {
        void onPlaceClick(Place place, int position);
        void onFavoriteClick(Place place, int position);
    }

    private final Context context;
    private List<Place> places = new ArrayList<>();
    private final OnPlaceClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);

    public PlaceListAdapter(Context context, OnPlaceClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setPlaces(List<Place> places) {
        this.places = places != null ? places : new ArrayList<>();
        notifyDataSetChanged();
    }

    public Place getPlace(int position) {
        if (position >= 0 && position < places.size()) {
            return places.get(position);
        }
        return null;
    }

    public void removePlace(int position) {
        if (position >= 0 && position < places.size()) {
            places.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, places.size());
        }
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_place_card, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place place = places.get(position);
        holder.bind(place);
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView card;
        private final ImageView ivCoverImage;
        private final Chip chipCategory;
        private final ImageView ivFavorite;
        private final TextView tvName;
        private final TextView tvDescription;
        private final RatingBar ratingBar;
        private final TextView tvAddress;
        private final TextView tvTags;
        private final TextView tvMediaCount;
        private final TextView tvNoteCount;

        PlaceViewHolder(View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            ivCoverImage = itemView.findViewById(R.id.ivCoverImage);
            chipCategory = itemView.findViewById(R.id.chipCategory);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvTags = itemView.findViewById(R.id.tvTags);
            tvMediaCount = itemView.findViewById(R.id.tvMediaCount);
            tvNoteCount = itemView.findViewById(R.id.tvNoteCount);
        }

        void bind(Place place) {
            // Name
            tvName.setText(TextUtils.isEmpty(place.name) ? context.getString(R.string.untitled_place) : place.name);

            // Description
            if (!TextUtils.isEmpty(place.description)) {
                tvDescription.setText(place.description);
                tvDescription.setVisibility(View.VISIBLE);
            } else {
                tvDescription.setVisibility(View.GONE);
            }

            // Category chip
            String categoryName = Category.getCategoryName(place.category);
            chipCategory.setText(categoryName);
            chipCategory.setChipBackgroundColor(Category.getCategoryColor(place.category));

            // Rating
            ratingBar.setRating(place.rating);

            // Address
            if (!TextUtils.isEmpty(place.address)) {
                tvAddress.setText(place.address);
                tvAddress.setVisibility(View.VISIBLE);
            } else {
                tvAddress.setText(context.getString(R.string.no_address));
            }

            // Tags
            if (!TextUtils.isEmpty(place.tags)) {
                tvTags.setText(place.tags);
                tvTags.setVisibility(View.VISIBLE);
            } else {
                tvTags.setVisibility(View.GONE);
            }

            // Favorite
            if (place.isFavorite) {
                ivFavorite.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                ivFavorite.setImageResource(android.R.drawable.btn_star_big_off);
            }

            // Note/media counts - these will be updated by the activity
            tvMediaCount.setText("");
            tvNoteCount.setText("");

            // Cover image
            if (!TextUtils.isEmpty(place.coverImagePath)) {
                // In production, load with Glide/Coil. For now, show placeholder
                ivCoverImage.setImageResource(android.R.drawable.ic_menu_gallery);
            } else {
                ivCoverImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            // Click listeners
            card.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlaceClick(place, getAdapterPosition());
                }
            });

            ivFavorite.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFavoriteClick(place, getAdapterPosition());
                }
            });
        }
    }
}
