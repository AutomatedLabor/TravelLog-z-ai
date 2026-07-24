package com.example.travellog.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.travellog.data.db.dao.PlaceDao;
import com.example.travellog.data.db.dao.PlaceMediaDao;
import com.example.travellog.data.db.dao.PlaceNoteDao;
import com.example.travellog.data.db.dao.TripDao;
import com.example.travellog.data.db.entity.Place;
import com.example.travellog.data.db.entity.PlaceMedia;
import com.example.travellog.data.db.entity.PlaceNote;
import com.example.travellog.data.db.entity.Trip;

@Database(
    entities = {
        Place.class,
        PlaceMedia.class,
        PlaceNote.class,
        Trip.class
    },
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract PlaceDao placeDao();
    public abstract PlaceMediaDao placeMediaDao();
    public abstract PlaceNoteDao placeNoteDao();
    public abstract TripDao tripDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "travellog_database"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
