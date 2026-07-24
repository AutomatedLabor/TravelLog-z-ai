package com.example.travellog.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.travellog.data.db.entity.Place;

import java.util.List;

@Dao
public interface PlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Place place);

    @Update
    void update(Place place);

    @Delete
    void delete(Place place);

    @Query("DELETE FROM places WHERE id = :placeId")
    void deleteById(String placeId);

    @Query("SELECT * FROM places ORDER BY isFavorite DESC, updatedAt DESC")
    LiveData<List<Place>> getAllPlaces();

    @Query("SELECT * FROM places ORDER BY isFavorite DESC, updatedAt DESC")
    List<Place> getAllPlacesSync();

    @Query("SELECT * FROM places WHERE id = :placeId")
    LiveData<Place> getPlaceById(String placeId);

    @Query("SELECT * FROM places WHERE id = :placeId")
    Place getPlaceByIdSync(String placeId);

    @Query("SELECT * FROM places WHERE category = :category ORDER BY updatedAt DESC")
    LiveData<List<Place>> getPlacesByCategory(String category);

    @Query("SELECT * FROM places WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    LiveData<List<Place>> getFavoritePlaces();

    @Query("SELECT * FROM places WHERE tripId = :tripId ORDER BY createdAt ASC")
    LiveData<List<Place>> getPlacesByTrip(String tripId);

    @Query("SELECT * FROM places WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR address LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    LiveData<List<Place>> searchPlaces(String query);

    @Query("SELECT * FROM places WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR address LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    List<Place> searchPlacesSync(String query);

    @Query("SELECT COUNT(*) FROM places")
    LiveData<Integer> getPlaceCount();

    @Query("SELECT COUNT(*) FROM places WHERE category = :category")
    LiveData<Integer> getPlaceCountByCategory(String category);

    @Query("SELECT COUNT(*) FROM places WHERE isFavorite = 1")
    LiveData<Integer> getFavoriteCount();

    @Query("SELECT DISTINCT category FROM places ORDER BY category")
    LiveData<List<String>> getAllCategories();

    @Query("SELECT DISTINCT tripId FROM places WHERE tripId IS NOT NULL AND tripId != '' ORDER BY tripId")
    LiveData<List<String>> getAllTripIds();

    @Query("UPDATE places SET visitCount = visitCount + 1, updatedAt = :timestamp WHERE id = :placeId")
    void incrementVisitCount(String placeId, long timestamp);

    @Query("SELECT * FROM places ORDER BY visitCount DESC LIMIT :limit")
    LiveData<List<Place>> getMostVisited(int limit);

    @Query("SELECT * FROM places ORDER BY rating DESC LIMIT :limit")
    LiveData<List<Place>> getTopRated(int limit);

    @Query("SELECT * FROM places WHERE latitude != 0 AND longitude != 0")
    List<Place> getAllPlacesWithLocationSync();
}
