package com.example.travellog.data.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "trips")
public class Trip {

    @PrimaryKey
    @NonNull
    public String id = UUID.randomUUID().toString();

    public String name;
    public String description;
    public String coverImagePath;
    public long startDate;
    public long endDate;
    public long createdAt;
    public long updatedAt;
    public int placeCount;

    public Trip() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
}
