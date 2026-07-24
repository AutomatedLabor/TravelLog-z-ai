package com.example.travellog;

import android.app.Application;

import com.example.travellog.data.repository.PlaceRepository;

public class TravelLogApp extends Application {

    private static TravelLogApp instance;
    private PlaceRepository repository;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        repository = new PlaceRepository(this);
    }

    public static TravelLogApp getInstance() {
        return instance;
    }

    public PlaceRepository getRepository() {
        return repository;
    }
}
