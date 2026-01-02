package com.orbitex.sis;

import android.media.MediaCommunicationManager;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cloudinary.android.MediaManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.orbitex.sis.ui.fragments.DiscoverFragments;
import com.orbitex.sis.ui.fragments.MyEventsFragment;
import com.orbitex.sis.ui.fragments.ProfileFragments;
import com.orbitex.sis.ui.fragments.SettingsFragments;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private static final String TAG_DISCOVER = "fragment_discover";
    private static final String TAG_EVENTS = "fragment_my_events";
    private static final String TAG_PROFILE = "fragment_profile";
    private static final String TAG_SETTINGS = "fragment_settings";

    private ImageButton cEvent;

    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_main);


        bottomNav = findViewById(R.id.bottom_navigation);
        cEvent = findViewById(R.id.fab_center);

        if (savedInstanceState == null) {
            setupFragments();
        } else {
            restoreFragments();
        }

        setupNavigation();

        cEvent.setOnClickListener(v -> {
            NavigationUtils.go(MainActivity.this, CreateEventActivity.class, false);
        });
    }

    private void setupNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {

            FragmentManager fm = getSupportFragmentManager();

            int id = item.getItemId();

            if (id == R.id.nav_discover) {
                switchToFragment(fm.findFragmentByTag(TAG_DISCOVER));
                return true;
            }
            else if (id == R.id.nav_my_events) {
                switchToFragment(fm.findFragmentByTag(TAG_EVENTS));
                return true;
            }
            else if (id == R.id.nav_profile) {
                switchToFragment(fm.findFragmentByTag(TAG_PROFILE));
                return true;
            }
            else if (id == R.id.nav_settings) {
                switchToFragment(fm.findFragmentByTag(TAG_SETTINGS));
                return true;
            }

            return false;
        });
    }

    private void switchToFragment(Fragment target) {
        if (target == null || target == activeFragment) return;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.hide(activeFragment);
        ft.show(target);
        ft.commit();

        activeFragment = target;
    }

    private void restoreFragments() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fDiscover = fm.findFragmentByTag(TAG_DISCOVER);
        Fragment fEvents = fm.findFragmentByTag(TAG_EVENTS);
        Fragment fProfile = fm.findFragmentByTag(TAG_PROFILE);
        Fragment fSettings = fm.findFragmentByTag(TAG_SETTINGS);

        if (fEvents != null && !fEvents.isHidden()) activeFragment = fEvents;
        else if (fDiscover != null && !fDiscover.isHidden()) activeFragment = fDiscover;
        else if (fProfile != null && !fProfile.isHidden()) activeFragment = fProfile;
        else if (fSettings != null && !fSettings.isHidden()) activeFragment = fSettings;
    }

    private void setupFragments() {
        FragmentManager fm = getSupportFragmentManager();

        Fragment fDiscover = new DiscoverFragments();
        Fragment fEvents = new MyEventsFragment();
        Fragment fProfile= new ProfileFragments();
        Fragment fSettings = new SettingsFragments();

        fm.beginTransaction()
                .add(R.id.fragment_container, fDiscover, TAG_DISCOVER).hide(fDiscover)
                .add(R.id.fragment_container, fProfile, TAG_PROFILE).hide(fProfile)
                .add(R.id.fragment_container, fSettings, TAG_SETTINGS).hide(fSettings)
                .add(R.id.fragment_container, fEvents, TAG_EVENTS)
                .commit();

        activeFragment = fEvents;
    }
}
