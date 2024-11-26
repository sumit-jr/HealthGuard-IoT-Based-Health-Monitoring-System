package com.example.stressmanagement;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.nafis.bottomnavigation.NafisBottomNavigation;

public class MainActivity extends AppCompatActivity {

    private static final int ID_HOME = 1;
    private static final int ID_SUGGESTION = 2;
    private static final int ID_MESSAGE = 3;
    private static final int ID_PROFILE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NafisBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);

        // Adding menu items
        bottomNavigation.add(new NafisBottomNavigation.Model(ID_HOME, R.drawable.baseline_home_24));
        bottomNavigation.add(new NafisBottomNavigation.Model(ID_SUGGESTION, R.drawable.baseline_check_box_24));
        bottomNavigation.add(new NafisBottomNavigation.Model(ID_MESSAGE, R.drawable.baseline_announcement_24));
        bottomNavigation.add(new NafisBottomNavigation.Model(ID_PROFILE, R.drawable.round_people_24));

        // Show Home by default
        bottomNavigation.show(ID_HOME , true);
        loadFragment(new HomeFragment());



        // Listener when a cell is clicked
        bottomNavigation.setOnClickMenuListener(model -> {
            Fragment fra = null;
            switch (model.getId()) {
                case ID_HOME:
                   fra = new HomeFragment();
                    // Handle Home Fragment
                    break;
                case ID_SUGGESTION:
                    fra = new ClinicFragment();
                    // Handle Explore Fragment
                    break;
                case ID_MESSAGE:
                    fra = new EmergencyFragment();
                    // Handle Explore Fragment
                    break;
                case ID_PROFILE:
                    fra = new ProfileFragment();
                    // Handle Message Fragment
                    break;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fra)
                    .commit();
            return null;
        });
    }
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // Make sure fragment_container exists in XML layout
                .commit();
    }
}
