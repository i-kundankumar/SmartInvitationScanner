package com.orbitex.sis;

import com.cloudinary.android.MediaManager;
import com.google.firebase.FirebaseApp;

public class MyApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        MediaManager.init(this);
    }
}
