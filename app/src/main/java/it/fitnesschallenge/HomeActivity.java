package it.fitnesschallenge;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity implements Home.OnFragmentInteractionListener {

    private static final String TAG = "HomeActivity";
    private Home mHomeFragment;
    private BottomNavigationView mBottomNavigation;
    private ImageButton mBackButton;
    private static final String HOME_FRAGMENT = "HomeFragment";
    private static final String PROFILE_FRAGMENT = "ProfileFragment";
    private static final String TIMER_FRAGMENT = "TimerFragment";
    private static final String SETTING_FRAGMENT = "SettingFragment";
    private static final String LAST_FRAGMENT = "LastFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mBottomNavigation = findViewById(R.id.bottom_navigation);
        mBackButton = findViewById(R.id.btn_back);
        mBackButton.setVisibility(View.GONE);

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

    @Override
    public void onFragmentInteraction(Uri uri) {
        //empty for now
    }
}
