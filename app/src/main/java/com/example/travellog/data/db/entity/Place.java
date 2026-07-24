package com.example.travellog.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(
    tableName = "places",
    indices = {
        @Index(value = "category", name = "idx_place_category"),
        @Index(value = "name", name = "idx_place_name"),
        @Index(value = "isFavorite", name = "idx_place_favorite"),
        @Index(value = "createdAt", name = "idx_place_created")
    }
)
public class Place {

    @PrimaryKey
    @NonNull
    public String id = UUID.randomUUID().toString();

    public String name;
    public String description;
    public String category;
    public double latitude;
    public double longitude;
    public String address;
    public String phone;
    public String website;
    public float rating;
    public boolean isFavorite;
    public String tags; // JSON array of tag strings
    public long createdAt;
    public long updatedAt;
    public String tripId; // optional trip grouping
    public int visitCount;
    public String receiptTotal; // total receipt amount as string
    public String receiptCurrency;
    public String coverImagePath; // first photo as cover
}
