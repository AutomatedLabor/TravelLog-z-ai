package com.example.travellog.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.travellog.data.db.entity.PlaceNote;

import java.util.List;

@Dao
public interface PlaceNoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PlaceNote note);

    @Update
    void update(PlaceNote note);

    @Delete
    void delete(PlaceNote note);

    @Query("DELETE FROM place_notes WHERE id = :noteId")
    void deleteById(String noteId);

    @Query("DELETE FROM place_notes WHERE placeId = :placeId")
    void deleteAllForPlace(String placeId);

    @Query("SELECT * FROM place_notes WHERE placeId = :placeId ORDER BY createdAt DESC")
    LiveData<List<PlaceNote>> getNotesForPlace(String placeId);

    @Query("SELECT * FROM place_notes WHERE placeId = :placeId ORDER BY createdAt DESC")
    List<PlaceNote> getNotesForPlaceSync(String placeId);

    @Query("SELECT * FROM place_notes WHERE id = :noteId")
    PlaceNote getNoteByIdSync(String noteId);

    @Query("SELECT * FROM place_notes ORDER BY updatedAt DESC LIMIT :limit")
    LiveData<List<PlaceNote>> getRecentNotes(int limit);
}
