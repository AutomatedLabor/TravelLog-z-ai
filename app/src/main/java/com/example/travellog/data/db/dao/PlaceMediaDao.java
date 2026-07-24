package com.example.travellog.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.travellog.data.db.entity.PlaceMedia;

import java.util.List;

@Dao
public interface PlaceMediaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PlaceMedia media);

    @Delete
    void delete(PlaceMedia media);

    @Query("DELETE FROM place_media WHERE id = :mediaId")
    void deleteById(String mediaId);

    @Query("DELETE FROM place_media WHERE placeId = :placeId")
    void deleteAllForPlace(String placeId);

    @Query("SELECT * FROM place_media WHERE placeId = :placeId ORDER BY createdAt DESC")
    LiveData<List<PlaceMedia>> getMediaForPlace(String placeId);

    @Query("SELECT * FROM place_media WHERE placeId = :placeId ORDER BY createdAt DESC")
    List<PlaceMedia> getMediaForPlaceSync(String placeId);

    @Query("SELECT * FROM place_media WHERE placeId = :placeId AND mediaType = :mediaType ORDER BY createdAt DESC")
    LiveData<List<PlaceMedia>> getMediaForPlaceByType(String placeId, String mediaType);

    @Query("SELECT * FROM place_media WHERE mediaType = :mediaType ORDER BY createdAt DESC")
    LiveData<List<PlaceMedia>> getAllMediaByType(String mediaType);

    @Query("SELECT COUNT(*) FROM place_media WHERE placeId = :placeId")
    LiveData<Integer> getMediaCountForPlace(String placeId);

    @Query("SELECT * FROM place_media WHERE id = :mediaId")
    PlaceMedia getMediaByIdSync(String mediaId);
}
