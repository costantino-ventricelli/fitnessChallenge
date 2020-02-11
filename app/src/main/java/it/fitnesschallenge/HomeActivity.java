package it.fitnesschallenge;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import it.fitnesschallenge.model.room.ExerciseTable;
import it.fitnesschallenge.model.view.HomeViewModel;

import static it.fitnesschallenge.model.SharedConstance.HOME_FRAGMENT;
import static it.fitnesschallenge.model.SharedConstance.LAST_FRAGMENT;


public class HomeActivity extends AppCompatActivity{

    private static final String TAG = "HomeActivity";
    private Home mHomeFragment;
    private BottomNavigationView mBottomNavigation;
    private static ImageButton mBackButton;
    private HomeViewModel homeViewModel;
    private static Context mContext;
    private boolean isHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        mBackButton = findViewById(R.id.btn_back);
        mBackButton.setVisibility(View.INVISIBLE);
        mContext = this;

        if (savedInstanceState == null) {
            mBottomNavigation.setSelectedItemId(R.id.navigation_home);
            mHomeFragment = new Home();
            isHome = true;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, mHomeFragment, HOME_FRAGMENT)
                    .addToBackStack(HOME_FRAGMENT)
                    .commit();
        } else {
            String lastFragment = savedInstanceState.getString(LAST_FRAGMENT);
            setCurrentFragment(lastFragment);
        }

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().popBackStackImmediate();
                try {
                    Log.d(TAG, "back button pressed");
                    setCurrentFragment(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer).getTag());
                } catch (NullPointerException ex) {
                    Toast.makeText(mContext, mContext.getString(R.string.shit_error), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static void setCurrentFragment(String currentFragment) {
        Log.d(TAG, "SetCurrentFragment on: " + currentFragment);
        if (HOME_FRAGMENT.equals(currentFragment)) {
            mBackButton.setVisibility(View.INVISIBLE);
        } else {
            mBackButton.setVisibility(View.VISIBLE);
        }
    }

    public static Context getHomeActivityContext() {
        return mContext;
    }
}
