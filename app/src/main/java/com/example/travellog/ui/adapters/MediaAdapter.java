package com.example.travellog.ui.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travellog.R;
import com.example.travellog.data.db.entity.PlaceMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {

    public interface OnMediaClickListener {
        void onMediaClick(PlaceMedia media, int position);
        void onMediaLongClick(PlaceMedia media, int position);
    }

    private final Context context;
    private List<PlaceMedia> mediaList = new ArrayList<>();
    private final OnMediaClickListener listener;

    public MediaAdapter(Context context, OnMediaClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setMedia(List<PlaceMedia> media) {
        this.mediaList = media != null ? media : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_media, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        PlaceMedia media = mediaList.get(position);
        holder.bind(media);
    }

    class MediaViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivMedia;
        private final ImageView ivAudioIcon;
        private final ImageView ivPlayOverlay;

        MediaViewHolder(View itemView) {
            super(itemView);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            ivAudioIcon = itemView.findViewById(R.id.ivAudioIcon);
            ivPlayOverlay = itemView.findViewById(R.id.ivPlayOverlay);
        }

        void bind(PlaceMedia media) {
            // Hide all overlays by default
            ivAudioIcon.setVisibility(View.GONE);
            ivPlayOverlay.setVisibility(View.GONE);

            if (PlaceMedia.TYPE_AUDIO.equals(media.mediaType)) {
                ivMedia.setImageResource(android.R.drawable.ic_lock_silent_mode);
                ivMedia.setScaleType(ImageView.ScaleType.CENTER);
                ivAudioIcon.setVisibility(View.VISIBLE);
            } else if (PlaceMedia.TYPE_VIDEO.equals(media.mediaType)) {
                ivMedia.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ivPlayOverlay.setVisibility(View.VISIBLE);
                // Try to show video thumbnail
                loadThumbnail(media.filePath);
            } else {
                // Photo or receipt
                ivMedia.setScaleType(ImageView.ScaleType.CENTER_CROP);
                loadImage(media.filePath);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMediaClick(media, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onMediaLongClick(media, getAdapterPosition());
                }
                return true;
            });
        }

        private void loadImage(String path) {
            if (path == null) {
                ivMedia.setImageResource(android.R.drawable.ic_menu_gallery);
                return;
            }
            try {
                File file = new File(path);
                if (file.exists()) {
                    ivMedia.setImageURI(Uri.fromFile(file));
                } else {
                    ivMedia.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            } catch (Exception e) {
                ivMedia.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }

        private void loadThumbnail(String path) {
            if (path == null) {
                ivMedia.setImageResource(android.R.drawable.ic_menu_slideshow);
                return;
            }
            try {
                File file = new File(path);
                if (file.exists()) {
                    ivMedia.setImageURI(Uri.fromFile(file));
                } else {
                    ivMedia.setImageResource(android.R.drawable.ic_menu_slideshow);
                }
            } catch (Exception e) {
                ivMedia.setImageResource(android.R.drawable.ic_menu_slideshow);
            }
        }
    }
}
