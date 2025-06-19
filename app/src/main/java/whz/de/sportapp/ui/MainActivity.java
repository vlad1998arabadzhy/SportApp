package whz.de.sportapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import whz.de.sportapp.R;
import whz.de.sportapp.ui.fragments.home.HomeFragment;
import whz.de.sportapp.ui.fragments.profile.ProfileFragment;
import whz.de.sportapp.ui.fragments.stat.StatsFragment;

public class MainActivity extends AppCompatActivity {

    private ImageButton homeButton;
    private ImageButton statsButton;
    private ImageButton profileButton;

    private ProfileFragment profileFragment;
    private HomeFragment homeFragment;
    private StatsFragment statsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        initializeFragments();
        setupClickListeners();

        showFragment(statsFragment);
        setActiveButton(homeButton);
    }

    private void initializeViews() {
        homeButton = findViewById(R.id.btn_home);
        statsButton = findViewById(R.id.btn_stats);
        profileButton = findViewById(R.id.btn_profile);
    }

    private void initializeFragments() {
        profileFragment = new ProfileFragment();
        homeFragment = new HomeFragment();
        statsFragment = new StatsFragment();
    }

    private void setupClickListeners() {
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment(homeFragment);
                setActiveButton(homeButton);
            }
        });

        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment(statsFragment);
                setActiveButton(statsButton);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment(profileFragment);
                setActiveButton(profileButton);
            }
        });
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void setActiveButton(ImageButton activeButton) {
        homeButton.setSelected(false);
        statsButton.setSelected(false);
        profileButton.setSelected(false);

        activeButton.setSelected(true);
    }
}