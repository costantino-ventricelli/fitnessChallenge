package it.fitnesschallenge;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import it.fitnesschallenge.model.ExerciseTable;
import it.fitnesschallenge.model.HomeViewModel;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private Home mHomeFragment;
    private BottomNavigationView mBottomNavigation;
    private ImageButton mBackButton;
    private static Context mContext;
    private HomeViewModel homeViewModel;
    private static final String HOME_FRAGMENT = "HomeFragment";
    private static final String PROFILE_FRAGMENT = "ProfileFragment";
    private static final String TIMER_FRAGMENT = "TimerFragment";
    private static final String SETTING_FRAGMENT = "SettingFragment";
    private static final String LAST_FRAGMENT = "LastFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        homeViewModel.getAllExercise().observe(this, new Observer<List<ExerciseTable>>() {
            @Override
            public void onChanged(List<ExerciseTable> exerciseTables) {
                //necessario per il ViewModel
            }
        });
        mBottomNavigation = findViewById(R.id.bottom_navigation);
        mBackButton = findViewById(R.id.btn_back);
        mBackButton.setVisibility(View.GONE);
        mContext = getApplicationContext();


        if(savedInstanceState == null){
            mBottomNavigation.setSelectedItemId(R.id.navigation_home);
            mHomeFragment = new Home();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, mHomeFragment, HOME_FRAGMENT).commit();
        }else{
            String lastFragment = savedInstanceState.getString(LAST_FRAGMENT);
            setCurrentFragment(lastFragment);
        }
    }

    private void setCurrentFragment(String lastFragment){
        switch (lastFragment){
            case PROFILE_FRAGMENT:
                break;
            case TIMER_FRAGMENT:
                break;
            case SETTING_FRAGMENT:
                break;
            case HOME_FRAGMENT:
                break;
        }
    }

    public static Context getHomeActivityContext(){
        return mContext;
    }
}
