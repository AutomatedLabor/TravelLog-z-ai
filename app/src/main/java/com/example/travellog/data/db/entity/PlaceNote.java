package com.example.travellog.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(
    tableName = "place_notes",
    indices = {
        @Index(value = "placeId", name = "idx_note_place")
    }
)
public class PlaceNote {

    @PrimaryKey
    @NonNull
    public String id = UUID.randomUUID().toString();

    public String placeId;
    public String title;
    public String content;
    public long createdAt;
    public long updatedAt;

    public PlaceNote() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
}
