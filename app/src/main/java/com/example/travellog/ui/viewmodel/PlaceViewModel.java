package com.example.travellog.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.travellog.TravelLogApp;
import com.example.travellog.data.db.entity.Place;
import com.example.travellog.data.db.entity.PlaceMedia;
import com.example.travellog.data.db.entity.PlaceNote;
import com.example.travellog.data.db.entity.Trip;
import com.example.travellog.data.repository.PlaceRepository;

import java.util.List;

public class PlaceViewModel extends AndroidViewModel {

    private final PlaceRepository repository;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");

    private final LiveData<List<Place>> allPlaces;
    private final LiveData<List<Place>> searchResults;
    private final LiveData<List<String>> allCategories;

    public PlaceViewModel(@NonNull Application application) {
        super(application);
        repository = ((TravelLogApp) application).getRepository();

        allPlaces = repository.getAllPlaces();
        allCategories = repository.getAllCategories();

        searchResults = Transformations.switchMap(searchQuery, query -> {
            if (query == null || query.trim().isEmpty()) {
                return repository.getAllPlaces();
            }
            return repository.searchPlaces(query.trim());
        });
    }

    public LiveData<List<Place>> getAllPlaces() {
        return allPlaces;
    }

    public LiveData<List<Place>> getSearchResults() {
        return searchResults;
    }

    public LiveData<Place> getPlaceById(String placeId) {
        return repository.getPlaceById(placeId);
    }

    public Place getPlaceByIdSync(String placeId) {
        return repository.getPlaceByIdSync(placeId);
    }

    public LiveData<List<Place>> getPlacesByCategory(String category) {
        return repository.getPlacesByCategory(category);
    }

    public LiveData<List<Place>> getFavoritePlaces() {
        return repository.getFavoritePlaces();
    }

    public LiveData<List<String>> getAllCategories() {
        return allCategories;
    }

    public LiveData<Integer> getPlaceCount() {
        return repository.getPlaceCount();
    }

    public LiveData<List<Place>> getMostVisited() {
        return repository.getMostVisited(10);
    }

    public LiveData<List<Place>> getTopRated() {
        return repository.getTopRated(10);
    }

    public List<Place> getAllPlacesWithLocationSync() {
        return repository.getAllPlacesWithLocationSync();
    }

    public LiveData<List<PlaceMedia>> getMediaForPlace(String placeId) {
        return repository.getMediaForPlace(placeId);
    }

    public List<PlaceMedia> getMediaForPlaceSync(String placeId) {
        return repository.getMediaForPlaceSync(placeId);
    }

    public LiveData<List<PlaceNote>> getNotesForPlace(String placeId) {
        return repository.getNotesForPlace(placeId);
    }

    public LiveData<List<PlaceNote>> getNotesForPlaceSync(String placeId) {
        return repository.getNotesForPlace(placeId);
    }

    public LiveData<List<Trip>> getAllTrips() {
        return repository.getAllTrips();
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void insertPlace(Place place) {
        repository.insertPlace(place);
    }

    public void updatePlace(Place place) {
        repository.updatePlace(place);
    }

    public void deletePlace(Place place) {
        repository.deletePlace(place);
    }

    public void deletePlaceById(String placeId) {
        repository.deletePlaceById(placeId);
    }

    public void toggleFavorite(String placeId) {
        repository.toggleFavorite(placeId);
    }

    public void incrementVisitCount(String placeId) {
        repository.incrementVisitCount(placeId);
    }

    public void insertMedia(PlaceMedia media) {
        repository.insertMedia(media);
    }

    public void deleteMedia(PlaceMedia media) {
        repository.deleteMedia(media);
    }

    public void insertNote(PlaceNote note) {
        repository.insertNote(note);
    }

    public void updateNote(PlaceNote note) {
        repository.updateNote(note);
    }

    public void deleteNote(PlaceNote note) {
        repository.deleteNote(note);
    }

    public void insertTrip(Trip trip) {
        repository.insertTrip(trip);
    }

    public void deleteTrip(Trip trip) {
        repository.deleteTrip(trip);
    }
}
