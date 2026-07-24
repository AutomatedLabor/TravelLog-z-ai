package com.example.travellog.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(
    tableName = "place_media",
    indices = {
        @Index(value = "placeId", name = "idx_media_place"),
        @Index(value = {"placeId", "mediaType"}, name = "idx_media_place_type")
    }
)
public class PlaceMedia {

    public static final String TYPE_PHOTO = "photo";
    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_RECEIPT = "receipt";

    @PrimaryKey
    @NonNull
    public String id = UUID.randomUUID().toString();

    public String placeId;
    public String mediaType; // TYPE_PHOTO, TYPE_VIDEO, TYPE_AUDIO, TYPE_RECEIPT
    public String filePath;
    public String thumbnailPath;
    public String caption;
    public long createdAt;

    public PlaceMedia() {
        this.createdAt = System.currentTimeMillis();
    }
}
