package com.example.travellog.data.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.travellog.data.db.AppDatabase;
import com.example.travellog.data.db.dao.PlaceDao;
import com.example.travellog.data.db.dao.PlaceMediaDao;
import com.example.travellog.data.db.dao.PlaceNoteDao;
import com.example.travellog.data.db.dao.TripDao;
import com.example.travellog.data.db.entity.Place;
import com.example.travellog.data.db.entity.PlaceMedia;
import com.example.travellog.data.db.entity.PlaceNote;
import com.example.travellog.data.db.entity.Trip;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlaceRepository {

    private final PlaceDao placeDao;
    private final PlaceMediaDao mediaDao;
    private final PlaceNoteDao noteDao;
    private final TripDao tripDao;
    private final ExecutorService executor;

    public PlaceRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        placeDao = db.placeDao();
        mediaDao = db.placeMediaDao();
        noteDao = db.placeNoteDao();
        tripDao = db.tripDao();
        executor = Executors.newFixedThreadPool(4);
    }

    // Place operations
    public LiveData<List<Place>> getAllPlaces() {
        return placeDao.getAllPlaces();
    }

    public LiveData<Place> getPlaceById(String placeId) {
        return placeDao.getPlaceById(placeId);
    }

    public Place getPlaceByIdSync(String placeId) {
        return placeDao.getPlaceByIdSync(placeId);
    }

    public LiveData<List<Place>> getPlacesByCategory(String category) {
        return placeDao.getPlacesByCategory(category);
    }

    public LiveData<List<Place>> getFavoritePlaces() {
        return placeDao.getFavoritePlaces();
    }

    public LiveData<List<Place>> searchPlaces(String query) {
        return placeDao.searchPlaces(query);
    }

    public LiveData<Integer> getPlaceCount() {
        return placeDao.getPlaceCount();
    }

    public LiveData<List<String>> getAllCategories() {
        return placeDao.getAllCategories();
    }

    public LiveData<List<Place>> getMostVisited(int limit) {
        return placeDao.getMostVisited(limit);
    }

    public LiveData<List<Place>> getTopRated(int limit) {
        return placeDao.getTopRated(limit);
    }

    public List<Place> getAllPlacesWithLocationSync() {
        return placeDao.getAllPlacesWithLocationSync();
    }

    public void insertPlace(Place place) {
        executor.execute(() -> placeDao.insert(place));
    }

    public void updatePlace(Place place) {
        executor.execute(() -> placeDao.update(place));
    }

    public void deletePlace(Place place) {
        executor.execute(() -> {
            mediaDao.deleteAllForPlace(place.id);
            noteDao.deleteAllForPlace(place.id);
            placeDao.delete(place);
        });
    }

    public void deletePlaceById(String placeId) {
        executor.execute(() -> {
            mediaDao.deleteAllForPlace(placeId);
            noteDao.deleteAllForPlace(placeId);
            placeDao.deleteById(placeId);
        });
    }

    public void toggleFavorite(String placeId) {
        executor.execute(() -> {
            Place place = placeDao.getPlaceByIdSync(placeId);
            if (place != null) {
                place.isFavorite = !place.isFavorite;
                place.updatedAt = System.currentTimeMillis();
                placeDao.update(place);
            }
        });
    }

    public void incrementVisitCount(String placeId) {
        executor.execute(() -> {
            placeDao.incrementVisitCount(placeId, System.currentTimeMillis());
        });
    }

    // Media operations
    public LiveData<List<PlaceMedia>> getMediaForPlace(String placeId) {
        return mediaDao.getMediaForPlace(placeId);
    }

    public List<PlaceMedia> getMediaForPlaceSync(String placeId) {
        return mediaDao.getMediaForPlaceSync(placeId);
    }

    public void insertMedia(PlaceMedia media) {
        executor.execute(() -> mediaDao.insert(media));
    }

    public void deleteMedia(PlaceMedia media) {
        executor.execute(() -> mediaDao.delete(media));
    }

    public void deleteMediaById(String mediaId) {
        executor.execute(() -> mediaDao.deleteById(mediaId));
    }

    // Note operations
    public LiveData<List<PlaceNote>> getNotesForPlace(String placeId) {
        return noteDao.getNotesForPlace(placeId);
    }

    public List<PlaceNote> getNotesForPlaceSync(String placeId) {
        return noteDao.getNotesForPlaceSync(placeId);
    }

    public void insertNote(PlaceNote note) {
        executor.execute(() -> noteDao.insert(note));
    }

    public void updateNote(PlaceNote note) {
        executor.execute(() -> noteDao.update(note));
    }

    public void deleteNote(PlaceNote note) {
        executor.execute(() -> noteDao.delete(note));
    }

    public void deleteNoteById(String noteId) {
        executor.execute(() -> noteDao.deleteById(noteId));
    }

    // Trip operations
    public LiveData<List<Trip>> getAllTrips() {
        return tripDao.getAllTrips();
    }

    public LiveData<Trip> getTripById(String tripId) {
        return tripDao.getTripById(tripId);
    }

    public void insertTrip(Trip trip) {
        executor.execute(() -> tripDao.insert(trip));
    }

    public void updateTrip(Trip trip) {
        executor.execute(() -> tripDao.update(trip));
    }

    public void deleteTrip(Trip trip) {
        executor.execute(() -> tripDao.delete(trip));
    }

    public void deleteTripById(String tripId) {
        executor.execute(() -> tripDao.deleteById(tripId));
    }
}
