package com.example.travellog.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.travellog.data.db.entity.Trip;

import java.util.List;

@Dao
public interface TripDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Trip trip);

    @Update
    void update(Trip trip);

    @Delete
    void delete(Trip trip);

    @Query("DELETE FROM trips WHERE id = :tripId")
    void deleteById(String tripId);

    @Query("SELECT * FROM trips ORDER BY startDate DESC")
    LiveData<List<Trip>> getAllTrips();

    @Query("SELECT * FROM trips WHERE id = :tripId")
    LiveData<Trip> getTripById(String tripId);

    @Query("SELECT * FROM trips WHERE id = :tripId")
    Trip getTripByIdSync(String tripId);

    @Query("SELECT COUNT(*) FROM trips")
    LiveData<Integer> getTripCount();
}
